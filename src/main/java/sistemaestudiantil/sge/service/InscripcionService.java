package sistemaestudiantil.sge.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;

import sistemaestudiantil.sge.dto.AvanceAcademicoDTO;
import sistemaestudiantil.sge.dto.CicloDetalleDTO;
import sistemaestudiantil.sge.dto.EstudianteDTO;
import sistemaestudiantil.sge.dto.EvaluacionDTO;
import sistemaestudiantil.sge.dto.HistorialDTO;
import sistemaestudiantil.sge.dto.InscripcionDTO;
import sistemaestudiantil.sge.dto.MateriaKardexDTO;
import sistemaestudiantil.sge.enums.EstadoEstudiante;
import sistemaestudiantil.sge.enums.EstadoInscripcion;
import sistemaestudiantil.sge.enums.Roles;
import sistemaestudiantil.sge.exceptions.DuplicadoException;
import sistemaestudiantil.sge.exceptions.OperacionNoPermitidaException;
import sistemaestudiantil.sge.exceptions.RecursoNoencontradoException;
import sistemaestudiantil.sge.mapper.EstudianteMapper;
import sistemaestudiantil.sge.mapper.InscripcionMapper;
import sistemaestudiantil.sge.model.Asignatura;
import sistemaestudiantil.sge.model.Ciclo;
import sistemaestudiantil.sge.model.Estudiante;
import sistemaestudiantil.sge.model.Grupo;
import sistemaestudiantil.sge.model.Inscripcion;
import sistemaestudiantil.sge.repository.CicloRepository;
import sistemaestudiantil.sge.repository.EstudianteRepository;
import sistemaestudiantil.sge.repository.EvaluacionRepository;
import sistemaestudiantil.sge.repository.GrupoRespository;
import sistemaestudiantil.sge.repository.InscripcionRepository;

@Service
@AllArgsConstructor
public class InscripcionService {

    private final GrupoRespository grupoRespository;
    private final EstudianteRepository estudianteRepository;
    private final InscripcionRepository inscripcionRepository;
    private final InscripcionMapper inscripcionMapper;
    private final EvaluacionRepository evaluacionRepository;
    private final CicloRepository cicloRepository;
    private final EstudianteMapper estudianteMapper;
    private final PasswordEncoder passwordEncoder;
    private final PagoService pagoService;

    @Transactional
    public InscripcionDTO inscribir(InscripcionDTO dto){
        Estudiante estudiante = estudianteRepository.findById(dto.getIdEstudiante())
                .orElseThrow(() -> new RecursoNoencontradoException("Estudiante no encontrado con el ID: " + dto.getIdEstudiante()));
        
        Grupo grupo = grupoRespository.findById(dto.getIdGrupo())
                .orElseThrow(() -> new RecursoNoencontradoException("El grupo no se encontró con ID: " + dto.getIdGrupo()));

        if (!estudiante.estaActivo()) {
            throw new OperacionNoPermitidaException("El estudiante no está activo. Su estado actual es: " + estudiante.getEstado());
        }

        if (grupo.getCuposDisponibles() <= 0) {
            throw new OperacionNoPermitidaException("El grupo ya no tiene cupos disponibles.");
        }

        if (estudiante.getRol().equals(Roles.ROLE_ADMINISTRATIVO) || estudiante.getRol().equals(Roles.ROLE_ADMIN)||estudiante.getRol().equals(Roles.ROLE_PROFESOR)) {
            throw new OperacionNoPermitidaException("Los Administradores o Docentes no pueden inscribir materias.");
        }

        Long idAsignatura = grupo.getAsignatura().getIdAsignatura();
        Ciclo cicloActual = grupo.getCiclo();

        boolean yaTieneLaMateria = inscripcionRepository.yaEstaInscritoEnLaMateria(estudiante.getIdEstudiante(), idAsignatura, cicloActual);

        if(yaTieneLaMateria){
            throw new DuplicadoException("El estudiante ya tiene inscrita la materia '" + grupo.getAsignatura().getName() + "' en el ciclo " + cicloActual + ". No se permite doble inscripción.");
        }

        boolean yaInscrito = inscripcionRepository.existsByEstudianteAndGrupo(estudiante, grupo);
        if(yaInscrito){
            throw new DuplicadoException("El estudiante " + dto.getIdEstudiante() + " ya se encuentra inscrito en este grupo.");
        }

        Asignatura asignaturaObjetivo = grupo.getAsignatura();

        boolean yaLaAprobo = inscripcionRepository.haAprobadoMateria(
            estudiante.getIdEstudiante(), 
            idAsignatura,
            EstadoInscripcion.APROBADO
        );

        if (yaLaAprobo) {
            throw new OperacionNoPermitidaException("¡Ya aprobaste esta materia anteriormente! No puedes volver a cursarla.");
        }

        if(asignaturaObjetivo.getPrerrequisitos() != null && !asignaturaObjetivo.getPrerrequisitos().isEmpty()){
            for(Asignatura requisito : asignaturaObjetivo.getPrerrequisitos()){
                boolean aprobado = inscripcionRepository.haAprobadoMateria(
                        estudiante.getIdEstudiante(), 
                        requisito.getIdAsignatura(), 
                        EstadoInscripcion.APROBADO
                ); 

                if(!aprobado){
                    throw new OperacionNoPermitidaException("No cumple con los prerrequisitos. Se debe aprobar " + requisito.getName() + " para inscribir " + asignaturaObjetivo.getName());
                }
            }
        }

        List<Inscripcion> conflictos = inscripcionRepository.encontrarChoquesHorarioEstudiante(
            estudiante.getIdEstudiante(),
            grupo.getCiclo(),
            grupo.getDias(),
            grupo.getHoraInicio(),
            grupo.getHoraFin()
        );

        if (!conflictos.isEmpty()) {
            Inscripcion conflicto = conflictos.get(0);
            String nombreMateriaConflicto = conflicto.getGrupo().getAsignatura().getName();
            String horarioConflicto = conflicto.getGrupo().getHoraInicio() + " - " + conflicto.getGrupo().getHoraFin();

            throw new OperacionNoPermitidaException(
                "CONFLICTO DE HORARIO: No puedes inscribir " + grupo.getAsignatura().getName() + 
                " porque choca con tu clase de " + nombreMateriaConflicto + 
                " (" + horarioConflicto + ")."
            );
        }

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setEstudiante(estudiante);
        inscripcion.setGrupo(grupo);
        inscripcion.setFechaInscripcion(LocalDate.now());
        inscripcion.setEstadoInscripcion(EstadoInscripcion.INSCRITO);
        inscripcion.setNotaFinal(0.0);

        grupo.setCuposDisponibles(grupo.getCuposDisponibles() - 1);
        grupoRespository.save(grupo);

        Inscripcion guardado = inscripcionRepository.save(inscripcion);
        return inscripcionMapper.toDTO(guardado);
    }

    public List<InscripcionDTO> listarInscripciones(){
        return inscripcionRepository.findAll().stream().map(inscripcionMapper::toDTO).toList();
    }

    @Transactional(readOnly=true)
    public List<HistorialDTO> obtenerHistorialEstudiante(Long idEstudiante){
        List<Inscripcion> inscripciones = inscripcionRepository.findHistorialCompleto(idEstudiante);
        return inscripciones.stream().map(inscripcion -> {
            HistorialDTO dto = new HistorialDTO();
            dto.setNombreMateria(inscripcion.getGrupo().getAsignatura().getName());
            dto.setCiclo(inscripcion.getGrupo().getCiclo().getNombre());
            dto.setEstado(inscripcion.getEstadoInscripcion().name());
            dto.setNotaFinal(inscripcion.getNotaFinal());

            List<EvaluacionDTO> evaluacionesDTO = inscripcion.getEvaluaciones().stream().map(e -> {
                EvaluacionDTO evaluacionDTO = new EvaluacionDTO();
                evaluacionDTO.setNombreActividad(e.getNombreActividad());
                evaluacionDTO.setPorcentaje(e.getPorcentaje());
                evaluacionDTO.setNotaObtenida(e.getNotaObtenida());
                return evaluacionDTO;
            }).toList();

            dto.setEvaluaciones(evaluacionesDTO);

            return dto;
        }).toList();
    }

    @Transactional
    public InscripcionDTO cambioGrupo(InscripcionDTO dto) {
        Estudiante estudiante = estudianteRepository.findById(dto.getIdEstudiante()).orElseThrow(() -> new RecursoNoencontradoException("Estudiante indicado no ha sido encontrado"));

        if (!estudiante.estaActivo()) {
            throw new OperacionNoPermitidaException("El estudiante no está activo.");
        }

        Grupo nuevoGrupo = grupoRespository.findById(dto.getIdGrupo()).orElseThrow(() -> new RecursoNoencontradoException("Grupo destino no encontrado"));

        if (nuevoGrupo.getCuposDisponibles() <= 0) {
            throw new OperacionNoPermitidaException("El grupo destino ya no tiene cupos disponibles.");
        }

        Inscripcion inscripcionActual = inscripcionRepository.findInscripcionActiva(
                estudiante.getIdEstudiante(),
                nuevoGrupo.getAsignatura().getIdAsignatura(),
                nuevoGrupo.getCiclo()
        ).orElseThrow(() -> new RecursoNoencontradoException("El estudiante no está inscrito en esta materia, por lo tanto no se puede cambiar de grupo."));

        Grupo grupoAnterior = inscripcionActual.getGrupo();
        if (grupoAnterior.getIdGrupo().equals(nuevoGrupo.getIdGrupo())) {
            throw new DuplicadoException("El estudiante ya está en el grupo " + nuevoGrupo.getCodigoGrupo());
        }

        grupoAnterior.setCuposDisponibles(grupoAnterior.getCuposDisponibles() + 1);
        grupoRespository.save(grupoAnterior);

        nuevoGrupo.setCuposDisponibles(nuevoGrupo.getCuposDisponibles() - 1);
        grupoRespository.save(nuevoGrupo);

        inscripcionActual.setGrupo(nuevoGrupo);

        Inscripcion guardado = inscripcionRepository.save(inscripcionActual);
        
        return inscripcionMapper.toDTO(guardado);
    }

    @Transactional
    public String cerrarCiclo(Long idCiclo) {
        Ciclo ciclo = cicloRepository.findById(idCiclo).orElseThrow(() -> new RecursoNoencontradoException("El ciclo " + idCiclo + " no ha sido encontrado"));

        inscripcionRepository.normalizarNotasNulas(idCiclo);

        int aprobados = inscripcionRepository.marcarAprobados(idCiclo, EstadoInscripcion.APROBADO);
        int reprobados = inscripcionRepository.marcarReprobados(idCiclo, EstadoInscripcion.REPROBADO);

        if (Boolean.TRUE.equals(ciclo.getActivo())) {
            ciclo.setActivo(false);
            cicloRepository.save(ciclo);
        }

        return "Cierre del " + ciclo.getNombre() + " completado." + aprobados + " aprobados, " + reprobados + " reprobados.";
    }

    @Transactional
    public InscripcionDTO retirarMateria(Long idInscripcion, Long idEstudianteSolicitante) {

        Inscripcion inscripcion = inscripcionRepository.findById(idInscripcion).orElseThrow(() -> new RecursoNoencontradoException("Inscripción no encontrada."));

        if (!inscripcion.getEstudiante().getIdEstudiante().equals(idEstudianteSolicitante)) {
             throw new OperacionNoPermitidaException("No puede retirar una materia que no le pertenece.");
        }

        if (inscripcion.getEstadoInscripcion() != EstadoInscripcion.INSCRITO) {
            throw new OperacionNoPermitidaException("Solo se pueden retirar materias en estado 'INSCRITO'. El estado actual es: " + inscripcion.getEstadoInscripcion());
        }

        Double porcentajeEvaluado = evaluacionRepository.obtenerPorcentajeAcumulado(idInscripcion);

        if (porcentajeEvaluado == null) porcentajeEvaluado = 0.0;

        if (porcentajeEvaluado > 50.0) {
            throw new OperacionNoPermitidaException(
                "Ya no es posible retirar la materia. Se ha evaluado el " + porcentajeEvaluado + "% del curso (Límite permitido: 50%)."
            );
        }

        inscripcion.setEstadoInscripcion(EstadoInscripcion.RETIRADO);
        inscripcion.setNotaFinal(0.0);

        Inscripcion guardada = inscripcionRepository.save(inscripcion);
        return inscripcionMapper.toDTO(guardada);
    }

    public AvanceAcademicoDTO obtenerKardex(Long idEstudiante) {

        Estudiante estudiante = estudianteRepository.findById(idEstudiante).orElseThrow(() -> new RecursoNoencontradoException("Estudiante no encontrado"));
        
        List<Inscripcion> historialCompleto = inscripcionRepository.findHistorialCompleto(idEstudiante);

        long cantidadAprobadas = historialCompleto.stream().filter(i -> i.getEstadoInscripcion() == EstadoInscripcion.APROBADO).count();

        int totalMateriasCarrera = 0;
        if (estudiante.getCarrera() != null && estudiante.getCarrera().getNumeroAsignaturas() != null) {
            totalMateriasCarrera = estudiante.getCarrera().getNumeroAsignaturas();
        }
        if (totalMateriasCarrera == 0) totalMateriasCarrera = 1; 

        double porcentaje = ((double) cantidadAprobadas / totalMateriasCarrera) * 100.0;

        double sumaNotas = 0;
        int contNotas = 0;
        for (Inscripcion i : historialCompleto) {
            if (i.getEstadoInscripcion() == EstadoInscripcion.APROBADO ||i.getEstadoInscripcion() == EstadoInscripcion.REPROBADO) {
                sumaNotas += i.getNotaFinal();
                contNotas++;
            }
        }
        double cum = (contNotas > 0) ? (sumaNotas / contNotas) : 0.0;

        Map<String, List<Inscripcion>> materiasPorCiclo = historialCompleto.stream().collect(Collectors.groupingBy(ins -> ins.getGrupo().getCiclo().getNombre()));

        List<CicloDetalleDTO> listaCiclos = new ArrayList<>();

        materiasPorCiclo.forEach((nombreCiclo, inscripciones) -> {

            List<MateriaKardexDTO> materiasDTO = inscripciones.stream().map(i -> new MateriaKardexDTO(
                    i.getGrupo().getAsignatura().getCodigo(),
                    i.getGrupo().getAsignatura().getName(),
                    i.getGrupo().getAsignatura().getUv(),
                    i.getNotaFinal(),
                    i.getEstadoInscripcion().toString(),
                    1 // Aquí se podria calcular las inscripciones en segunda o tercera
            )).toList();

            listaCiclos.add(new CicloDetalleDTO(nombreCiclo, materiasDTO));
        });

        AvanceAcademicoDTO respuesta = new AvanceAcademicoDTO();
        respuesta.setNombreEstudiante(estudiante.getNombres() + " " + estudiante.getApellidos());
        respuesta.setCarnet(estudiante.getCarnet());
        respuesta.setNombreCarrera(estudiante.getCarrera() != null ? estudiante.getCarrera().getNombreCarrera() : "Sin Carrera");
        respuesta.setMateriasAprobadas((int) cantidadAprobadas);
        respuesta.setTotalMateriasPlan(totalMateriasCarrera);
        respuesta.setPorcentajeAvance(Math.round(porcentaje * 100.0) / 100.0);
        respuesta.setCum(Math.round(cum * 100.0) / 100.0);
        respuesta.setCiclos(listaCiclos);

        return respuesta;
    }

    @Transactional
    public List<EstudianteDTO> cerrarCicloAdmision(Integer anioIngreso) {
        
        List<Estudiante> seleccionados = estudianteRepository.findByEstadoOrderByApellidosAsc(EstadoEstudiante.SELECCIONADO);

        if (seleccionados.isEmpty()) {
            throw new RecursoNoencontradoException("No hay estudiantes seleccionados para procesar.");
        }

        Map<String, Integer> contadoresPorPrefijo = new HashMap<>();
        String anioDosDigitos = String.valueOf(anioIngreso).substring(2);

        for (Estudiante estudiante : seleccionados) {
            
            String prefijoLetras = obtenerIniciales(estudiante.getApellidos());
            String prefijoCompleto = prefijoLetras + anioDosDigitos;

            if (!contadoresPorPrefijo.containsKey(prefijoCompleto)) {
                
                Optional<String> ultimoCarnetBD = estudianteRepository.findUltimoCarnet(prefijoCompleto);
                
                if (ultimoCarnetBD.isPresent()) {
                    String ultimoCarnet = ultimoCarnetBD.get();
                    String numeroStr = ultimoCarnet.substring(4);
                    int ultimoNumero = Integer.parseInt(numeroStr);
                    contadoresPorPrefijo.put(prefijoCompleto, ultimoNumero + 1);
                } else {
                    contadoresPorPrefijo.put(prefijoCompleto, 1);
                }
            }

            int correlativoActual = contadoresPorPrefijo.get(prefijoCompleto);

            String carnetGenerado = prefijoCompleto + String.format("%03d", correlativoActual);
            
            estudiante.setCarnet(carnetGenerado);
            estudiante.setEstado(EstadoEstudiante.ESTUDIANTE);
            estudiante.setEstaActivo(true);

            String claveTemporal = carnetGenerado + "." + anioIngreso;
            estudiante.setContrasenia(passwordEncoder.encode(claveTemporal));
            estudiante.setDebeCambiarClave(true); 

            contadoresPorPrefijo.put(prefijoCompleto, correlativoActual + 1);

            pagoService.generarTalonarioAnual(estudiante, anioIngreso);
        }

        List<Estudiante> guardados = estudianteRepository.saveAll(seleccionados);

        return guardados.stream().map(estudianteMapper::toDTO).toList();
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
        Estudiante estudiante = estudianteRepository.findById(idEstudiante)
                .orElseThrow(() -> new RecursoNoencontradoException("Estudiante no encontrado"));

        if (estudiante.getEstado() != EstadoEstudiante.SELECCIONADO) {
            throw new IllegalStateException("El estudiante debe estar SELECCIONADO para formalizar su inscripción.");
        }

        String nuevoCarnet = generarCarnet(estudiante);
        estudiante.setCarnet(nuevoCarnet);

        estudiante.setEstado(EstadoEstudiante.ESTUDIANTE);
        estudiante.setEstaActivo(true);

        Estudiante guardado = estudianteRepository.save(estudiante);

        int anioLectivo = LocalDate.now().getYear(); 
        pagoService.generarTalonarioAnual(guardado, anioLectivo);

        return estudianteMapper.toDTO(guardado);
    }

    private String generarCarnet(Estudiante estudiante) {
        String[] partesApellido = estudiante.getApellidos().trim().split("\\s+");
        
        char letra1 = partesApellido[0].toUpperCase().charAt(0);
        char letra2 = (partesApellido.length > 1) 
                        ? partesApellido[1].toUpperCase().charAt(0)
                        : partesApellido[0].toUpperCase().charAt(0);

        String anio = String.valueOf(LocalDate.now().getYear()).substring(2);

        String prefijo = "" + letra1 + letra2 + anio;

        Optional<String> ultimoCarnet = estudianteRepository.findUltimoCarnet(prefijo);

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