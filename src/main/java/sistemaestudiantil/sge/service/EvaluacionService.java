package sistemaestudiantil.sge.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import sistemaestudiantil.sge.dto.EvaluacionDTO;
import sistemaestudiantil.sge.enums.EstadoInscripcion;
import sistemaestudiantil.sge.enums.Roles;
import sistemaestudiantil.sge.exceptions.CredencialesInvalidasException;
import sistemaestudiantil.sge.exceptions.OperacionNoPermitidaException;
import sistemaestudiantil.sge.exceptions.RecursoNoencontradoException;
import sistemaestudiantil.sge.mapper.EvaluacionMapper;
import sistemaestudiantil.sge.model.Estudiante;
import sistemaestudiantil.sge.model.Evaluacion;
import sistemaestudiantil.sge.model.Inscripcion;
import sistemaestudiantil.sge.repository.EstudianteRepository;
import sistemaestudiantil.sge.repository.EvaluacionRepository;
import sistemaestudiantil.sge.repository.InscripcionRepository;

@Service
public class EvaluacionService {
    private final EvaluacionRepository evaluacionRepository;
    private final InscripcionRepository inscripcionRepository;
    private final EstudianteRepository estudianteRepository;
    private final EvaluacionMapper evaluacionMapper;

    public EvaluacionService(EvaluacionRepository evaluacionRepository, InscripcionRepository inscripcionRepository, EstudianteRepository estudianteRepository, EvaluacionMapper evaluacionMapper) {
        this.evaluacionRepository = evaluacionRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.estudianteRepository = estudianteRepository;
        this.evaluacionMapper = evaluacionMapper;
    }

    @Transactional
    public EvaluacionDTO agregarNota(EvaluacionDTO dto) {
        validarPermisoParaCalificar(dto.getIdInscripcion());

        if(dto.getNotaObtenida()<0||dto.getNotaObtenida()>10){
            throw new IllegalArgumentException("La nota debe estar entre 0.0 y 10.0. Por favor revise.");
        }

        List<Evaluacion> evaluacionesExistentes = evaluacionRepository.findByInscripcionIdInscripcion(dto.getIdInscripcion());

        boolean nombreDuplicado = evaluacionesExistentes.stream().anyMatch(e -> e.getNombreActividad().equalsIgnoreCase(dto.getNombreActividad()));
        
        if (nombreDuplicado) {
            throw new OperacionNoPermitidaException("Ya existe una evaluación llamada '" + dto.getNombreActividad() + "' para este estudiante.");
        }

        Inscripcion inscripcionReal = inscripcionRepository.findById(dto.getIdInscripcion()).orElseThrow(() -> new RecursoNoencontradoException("Inscripción con ID" + dto.getIdInscripcion() + "no encontrada"));
        
        if (inscripcionReal.getEstadoInscripcion() != EstadoInscripcion.INSCRITO) {
            throw new OperacionNoPermitidaException("No se puede registrar notas. El estado de la inscripción es: " + inscripcionReal.getEstadoInscripcion());
        }

        Double porcentajeYaRegistrado = evaluacionRepository.obtenerPorcentajeAcumulado(dto.getIdInscripcion());
        
        double nuevoTotal = porcentajeYaRegistrado + dto.getPorcentaje();

        if (nuevoTotal > 100.0) {
            double restante = 100.0 - porcentajeYaRegistrado;
            throw new IllegalArgumentException(
                "No se puede registrar la nota. El porcentaje excede el 100%. " +
                "Acumulado actual: " + porcentajeYaRegistrado + "%. " +
                "Solo puedes agregar hasta: " + restante + "%."
            );
        }
        
        Evaluacion evaluacion = evaluacionMapper.toEntity(dto);
        evaluacion.setInscripcion(inscripcionReal);

        Evaluacion guardada = evaluacionRepository.save(evaluacion);

        actualizarPromedioInscripcion(inscripcionReal);

        return evaluacionMapper.toDTO(guardada);
    }

    @Transactional(readOnly = true)
    public List<EvaluacionDTO> obtenerNotasPorInscripcion(Long idInscripcion) {
        validarPermisoParaVerNotas(idInscripcion);

        List<Evaluacion> evaluaciones = evaluacionRepository.findByInscripcionIdInscripcion(idInscripcion);
        
        return evaluaciones.stream().map(evaluacionMapper::toDTO).toList();
    }

    private void actualizarPromedioInscripcion(Inscripcion inscripcion) {
        List<Evaluacion> notas = evaluacionRepository.findByInscripcionIdInscripcion(inscripcion.getIdInscripcion());
        
        double promedioAcumulado = 0.0;
        
        for (Evaluacion ev : notas) {
            Number notaNum = ev.getNotaObtenida(); 
            Number porcNum = ev.getPorcentaje();
            
            if (notaNum != null && porcNum != null) {
                double notaVal = notaNum.doubleValue();
                double porcVal = porcNum.doubleValue();
                
                promedioAcumulado += (notaVal * (porcVal / 100.0));
            }
        }

        inscripcion.setNotaFinal(Math.round(promedioAcumulado * 100.0) / 100.0); 
        inscripcionRepository.save(inscripcion);
    }

    private void validarPermisoParaCalificar(Long idInscripcion) {
        Authentication auth = obtenerAuthSeguro();
        String username = auth.getName();

        boolean esProfesor = tieneRol(auth, Roles.ROLE_PROFESOR);
        boolean esAdmin = tieneRol(auth, Roles.ROLE_ADMIN) || tieneRol(auth, Roles.ROLE_ADMINISTRATIVO);

        if (esProfesor) {
            boolean esAlumnoSuyo = inscripcionRepository.esInscripcionDeProfesor(idInscripcion, username);
            if (!esAlumnoSuyo) {
                throw new OperacionNoPermitidaException("No tiene permiso para calificar a este estudiante.");
            }
        } else if (!esAdmin) {
            throw new OperacionNoPermitidaException("Su rol no permite registrar calificaciones.");
        }
    }

    private void validarPermisoParaVerNotas(Long idInscripcion) {
        Authentication auth = obtenerAuthSeguro();
        String username = auth.getName();

        boolean esPersonal = tieneRol(auth, Roles.ROLE_ADMIN) || tieneRol(auth, Roles.ROLE_ADMINISTRATIVO) || tieneRol(auth, Roles.ROLE_PROFESOR);

        if (!esPersonal) {
            Estudiante estudiante = estudianteRepository.findByCarnet(username)
                    .orElseThrow(() -> new RecursoNoencontradoException("Usuario no encontrado."));

            boolean esSuya = inscripcionRepository.existsByIdInscripcionAndEstudiante_IdEstudiante(
                    idInscripcion, 
                    estudiante.getIdEstudiante()
            );

            if (!esSuya) {
                throw new OperacionNoPermitidaException("No puedes ver notas ajenas.");
            }
        }
    }

    private Authentication obtenerAuthSeguro() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new CredencialesInvalidasException("Sesión no válida o expirada.");
        }
        return auth;
    }

    private boolean tieneRol(Authentication auth, Roles rol) {
        return auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals(rol.name()));
    }
}
