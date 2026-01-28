package sistemaestudiantil.sge.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sistemaestudiantil.sge.dto.EstudianteDTO;
import sistemaestudiantil.sge.enums.EstadoEstudiante;
import sistemaestudiantil.sge.enums.TipoExamenAdmision;
import sistemaestudiantil.sge.exceptions.RecursoNoencontradoException;
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
    private final EstudianteRepository estudianteRepository;
    private static final Logger logger = LoggerFactory.getLogger(EstudianteController.class);
    private static final String MSJ_ESTUDIANTE_NO_ENCONTRADO = "Estudiante con ID %d no encontrado.";

    public EstudianteController(EstudianteService service, StorageService storageService, EstudianteRepository estudianteRepository){
        this.service = service;
        this.estudianteRepository=estudianteRepository;
        this.storageService=storageService;
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

   @PostMapping("/{id}/foto")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('ADMINISTRATIVO')")
    public ResponseEntity<ApiResponse<String>> subirFotoPerfil(@PathVariable Long id, @RequestParam("file") MultipartFile file) {

        Estudiante estudiante = estudianteRepository.findById(id).orElseThrow(() -> new RecursoNoencontradoException((String.format(MSJ_ESTUDIANTE_NO_ENCONTRADO, id))));

        String nombreArchivo = storageService.almacenarArchivo(file);

        estudiante.setFotoPerfil(nombreArchivo);
        estudianteRepository.save(estudiante);

        return ResponseEntity.ok(new ApiResponse<>(
            "Foto de perfil actualizada correctamente.",
            nombreArchivo,
            true
        ));
    }

    @PostMapping("/{id}/titulo-bachiller")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('ADMINISTRATIVO')")
    public ResponseEntity<ApiResponse<String>> subirTitulo(@PathVariable Long id, @RequestParam("file") MultipartFile file) {

        Estudiante estudiante = estudianteRepository.findById(id).orElseThrow(() -> new RecursoNoencontradoException((String.format(MSJ_ESTUDIANTE_NO_ENCONTRADO, id))));

        String nombreArchivo = storageService.almacenarArchivo(file);

        estudiante.setDocumentoTitulo(nombreArchivo);
        estudianteRepository.save(estudiante);

        return ResponseEntity.ok(new ApiResponse<>(
            "Documento de título de bachiller subido correctamente.",
            nombreArchivo,
            true
        ));
    }

    @PostMapping("/{id}/dui")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('ADMINISTRATIVO')")
    public ResponseEntity<ApiResponse<String>> subirDUI(@PathVariable Long id, @RequestParam("file") MultipartFile file) {

        Estudiante estudiante = estudianteRepository.findById(id).orElseThrow(() -> new RecursoNoencontradoException((String.format(MSJ_ESTUDIANTE_NO_ENCONTRADO, id))));

        String nombreArchivo = storageService.almacenarArchivo(file);

        estudiante.setDocumentoDUI(nombreArchivo);
        estudianteRepository.save(estudiante);

        return ResponseEntity.ok(new ApiResponse<>(
            "Documento DUI subido correctamente.",
            nombreArchivo,
            true
        ));
    }

    @PostMapping("/{id}/nit")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('ADMINISTRATIVO')")
    public ResponseEntity<ApiResponse<String>> subirNIT(@PathVariable Long id, @RequestParam("file") MultipartFile file) {

        Estudiante estudiante = estudianteRepository.findById(id).orElseThrow(() -> new RecursoNoencontradoException((String.format(MSJ_ESTUDIANTE_NO_ENCONTRADO, id))));

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
    public ResponseEntity<Resource> verArchivo(@PathVariable String filename) {
        
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
}
