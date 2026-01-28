package sistemaestudiantil.sge.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sistemaestudiantil.sge.dto.EstudianteDTO;
import sistemaestudiantil.sge.exceptions.RecursoNoencontradoException;
import sistemaestudiantil.sge.mapper.EstudianteMapper;
import sistemaestudiantil.sge.model.Estudiante;
import sistemaestudiantil.sge.repository.EstudianteRepository;
import sistemaestudiantil.sge.response.ApiResponse;
import sistemaestudiantil.sge.service.EstudianteService;
import sistemaestudiantil.sge.service.StorageService;

@RestController
@RequestMapping("/api/estudiantes")
public class EstudianteController {
    private final EstudianteService service;
    private final StorageService storageService;
    private final EstudianteMapper estudianteMapper;
    private final EstudianteRepository estudianteRepository;
    private static final Logger logger = LoggerFactory.getLogger(EstudianteController.class);

    public EstudianteController(EstudianteService service, StorageService storageService, EstudianteRepository estudianteRepository, EstudianteMapper estudianteMapper){
        this.service = service;
        this.estudianteRepository=estudianteRepository;
        this.storageService=storageService;
        this.estudianteMapper=estudianteMapper;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EstudianteDTO>>> listarTodos(){
        List<EstudianteDTO> lista = service.obtenerTodos();
        ApiResponse<List<EstudianteDTO>> respuesta = new ApiResponse<>(
            "Lista obtenida",
            lista,
            true
        );
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EstudianteDTO>> guardar(@RequestBody EstudianteDTO dto) {
        EstudianteDTO nuevoEstudiante = service.crearEstudiante(dto);
        
        ApiResponse<EstudianteDTO> respuesta = new ApiResponse<>(
            "Estudiante creado con éxito",
            nuevoEstudiante,
            true
        );
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EstudianteDTO>> actualizarEstudiante(@PathVariable Long id, @RequestBody EstudianteDTO dto) {
        EstudianteDTO actualizado=service.actualizarEstudiante(id, dto);

        ApiResponse<EstudianteDTO> respuesta=new ApiResponse<>(
            "Estudiante actualizado correctamente.", 
            actualizado, 
            true
        );
        return new ResponseEntity<>(respuesta,HttpStatus.OK);
    }

    @GetMapping("/facultad/{idFacultad}")
    public ResponseEntity<ApiResponse<List<EstudianteDTO>>> listarPorFacultad(@PathVariable Long idFacultad){
        List<EstudianteDTO> lista=service.listarEstudiantesPorFacultad(idFacultad);
        if(lista.isEmpty()){
            return ResponseEntity.ok(new ApiResponse<>(
                "No hay estudiantes inscritos en esta facultad.", 
                lista, 
                true)
            );
        }
        return ResponseEntity.ok(new ApiResponse<>(
            "Lista de estudiantes inscritos en la facultad con ID: " +idFacultad, 
            lista, 
            true)
        );
    }

   @PostMapping(value = "/foto-perfil", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // @PreAuthorize("hasRole('ADMIN') or hasRole('ADMINISTRATIVO') or hasRole('ESTUDIANTE')")
    public ResponseEntity<ApiResponse<String>> subirFotoPerfil(Authentication authentication, @RequestParam("file") MultipartFile file) {
        Estudiante estudiante = obtenerEstudianteLogueado(authentication);

        String nombreArchivo = storageService.almacenarArchivo(file);

        estudiante.setFotoPerfil(nombreArchivo);
        estudianteRepository.save(estudiante);

        return ResponseEntity.ok(new ApiResponse<>(
            "Foto de perfil actualizada correctamente.",
            nombreArchivo,
            true
        ));
    }

    @PostMapping(value = "/titulo-bachiller", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // @PreAuthorize("hasRole('ADMIN') or hasRole('ADMINISTRATIVO') or hasRole('ESTUDIANTE')")
    public ResponseEntity<ApiResponse<String>> subirTitulo(Authentication authentication, @RequestParam("file") MultipartFile file) {
        Estudiante estudiante = obtenerEstudianteLogueado(authentication);
        String nombreArchivo = storageService.almacenarArchivo(file);

        estudiante.setDocumentoTitulo(nombreArchivo);
        estudianteRepository.save(estudiante);

        return ResponseEntity.ok(new ApiResponse<>(
            "Documento de título de bachiller subido correctamente.",
            nombreArchivo,
            true
        ));
    }

    @PostMapping(value = "/dui", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // @PreAuthorize("hasRole('ADMIN') or hasRole('ADMINISTRATIVO') or hasRole('ESTUDIANTE')")
    public ResponseEntity<ApiResponse<String>> subirDUI(Authentication authentication, @RequestParam("file") MultipartFile file) {
        Estudiante estudiante = obtenerEstudianteLogueado(authentication);
        String nombreArchivo = storageService.almacenarArchivo(file);

        estudiante.setDocumentoDUI(nombreArchivo);
        estudianteRepository.save(estudiante);

        return ResponseEntity.ok(new ApiResponse<>(
            "Documento DUI subido correctamente.",
            nombreArchivo,
            true
        ));
    }

    @PostMapping(value = "/nit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // @PreAuthorize("hasRole('ADMIN') or hasRole('ADMINISTRATIVO') or hasRole('ESTUDIANTE')")
    public ResponseEntity<ApiResponse<String>> subirNIT(Authentication authentication, @RequestParam("file") MultipartFile file) {

        Estudiante estudiante = obtenerEstudianteLogueado(authentication);
        String nombreArchivo = storageService.almacenarArchivo(file);

        estudiante.setDocumentoNIT(nombreArchivo);
        estudianteRepository.save(estudiante);

        return ResponseEntity.ok(new ApiResponse<>(
            "Documento NIT subido correctamente.",
            nombreArchivo,
            true
        ));
    }

    @GetMapping({
        "/foto/{filename:.+}",
        "/documento/{filename:.+}",
        "/archivo/{filename:.+}"
    })
    public ResponseEntity<Resource> verArchivo(Authentication authentication, @PathVariable String filename) {
        String usuarioLogueado= authentication.getName();

        boolean esAdmin = authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN") || r.getAuthority().equals("ROLE_ADMINISTRATIVO"));

        if(!esAdmin){
            boolean esSuyo=estudianteRepository.esSuyoElArchivo(usuarioLogueado, filename);
            if(!esSuyo){
                throw new RecursoNoencontradoException("No tiene permiso para acceder a este archivo.");
            }
        }

        Resource file = storageService.cargarComoRecurso(filename);
        
        String contentType = "application/octet-stream";
        
        try {
            contentType = java.nio.file.Files.probeContentType(file.getFile().toPath());
        } catch (IOException ex) {
            logger.warn("No se pudo determinar el tipo de archivo para: {}", filename);
        }

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, contentType)
                .body(file);
    }

    private Estudiante obtenerEstudianteLogueado(Authentication authentication) {
        String username = authentication.getName();

        return estudianteRepository.findByCarnetOrEmail(username, username).orElseThrow(() -> new RecursoNoencontradoException("No se encontró el perfil del usuario: " + username));
    }

    @GetMapping("/mi-perfil")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ADMINISTRATIVO') or hasRole('ESTUDIANTE')")
    public ResponseEntity<ApiResponse<EstudianteDTO>> obtenerMiPerfil(Authentication authentication) {
        String identificador= authentication.getName();

        EstudianteDTO perfilEstudiante = service.buscarPorCarnetOrEmail(identificador);

        return ResponseEntity.ok(new ApiResponse<>(
            "Perfil recuperado exitosamente.",
            perfilEstudiante,
            true
        ));
    }
}
