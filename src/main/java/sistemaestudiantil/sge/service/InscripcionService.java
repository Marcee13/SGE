package sistemaestudiantil.sge.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sistemaestudiantil.sge.dto.EvaluacionDTO;
import sistemaestudiantil.sge.dto.HistorialDTO;
import sistemaestudiantil.sge.dto.InscripcionDTO;
import sistemaestudiantil.sge.enums.CicloAcademico;
import sistemaestudiantil.sge.enums.EstadoInscripcion;
import sistemaestudiantil.sge.exceptions.DuplicadoException;
import sistemaestudiantil.sge.exceptions.OperacionNoPermitidaException;
import sistemaestudiantil.sge.exceptions.RecursoNoencontradoException;
import sistemaestudiantil.sge.mapper.InscripcionMapper;
import sistemaestudiantil.sge.model.Asignatura;
import sistemaestudiantil.sge.model.Estudiante;
import sistemaestudiantil.sge.model.Grupo;
import sistemaestudiantil.sge.model.Inscripcion;
import sistemaestudiantil.sge.repository.EstudianteRepository;
import sistemaestudiantil.sge.repository.GrupoRespository;
import sistemaestudiantil.sge.repository.InscripcionRepository;

@Service
public class InscripcionService {

    private final GrupoRespository grupoRespository;
    private final EstudianteRepository estudianteRepository;
    private final InscripcionRepository inscripcionRepository;
    private final InscripcionMapper inscripcionMapper;

    public InscripcionService(GrupoRespository gRespository, InscripcionMapper inscripcionMapper, EstudianteRepository eRepository, InscripcionRepository iRepository){
        this.grupoRespository = gRespository;
        this.inscripcionMapper=inscripcionMapper;
        this.estudianteRepository = eRepository;
        this.inscripcionRepository = iRepository;
    }

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

        Long idAsignatura = grupo.getAsignatura().getIdAsignatura();
        CicloAcademico cicloActual = grupo.getCiclo();

        boolean yaTieneLaMateria = inscripcionRepository.yaEstaInscritoEnLaMateria(estudiante.getIdEstudiante(), idAsignatura, cicloActual);

        if(yaTieneLaMateria){
            throw new DuplicadoException("El estudiante ya tiene inscrita la materia '" + grupo.getAsignatura().getName() + "' en el ciclo " + cicloActual + ". No se permite doble inscripción.");
        }

        boolean yaInscrito = inscripcionRepository.existsByEstudianteAndGrupo(estudiante, grupo);
        if(yaInscrito){
            throw new DuplicadoException("El estudiante " + dto.getIdEstudiante() + " ya se encuentra inscrito en este grupo.");
        }

        Asignatura asignaturaObjetivo = grupo.getAsignatura();

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
            dto.setCiclo(inscripcion.getGrupo().getCiclo().name());
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
        Estudiante estudiante = estudianteRepository.findById(dto.getIdEstudiante())
                .orElseThrow(() -> new RecursoNoencontradoException("Estudiante no encontrado"));

        if (!estudiante.estaActivo()) {
            throw new OperacionNoPermitidaException("El estudiante no está activo.");
        }

        Grupo nuevoGrupo = grupoRespository.findById(dto.getIdGrupo())
                .orElseThrow(() -> new RecursoNoencontradoException("Grupo destino no encontrado"));

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
    public String cerrarCiclo(CicloAcademico ciclo) {
        List<Inscripcion> pendientes = inscripcionRepository.findPendientesDeCierre(ciclo);
        
        int aprobados = 0;
        int reprobados = 0;

        for (Inscripcion i : pendientes) {
            if (i.getNotaFinal() >= 6.0) {
                i.setEstadoInscripcion(EstadoInscripcion.APROBADO);
                aprobados++;
            } else {
                i.setEstadoInscripcion(EstadoInscripcion.REPROBADO);
                reprobados++;
            }
        }

        inscripcionRepository.saveAll(pendientes);

        return "Cierre del ciclo " + ciclo + " completado. " + 
               aprobados + " aprobados, " + reprobados + " reprobados.";
    }
}