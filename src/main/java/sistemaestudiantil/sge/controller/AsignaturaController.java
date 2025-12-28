package sistemaestudiantil.sge.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sistemaestudiantil.sge.dto.AsignaturaDTO;
import sistemaestudiantil.sge.response.ApiResponse;
import sistemaestudiantil.sge.service.AsignaturaService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/asignaturas")
public class AsignaturaController {
    private final AsignaturaService service;

    public AsignaturaController(AsignaturaService service){
        this.service=service;
    }

    @GetMapping
     public ResponseEntity<ApiResponse<List<AsignaturaDTO>>> listarTodos(){
        List<AsignaturaDTO> lista = service.obtenerTodos();
        ApiResponse<List<AsignaturaDTO>> respuesta = new ApiResponse<>(
            "Lista obtenida",
            lista,
            true
        );
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AsignaturaDTO>> guardar(@RequestBody AsignaturaDTO dto) {
        AsignaturaDTO nuevaAsignatura = service.crearAsignatura(dto);
        
        ApiResponse<AsignaturaDTO> respuesta = new ApiResponse<>(
            "Asignatura creado con éxito",
            nuevaAsignatura,
            true
        );
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }
    
}
