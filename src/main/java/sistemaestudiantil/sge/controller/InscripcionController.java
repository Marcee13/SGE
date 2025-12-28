package sistemaestudiantil.sge.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import sistemaestudiantil.sge.dto.HistorialDTO;
import sistemaestudiantil.sge.dto.InscripcionDTO;
import sistemaestudiantil.sge.response.ApiResponse;
import sistemaestudiantil.sge.service.InscripcionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@Controller
@RequestMapping("api/inscripciones")
public class InscripcionController {
    private final InscripcionService service;

    public InscripcionController(InscripcionService service){
        this.service=service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InscripcionDTO>>>listar(){
        List<InscripcionDTO> lista=service.listarInscripciones();
        return new ResponseEntity<>(new ApiResponse<>("Lista de inscripciones",lista,true),HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<InscripcionDTO>>guardar(@RequestBody InscripcionDTO dto){
        InscripcionDTO nuevaInscripcion= service.inscribir(dto);
        ApiResponse<InscripcionDTO> respuesta= new ApiResponse<>("Inscripción realizada con éxito", nuevaInscripcion, true);
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }
    
    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<ApiResponse<List<HistorialDTO>>> verNotasPorEstudiante(@PathVariable Long idEstudiante) {
        List<HistorialDTO> historial=service.obtenerHistorialEstudiante(idEstudiante);
        return new ResponseEntity<>(new ApiResponse<>("Historial de notas obtenido.",historial,true),HttpStatus.OK);
    }
    
}
