package sistemaestudiantil.sge.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sistemaestudiantil.sge.dto.EvaluacionDTO;
import sistemaestudiantil.sge.enums.Roles;
import sistemaestudiantil.sge.exceptions.CredencialesInvalidasException;
import sistemaestudiantil.sge.exceptions.OperacionNoPermitidaException;
import sistemaestudiantil.sge.exceptions.RecursoNoencontradoException;
import sistemaestudiantil.sge.model.Estudiante;
import sistemaestudiantil.sge.repository.EstudianteRepository;
import sistemaestudiantil.sge.repository.InscripcionRepository;
import sistemaestudiantil.sge.response.ApiResponse;
import sistemaestudiantil.sge.service.EvaluacionService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/evaluaciones")
@CrossOrigin("*")
public class EvaluacionController {
    private final EvaluacionService service;
    private final InscripcionRepository inscripcionRepository;
    private final EstudianteRepository estudianteRepository;

    public EvaluacionController(EvaluacionService service, InscripcionRepository inscripcionRepository, EstudianteRepository estudianteRepository){
        this.service=service;
        this.estudianteRepository=estudianteRepository;
        this.inscripcionRepository=inscripcionRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EvaluacionDTO>> guardar(@RequestBody EvaluacionDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new CredencialesInvalidasException("Sesión no válida.");
        }

        String username = auth.getName();

        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals(Roles.ROLE_ADMIN.name())||r.getAuthority().equals(Roles.ROLE_ADMINISTRATIVO.name()));
        
        boolean esProfesor = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals(Roles.ROLE_PROFESOR.name()));

        if (esProfesor) {
            boolean esAlumnoSuyo = inscripcionRepository.esInscripcionDeProfesor(dto.getIdInscripcion(), username);

            if (!esAlumnoSuyo) {
                throw new OperacionNoPermitidaException("No tiene permiso para calificar a este estudiante. No pertenece a sus grupos asignados.");
            }
        } else if (!esAdmin) {
            throw new OperacionNoPermitidaException("Su rol no tiene permisos para registrar calificaciones.");
        }

        EvaluacionDTO guardado = service.agregarNota(dto);
        
        return new ResponseEntity<>(new ApiResponse<>("Nota registrada y promedio actualizado.", guardado, true), HttpStatus.CREATED);
    }

    @GetMapping("/inscripcion/{idInscripcion}")
    public ResponseEntity<ApiResponse<List<EvaluacionDTO>>> verNotas(@PathVariable Long idInscripcion) {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new CredencialesInvalidasException("No se detectó una sesión activa. Por favor inicie sesión nuevamente.");
        }
        String username=authentication.getName();
        boolean esPersonal=authentication.getAuthorities().stream().anyMatch(r->r.getAuthority().equals(Roles.ROLE_ADMIN.name())||r.getAuthority().equals(Roles.ROLE_ADMINISTRATIVO.name())||r.getAuthority().equals(Roles.ROLE_PROFESOR.name()));
        if (!esPersonal) {
            Estudiante estudiante = estudianteRepository.findByCarnetOrEmail(username, username).orElseThrow(() -> new RecursoNoencontradoException("Usuario no encontrado."));
            boolean esSuya = inscripcionRepository.existsByIdInscripcionAndEstudiante_IdEstudiante(
                idInscripcion, 
                estudiante.getIdEstudiante()
            );
            if (!esSuya) {
                throw new OperacionNoPermitidaException("No tienes permiso para ver notas de esta inscripción ajena.");
            }
        }
        List<EvaluacionDTO> notas = service.obtenerNotasPorInscripcion(idInscripcion);
        return ResponseEntity.ok(new ApiResponse<>(
            "Desglose de notas", 
            notas, 
            true
        ));
    }
    
}
