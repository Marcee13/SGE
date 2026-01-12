package sistemaestudiantil.sge.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sistemaestudiantil.sge.dto.EvaluacionDTO;
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

    public EvaluacionController(EvaluacionService service){
        this.service=service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EvaluacionDTO>> guardar(@RequestBody EvaluacionDTO dto){
        EvaluacionDTO guardado=service.agregarNota(dto);
        return new ResponseEntity<>(new ApiResponse<>("Nota registrada y promedio actualizado.",guardado,true),HttpStatus.CREATED);
    }

    @GetMapping("/inscripcion/{idInscripcion}")
    public ResponseEntity<ApiResponse<List<EvaluacionDTO>>> verNotas(@PathVariable Long idInscripcion) {
        List<EvaluacionDTO> notas = service.obtenerNotasPorInscripcion(idInscripcion);
        
        return ResponseEntity.ok(new ApiResponse<>(
            "Desglose de notas", 
            notas, 
            true
        ));
    }
    
}
