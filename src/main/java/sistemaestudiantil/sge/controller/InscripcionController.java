package sistemaestudiantil.sge.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sistemaestudiantil.sge.dto.AvanceAcademicoDTO;
import sistemaestudiantil.sge.dto.HistorialDTO;
import sistemaestudiantil.sge.dto.InscripcionDTO;
import sistemaestudiantil.sge.enums.Roles;
import sistemaestudiantil.sge.exceptions.CredencialesInvalidasException;
import sistemaestudiantil.sge.exceptions.RecursoNoencontradoException;
import sistemaestudiantil.sge.model.Estudiante;
import sistemaestudiantil.sge.repository.EstudianteRepository;
import sistemaestudiantil.sge.response.ApiResponse;
import sistemaestudiantil.sge.service.InscripcionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("api/inscripciones")
public class InscripcionController {
    private final InscripcionService service;
    private final EstudianteRepository estudianteRepository;

    public InscripcionController(InscripcionService service, EstudianteRepository estudianteRepository){
        this.service=service;
        this.estudianteRepository=estudianteRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InscripcionDTO>>>listar(){
        List<InscripcionDTO> lista=service.listarInscripciones();
        return new ResponseEntity<>(new ApiResponse<>("Lista de inscripciones",lista,true),HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<InscripcionDTO>>guardar(@RequestBody InscripcionDTO dto){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new CredencialesInvalidasException("No se detectó una sesión activa. Por favor inicie sesión nuevamente.");
        }
        String usernameAutenticado = authentication.getName();
        Estudiante estudianteLogueado = estudianteRepository.findByCarnet(usernameAutenticado).or(() -> estudianteRepository.findByEmail(usernameAutenticado)).orElseThrow(() -> new RecursoNoencontradoException("Error de seguridad: Usuario del token no encontrado en BD."));
        dto.setIdEstudiante(estudianteLogueado.getIdEstudiante());
        InscripcionDTO nuevaInscripcion= service.inscribir(dto);
        ApiResponse<InscripcionDTO> respuesta= new ApiResponse<>(
            "Inscripción realizada con éxito", 
            nuevaInscripcion, 
            true
        );
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }
    
    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<ApiResponse<List<HistorialDTO>>> verNotasPorEstudiante(@PathVariable Long idEstudiante) {
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        if(auth==null||!auth.isAuthenticated()||"anonymousUser".equals(auth.getPrincipal())){
            throw new CredencialesInvalidasException("Sesión no valida. Inicie sesión nuevamente.");
        }

        String username =auth.getName();
        boolean esPersonal=auth.getAuthorities().stream().anyMatch(r->r.getAuthority().equals(Roles.ROLE_ADMIN.name())||r.getAuthority().equals(Roles.ROLE_ADMINISTRATIVO.name())||r.getAuthority().equals(Roles.ROLE_PROFESOR.name()));
        
        Long idFinal=idEstudiante;
        Estudiante estudianteObjetivo;
        
        if (!esPersonal) {
            estudianteObjetivo = estudianteRepository.findByCarnetOrEmail(username, username).orElseThrow(() -> new RecursoNoencontradoException("Error de sesión: Estudiante no encontrado."));

            idFinal = estudianteObjetivo.getIdEstudiante();
        } else {
            estudianteObjetivo = estudianteRepository.findById(idEstudiante).orElseThrow(() -> new RecursoNoencontradoException("El estudiante con ID " + idEstudiante + " no existe."));
        }

        List<HistorialDTO> historial=service.obtenerHistorialEstudiante(idFinal);

        if (historial.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse<>(
                "El estudiante " + estudianteObjetivo.getCarnet() + " existe, pero aún no tiene notas registradas.", 
                historial, 
                true
            ));
        }

        return ResponseEntity.ok(new ApiResponse<>(
            "Historial académico de " + estudianteObjetivo.getCarnet() + " obtenido correctamente.",
            historial,
            true
        ));
    }
    
    @PutMapping("/cambio-grupo")
    public ResponseEntity<ApiResponse<InscripcionDTO>>cambiarGrupo(@RequestBody InscripcionDTO dto){
        InscripcionDTO cambioRealizado=service.cambioGrupo(dto);
        ApiResponse<InscripcionDTO> respuesta=new ApiResponse<>("Cambio de grupo realizado con éxito", cambioRealizado, true);
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }

    @PutMapping("/retirar/{idInscripcion}")
    public ResponseEntity<ApiResponse<InscripcionDTO>> retirarMateria(@PathVariable Long idInscripcion) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new CredencialesInvalidasException("Sesión inválida. Inicie sesión nuevamente.");
        }
        
        String username = auth.getName();

        Estudiante estudiante = estudianteRepository.findByCarnetOrEmail(username, username).orElseThrow(() -> new RecursoNoencontradoException("Estudiante no encontrado."));

        InscripcionDTO dto = service.retirarMateria(idInscripcion, estudiante.getIdEstudiante());

        ApiResponse<InscripcionDTO> respuesta = new ApiResponse<>(
            "Materia retirada correctamente. Se ha actualizado su historial.",
            dto,
            true
        );
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/avance-curricular/{idEstudiante}")
    public ResponseEntity<ApiResponse<AvanceAcademicoDTO>> verAvanceCurricular(@PathVariable Long idEstudiante) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) throw new CredencialesInvalidasException("Sesión inválida. Inicie sesión nuevamente.");
        
        String username = auth.getName();
        boolean esPersonal = auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals(Roles.ROLE_ADMIN.name()) || r.getAuthority().equals(Roles.ROLE_PROFESOR.name())||r.getAuthority().equals(Roles.ROLE_ADMINISTRATIVO.name()));

        Long idFinal = idEstudiante;

        if (!esPersonal) {
            Estudiante est = estudianteRepository.findByCarnetOrEmail(username, username).orElseThrow(() -> new RecursoNoencontradoException("Estudiante no encontrado"));
            idFinal = est.getIdEstudiante();
        }

        AvanceAcademicoDTO avance = service.obtenerKardex(idFinal);

        return ResponseEntity.ok(new ApiResponse<>(
            "Avance curricular cargado con éxito.",
            avance,
            true
        ));
    }
}
