package sistemaestudiantil.sge.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import sistemaestudiantil.sge.dto.GrupoDTO;
import sistemaestudiantil.sge.exceptions.DuplicadoException;
import sistemaestudiantil.sge.exceptions.OperacionNoPermitidaException;
import sistemaestudiantil.sge.exceptions.RecursoNoencontradoException;
import sistemaestudiantil.sge.mapper.GrupoMapper;
import sistemaestudiantil.sge.model.Asignatura;
import sistemaestudiantil.sge.model.Grupo;
import sistemaestudiantil.sge.model.Profesor;
import sistemaestudiantil.sge.repository.AsignaturaRepository;
import sistemaestudiantil.sge.repository.GrupoRespository;
import sistemaestudiantil.sge.repository.InscripcionRepository;
import sistemaestudiantil.sge.repository.ProfesorRepository;

@Service
public class GrupoService {
    private final AsignaturaRepository asignaturaRepository;
    private final ProfesorRepository profesorRepository;
    private final GrupoRespository grupoRepository;
    private final InscripcionRepository inscripcionRepository;
    private final GrupoMapper grupoMapper;

    public GrupoService(GrupoMapper grupoMapper, AsignaturaRepository asignaturaRepository, InscripcionRepository inscripcionRepository, GrupoRespository grupoRepository, ProfesorRepository profesorRepository){
        this.asignaturaRepository=asignaturaRepository;
        this.profesorRepository=profesorRepository;
        this.grupoMapper=grupoMapper;
        this.inscripcionRepository=inscripcionRepository;
        this.grupoRepository=grupoRepository;
    }

    @Transactional
    public GrupoDTO creaGrupo(GrupoDTO dto){
        Asignatura asignatura=asignaturaRepository.findById(dto.getIdAsignatura()).orElseThrow(()->new RecursoNoencontradoException("Asignatura no encontrada con el ID: " + dto.getIdAsignatura()));
        Profesor profesor =profesorRepository.findById(dto.getIdProfesor()).orElseThrow(()->new RecursoNoencontradoException("Profesor no encontrado con el ID: " +dto.getIdProfesor()));

        boolean existe = grupoRepository.existsByCodigoGrupoAndAsignaturaAndCiclo(
                dto.getCodigoGrupo(), 
                asignatura, 
                dto.getCiclo()
        );

        if (existe) {
            throw new DuplicadoException("Ya existe el grupo " + dto.getCodigoGrupo() + " para la materia " + asignatura.getName() + " en el ciclo " + dto.getCiclo());
        }

        List<Grupo> conflictos = grupoRepository.encontrarGruposEnConflicto(
            profesor.getIdProfesor(),
            dto.getCiclo(),
            dto.getDias(),
            dto.getHoraInicio(),
            dto.getHoraFin()
    );

    if (!conflictos.isEmpty()) {
        Grupo grupoConflictivo = conflictos.get(0);
        
        throw new OperacionNoPermitidaException(
            "CHOQUE DE HORARIO: El profesor " + profesor.getNombre() + " " + profesor.getApellidos() +
            " ya tiene asignado el grupo " + grupoConflictivo.getCodigoGrupo() + 
            " (" + grupoConflictivo.getAsignatura().getName() + ") " +
            "en el horario de " + grupoConflictivo.getHoraInicio() + " a " + grupoConflictivo.getHoraFin()
        );
    }

        Grupo grupo =new Grupo();
        grupo.setCodigoGrupo(dto.getCodigoGrupo());
        grupo.setCiclo(dto.getCiclo());
        grupo.setAsignatura(asignatura);
        grupo.setProfesor(profesor);
        grupo.setCuposDisponibles(dto.getCuposDisponibles());
        grupo.setDias(dto.getDias());
        grupo.setHoraInicio(dto.getHoraInicio());
        grupo.setHoraFin(dto.getHoraFin());

        Grupo guardado= grupoRepository.save(grupo);

        return grupoMapper.toDTO(guardado);
    }

    public List<GrupoDTO> listaGrupos(){
        return grupoRepository.findAll().stream().map(grupoMapper::toDTO).toList();
    }

    @Transactional
    public GrupoDTO actualizarCupos(Long idGrupo, Integer nuevosCupos) {
        Grupo grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new RecursoNoencontradoException("Grupo no encontrado"));

        if (nuevosCupos < 0) {
            throw new OperacionNoPermitidaException("Los cupos no pueden ser negativos.");
        }

        long inscritosActuales = inscripcionRepository.countByGrupo(grupo);
        if (nuevosCupos < inscritosActuales) {
             throw new OperacionNoPermitidaException("No puedes reducir cupos por debajo de la cantidad de inscritos actuales (" + inscritosActuales + ")");
        }

        grupo.setCuposDisponibles(nuevosCupos);
        Grupo grupoGuardado = grupoRepository.save(grupo);
        
        return grupoMapper.toDTO(grupoGuardado);
    }

    @Transactional
    public GrupoDTO asignarProfesor(Long idGrupo, Long idProfesor) {
        Grupo grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new RecursoNoencontradoException("Grupo no encontrado"));

        Profesor profesor = profesorRepository.findById(idProfesor)
                .orElseThrow(() -> new RecursoNoencontradoException("Profesor no encontrado"));

        boolean tieneConflicto = grupoRepository.existeChoqueHorario(
                idProfesor,
                grupo.getCiclo(),
                grupo.getDias(),
                grupo.getHoraInicio(),
                grupo.getHoraFin()
        );

        if (tieneConflicto) {
            throw new OperacionNoPermitidaException(
                "El profesor " + profesor.getNombre() + " " + profesor.getApellidos() +
                " ya tiene asignada una clase los " + grupo.getDias() + 
                " en el horario de " + grupo.getHoraInicio() + " a " + grupo.getHoraFin()
            );
        }

        grupo.setProfesor(profesor);
        Grupo grupoGuardado = grupoRepository.save(grupo);

        return grupoMapper.toDTO(grupoGuardado);
    }
}
