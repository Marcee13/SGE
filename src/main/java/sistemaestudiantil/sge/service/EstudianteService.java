package sistemaestudiantil.sge.service;

import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import sistemaestudiantil.sge.dto.EstudianteDTO;
import sistemaestudiantil.sge.enums.EstadoEstudiante;
import sistemaestudiantil.sge.enums.Roles;
import sistemaestudiantil.sge.enums.TipoArancel;
import sistemaestudiantil.sge.exceptions.DuplicadoException;
import sistemaestudiantil.sge.mapper.EstudianteMapper;
import sistemaestudiantil.sge.model.Carrera;
import sistemaestudiantil.sge.model.Estudiante;
import sistemaestudiantil.sge.repository.CarreraRepository;
import sistemaestudiantil.sge.repository.EstudianteRepository;

@Service
public class EstudianteService {
    private final EstudianteRepository repository;
    private final EstudianteMapper mapper;
    private final CarreraRepository carreraRepository;
    private final PagoService pagoService;
    private final PasswordEncoder passwordEncoder;

    public EstudianteService(EstudianteRepository repository, PasswordEncoder passwordEncoder, EstudianteMapper mapper, CarreraRepository carreraRepository, PagoService pagoService){
        this.repository=repository;
        this.passwordEncoder=passwordEncoder;
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

    public EstudianteDTO buscarPorCarnetOrEmail(String identificador) {
        Estudiante estudiante = repository.findByCarnetOrEmail(identificador, identificador).orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado con carnet o email: " + identificador));
        return mapper.toDTO(estudiante);
    }
}