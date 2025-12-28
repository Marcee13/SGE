package sistemaestudiantil.sge.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sistemaestudiantil.sge.dto.EvaluacionDTO;
import sistemaestudiantil.sge.dto.HistorialDTO;
import sistemaestudiantil.sge.dto.InscripcionDTO;
import sistemaestudiantil.sge.enums.EstadoInscripcion;
import sistemaestudiantil.sge.exceptions.DuplicadoException;
import sistemaestudiantil.sge.exceptions.OperacionNoPermitidaException;
import sistemaestudiantil.sge.exceptions.RecursoNoencontradoException;
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

    public InscripcionService(GrupoRespository gRespository, EstudianteRepository eRepository, InscripcionRepository iRepository){
        this.grupoRespository=gRespository;
        this.estudianteRepository=eRepository;
        this.inscripcionRepository=iRepository;
    }

    @Transactional
    public InscripcionDTO inscribir(InscripcionDTO dto){
        Estudiante estudiante=estudianteRepository.findById(dto.getIdEstudiante()).orElseThrow(()->new RecursoNoencontradoException("Estudiante no encontrado con el ID: " +dto.getIdEstudiante()));
        Grupo grupo=grupoRespository.findById(dto.getIdGrupo()).orElseThrow(()->new RecursoNoencontradoException("El grupo no se encontró con ID: " +dto.getIdGrupo()));

        if (!estudiante.estaActivo()) {
        throw new OperacionNoPermitidaException(
            "El estudiante no está activo. Su estado actual es: " + estudiante.getEstado());
        }

        boolean yaInscrito=inscripcionRepository.existsByEstudianteAndGrupo(estudiante,grupo);
        if(yaInscrito){
            throw new DuplicadoException("El estudiante " +dto.getIdEstudiante()+" ya se encuentra inscrito en este grupo.");
        }

        Asignatura asignaturaObjetivo=grupo.getAsignatura();

        if(asignaturaObjetivo.getPrerrequisitos()!=null&&!asignaturaObjetivo.getPrerrequisitos().isEmpty()){
            for(Asignatura requisito: asignaturaObjetivo.getPrerrequisitos()){
                boolean aprobado=inscripcionRepository.haAprobadoMateria(estudiante.getIdEstudiante(), requisito.getIdAsignatura());
                if(!aprobado){
                    throw new OperacionNoPermitidaException("No cumple con los prerequisitos. Se debe aprobar "+requisito.getName()+" para inscribir " +asignaturaObjetivo.getName());
                }
            }
        }

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setEstudiante(estudiante);
        inscripcion.setGrupo(grupo);
        inscripcion.setFechaInscripcion(LocalDate.now());
        inscripcion.setEstadoInscripcion(EstadoInscripcion.INSCRITO);
        inscripcion.setNotaFinal(0.0);

        Inscripcion guardado = inscripcionRepository.save(inscripcion);
        return convertirADTO(guardado);
    }

    public List<InscripcionDTO> listarInscripciones(){
        return inscripcionRepository.findAll().stream().map(this::convertirADTO).toList();
    }

    private InscripcionDTO convertirADTO(Inscripcion inscripcion) {
        InscripcionDTO dto = new InscripcionDTO();
        dto.setIdInscripcion(inscripcion.getIdInscripcion());
        dto.setIdEstudiante(inscripcion.getEstudiante().getIdEstudiante());
        dto.setNombreEstudiante(inscripcion.getEstudiante().getNombres() + " " + inscripcion.getEstudiante().getApellidos());
        dto.setIdGrupo(inscripcion.getGrupo().getIdGrupo());
        dto.setCodigoGrupo(inscripcion.getGrupo().getCodigoGrupo());
        dto.setNombreMateria(inscripcion.getGrupo().getAsignatura().getName());
        dto.setFechaInscripcion(inscripcion.getFechaInscripcion());
        dto.setEstado(inscripcion.getEstadoInscripcion());
        dto.setNotaFinal(inscripcion.getNotaFinal());
        return dto;
    }

    @Transactional(readOnly=true)
    public List<HistorialDTO> obtenerHistorialEstudiante(Long idEstudiante){
        List<Inscripcion> inscripciones =inscripcionRepository.findHistorialCompleto(idEstudiante);
        return inscripciones.stream().map(inscripcion->{
            HistorialDTO dto=new HistorialDTO();
            dto.setNombreMateria(inscripcion.getGrupo().getAsignatura().getName());
            dto.setCiclo(inscripcion.getGrupo().getCiclo().name());
            dto.setEstado(inscripcion.getEstadoInscripcion().name());
            dto.setNotaFinal(inscripcion.getNotaFinal());

            List<EvaluacionDTO> evaluacionesDTO=inscripcion.getEvaluaciones().stream().map(e->{
                EvaluacionDTO evaluacionDTO=new EvaluacionDTO();
                evaluacionDTO.setNombreActividad(e.getNombreActividad());
                evaluacionDTO.setPorcentaje(e.getPorcentaje());
                evaluacionDTO.setNotaObtenida(e.getNotaObtenida());

                return evaluacionDTO;
            }).toList();

            dto.setEvaluaciones(evaluacionesDTO);

            return dto;
        }).toList();
    }
}
