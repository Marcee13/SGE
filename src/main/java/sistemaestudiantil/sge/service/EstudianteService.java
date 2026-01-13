package sistemaestudiantil.sge.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import sistemaestudiantil.sge.dto.EstudianteDTO;
import sistemaestudiantil.sge.enums.EstadoEstudiante;
import sistemaestudiantil.sge.enums.EstadoPago;
import sistemaestudiantil.sge.enums.Roles;
import sistemaestudiantil.sge.enums.TipoArancel;
import sistemaestudiantil.sge.enums.TipoExamenAdmision;
import sistemaestudiantil.sge.exceptions.DuplicadoException;
import sistemaestudiantil.sge.exceptions.OperacionNoPermitidaException;
import sistemaestudiantil.sge.exceptions.RecursoNoencontradoException;
import sistemaestudiantil.sge.mapper.EstudianteMapper;
import sistemaestudiantil.sge.model.Carrera;
import sistemaestudiantil.sge.model.Estudiante;
import sistemaestudiantil.sge.repository.CarreraRepository;
import sistemaestudiantil.sge.repository.EstudianteRepository;
import sistemaestudiantil.sge.repository.PagoRepository;

@Service
public class EstudianteService {
    private final EstudianteRepository repository;
    private final EstudianteMapper mapper;
    private final CarreraRepository carreraRepository;
    private final PagoService pagoService;
    private final PagoRepository pagoRepository;
    private final PasswordEncoder passwordEncoder;

    public EstudianteService(EstudianteRepository repository, PasswordEncoder passwordEncoder, PagoRepository pagoRepository, EstudianteMapper mapper, CarreraRepository carreraRepository, PagoService pagoService){
        this.repository=repository;
        this.passwordEncoder=passwordEncoder;
        this.pagoRepository=pagoRepository;
        this.mapper=mapper;
        this.carreraRepository=carreraRepository;
        this.pagoService=pagoService;
    }

    public EstudianteDTO crearEstudiante(EstudianteDTO dto){

        validarUnico(dto);

        String numeroLimpio = dto.getNumeroDocumento() != null ? dto.getNumeroDocumento().trim() : null;
        if(dto.getDocumento()!=null&&numeroLimpio!=null){
            dto.getDocumento().validar(numeroLimpio);
        }
        
        Estudiante entidad = mapper.toEntity(dto);
        entidad.setNumeroDocumento(numeroLimpio);
        entidad.setCarnet(null);
        entidad.setEstado(EstadoEstudiante.ASPIRANTE);
        entidad.setRol(Roles.ROLE_ESTUDIANTE);
        if (dto.getContrasenia() != null && !dto.getContrasenia().isEmpty()) {
            entidad.setContrasenia(passwordEncoder.encode(dto.getContrasenia()));
        }
        
        Estudiante guardado= repository.save(entidad);
        pagoService.generarPagoExamen(guardado, TipoArancel.EXAMEN_ADMISION);

        return mapper.toDTO(guardado);
    }

    public List<EstudianteDTO> obtenerTodos() {
        List<Estudiante> lista = repository.findAll();
        return lista.stream().map(mapper::toDTO).toList();
    }

    public void validarUnico(EstudianteDTO dto){
         if(repository.existsByEmail(dto.getEmail())){
            throw new DuplicadoException("Ya existe un estudiante registrado con el correo " + dto.getEmail()+", por favor revise.");
        }

        if(repository.existsByNumeroDocumento(dto.getNumeroDocumento())){
            throw new DuplicadoException("Ya se encuentra un registro con el número de documento: " + dto.getNumeroDocumento());
        }
    }

    public EstudianteDTO calificarAspirante(Long idEstudiante, Double notaExamen, TipoExamenAdmision tipoExamen){
        if(notaExamen<0||notaExamen>100){
            throw new IllegalArgumentException("La nota del examen de admisión debe estar entre 0 y 100 puntos");
        }
        
        Estudiante estudiante=repository.findById(idEstudiante).orElseThrow(()->new RecursoNoencontradoException("Aspirante con ID "+idEstudiante+" no encontrado."));

        String codigoArancelRequerido = (tipoExamen == TipoExamenAdmision.GENERAL) ? "EX-ADM" : "EX-CON";
        
        boolean estaSolvente = pagoRepository.existsByEstudiante_IdEstudianteAndArancel_CodigoAndEstado(
                idEstudiante, 
                codigoArancelRequerido, 
                EstadoPago.PAGADO 
        );

        if (!estaSolvente) {
            throw new OperacionNoPermitidaException(
                "No se puede registrar la nota. El aspirante no ha realizado el pago correspondiente al examen: " + codigoArancelRequerido
            );
        }

        if(tipoExamen==TipoExamenAdmision.GENERAL){
            evaluarExamenGeneral(estudiante,notaExamen);
        }else{
            evaluarExamenEspecifico(estudiante,notaExamen);
        }

        return mapper.toDTO(repository.save(estudiante));
    }

    private void evaluarExamenGeneral(Estudiante estudiante, Double nota){
        if(estudiante.getNotaExamenGeneral()!=null){
            throw new IllegalStateException("El estudiante ya realizo el exámen general con nota: "+estudiante.getNotaExamenGeneral()+" no se puede modificar");
        }

        estudiante.setNotaExamenGeneral(nota);

        if (nota >= 50) {
            
            if (esCarreraEducacion(estudiante)) {
                estudiante.setEstado(EstadoEstudiante.CONDICIONADO);
            } else {
                estudiante.setEstado(EstadoEstudiante.SELECCIONADO);
            }

        } else if (nota >= 30) {
            estudiante.setEstado(EstadoEstudiante.ASPIRANTE_FASE_2);
            pagoService.generarPagoExamen(estudiante, TipoArancel.EXAMEN_CONOCIMIENTOS);
            
        } else {
            estudiante.setEstado(EstadoEstudiante.REPROBADO);
        }
    }

    private boolean esCarreraEducacion(Estudiante estudiante){
        return estudiante.getCarrera()!=null && Boolean.TRUE.equals(estudiante.getCarrera().getEsCarreraEducacion());
    }

    private void evaluarExamenEspecifico(Estudiante estudiante,Double nota){
        if(estudiante.getNotaExamenGeneral()==null){
            throw new IllegalStateException("El aspirante primero debe realizar el examen de conocimientos generales.");
        }

        if(estudiante.getEstado()!=EstadoEstudiante.ASPIRANTE_FASE_2){
            throw new IllegalStateException("El aspirante ya aprobó el examen de conocimiento generales. No debe realizar la prueba especifica.");
        }

        if(estudiante.getNotaExamenEspecifico()!=null){
            throw new IllegalStateException("El estudiante ya tiene una nota de prueba específica.");
        }

        estudiante.setNotaExamenEspecifico(nota);

        if(nota>=50){
            estudiante.setEstado(EstadoEstudiante.SELECCIONADO);
        }else{
            estudiante.setEstado(EstadoEstudiante.REPROBADO);
        }
    }

    public EstudianteDTO actualizarEstudiante(Long idEstudiante, EstudianteDTO dto){
        Estudiante estudiante= repository.findById(idEstudiante).orElseThrow(()->new EntityNotFoundException("Estudiante no encontrado con ID: "+idEstudiante));

        if (dto.getEmail() != null) {
            dto.setEmail(dto.getEmail().trim());
        }

        if(dto.getIdCarrera()!=null){
            Carrera carrera=carreraRepository.findById(dto.getIdCarrera()).orElseThrow(()-> new EntityNotFoundException("Carrera no encontrada con ID: "+dto.getIdCarrera()));
            estudiante.setCarrera(carrera);
        }

        validarUnicoParaActualizar(idEstudiante,dto);
        
        String numeroLimpio = dto.getNumeroDocumento() != null ? dto.getNumeroDocumento().trim() : null;
        if(dto.getDocumento()!=null&&numeroLimpio!=null){
            dto.getDocumento().validar(numeroLimpio);
        }

        if (dto.getNombres() != null) {
            estudiante.setNombres(dto.getNombres());
        }
        if (dto.getApellidos() != null) {
            estudiante.setApellidos(dto.getApellidos());
        }
        if (dto.getFechaNacimiento() != null) {
            estudiante.setFechaNacimiento(dto.getFechaNacimiento());
        }
        if (dto.getGenero() != null) {
            estudiante.setGenero(dto.getGenero());
        }
        if (dto.getSexo() != null) {
            estudiante.setSexo(dto.getSexo());
        }
        if (dto.getNumeroTelefonico() != null) {
            estudiante.setNumeroTelefonico(dto.getNumeroTelefonico());
        }
        if (dto.getEmail() != null) {
            estudiante.setEmail(dto.getEmail());
        }
        if (dto.getDocumento() != null) {
            estudiante.setDocumento(dto.getDocumento());
        }
        if (dto.getNumeroDocumento() != null) {
            estudiante.setNumeroDocumento(dto.getNumeroDocumento());
        }
        if(dto.getPaisResidencia()!=null){
            estudiante.setPaisResidencia(dto.getPaisResidencia());
        }
        
        Estudiante guardado = repository.save(estudiante);
        return mapper.toDTO(guardado);
    }

    public void validarUnicoParaActualizar(Long idActual, EstudianteDTO dto){
        if(dto.getEmail()!=null){
            repository.findByEmail(dto.getEmail()).ifPresent(estudianteEncontrado->{
                if(!estudianteEncontrado.getIdEstudiante().equals(idActual)){
                    throw new DuplicadoException("El correo: "+dto.getEmail()+" ya esta siendo utilizado.");
                }
            });
        }

        if(dto.getNumeroDocumento()!=null){
            repository.findByNumeroDocumento(dto.getNumeroDocumento()).ifPresent(estudianteEncontrado->{
                if(!estudianteEncontrado.getIdEstudiante().equals(idActual)){
                    throw new DuplicadoException("El número de documento: "+dto.getNumeroDocumento()+" ya pertenece a otro estudiante.");
                }
            });
        }
    }

    public List<EstudianteDTO> listarEstudiantesPorFacultad(Long idFacultad){
        List<Estudiante> estudiantes=repository.findByCarrera_Facultad_IdFacultad(idFacultad);
        return estudiantes.stream().map(mapper::toDTO).toList();
    }

    @Transactional
    public List<EstudianteDTO> cerrarCicloAdmision(Integer anioIngreso) {
        
        List<Estudiante> seleccionados = repository.findByEstadoOrderByApellidosAsc(EstadoEstudiante.SELECCIONADO);

        if (seleccionados.isEmpty()) {
            throw new RecursoNoencontradoException("No hay estudiantes seleccionados para procesar.");
        }

        Map<String, Integer> contadoresPorPrefijo = new HashMap<>();
        String anioDosDigitos = String.valueOf(anioIngreso).substring(2);

        for (Estudiante estudiante : seleccionados) {
            
            String prefijoLetras = obtenerIniciales(estudiante.getApellidos());
            String prefijoCompleto = prefijoLetras + anioDosDigitos;
            int correlativoActual = contadoresPorPrefijo.getOrDefault(prefijoCompleto, 1);
            
            String carnetGenerado = prefijoCompleto + String.format("%03d", correlativoActual);
            
            estudiante.setCarnet(carnetGenerado);
            estudiante.setEstado(EstadoEstudiante.ESTUDIANTE);
            estudiante.setEstaActivo(true);

            String claveTemporal = carnetGenerado + "." + anioIngreso;
            estudiante.setContrasenia(claveTemporal);
            estudiante.setDebeCambiarClave(true); 

            contadoresPorPrefijo.put(prefijoCompleto, correlativoActual + 1);

            pagoService.generarTalonarioAnual(estudiante, anioIngreso);
        }

        List<Estudiante> guardados = repository.saveAll(seleccionados);

        return guardados.stream().map(mapper::toDTO).toList();
    }

    private String obtenerIniciales(String apellidos) {
        if (apellidos == null || apellidos.isEmpty()) return "XX";
        
        String[] partes = apellidos.trim().split("\\s+");
        char l1 = partes[0].toUpperCase().charAt(0);
        char l2 = (partes.length > 1) ? partes[1].toUpperCase().charAt(0) : l1;
        
        return "" + l1 + l2;
    }

    @Transactional
    public EstudianteDTO formalizarInscripcion(Long idEstudiante) {
        Estudiante estudiante = repository.findById(idEstudiante)
                .orElseThrow(() -> new RecursoNoencontradoException("Estudiante no encontrado"));

        if (estudiante.getEstado() != EstadoEstudiante.SELECCIONADO) {
            throw new IllegalStateException("El estudiante debe estar SELECCIONADO para formalizar su inscripción.");
        }

        String nuevoCarnet = generarCarnet(estudiante);
        estudiante.setCarnet(nuevoCarnet);

        estudiante.setEstado(EstadoEstudiante.ESTUDIANTE);
        estudiante.setEstaActivo(true);

        Estudiante guardado = repository.save(estudiante);

        int anioLectivo = LocalDate.now().getYear(); 
        pagoService.generarTalonarioAnual(guardado, anioLectivo);

        return mapper.toDTO(guardado);
    }

    private String generarCarnet(Estudiante estudiante) {
        String[] partesApellido = estudiante.getApellidos().trim().split("\\s+");
        
        char letra1 = partesApellido[0].toUpperCase().charAt(0);
        char letra2 = (partesApellido.length > 1) 
                        ? partesApellido[1].toUpperCase().charAt(0)
                        : partesApellido[0].toUpperCase().charAt(0);

        String anio = String.valueOf(LocalDate.now().getYear()).substring(2);

        String prefijo = "" + letra1 + letra2 + anio;

        Optional<String> ultimoCarnet = repository.findUltimoCarnet(prefijo);

        int secuencia = 1;
        
        if (ultimoCarnet.isPresent()) {
            String codigo = ultimoCarnet.get();
            String correlativoStr = codigo.substring(4);
            secuencia = Integer.parseInt(correlativoStr) + 1;
        }

        String correlativoFinal = String.format("%03d", secuencia);

        return prefijo + correlativoFinal;
    }
}
