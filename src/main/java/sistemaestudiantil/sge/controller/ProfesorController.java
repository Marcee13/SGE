package sistemaestudiantil.sge.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sistemaestudiantil.sge.dto.ProfesorDTO;
import sistemaestudiantil.sge.response.ApiResponse;
import sistemaestudiantil.sge.service.ProfesorService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("api/profesores")
public class ProfesorController {
    private final ProfesorService service;
    public ProfesorController(ProfesorService service){
        this.service=service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProfesorDTO>>>listarTodos(){
        List<ProfesorDTO> lista = service.obtenerTodos();
        ApiResponse<List<ProfesorDTO>> respuesta = new ApiResponse<>(
            "Lista de profesores obtenida",
            lista,
            true
        );
        return ResponseEntity.ok(respuesta);
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<ProfesorDTO>> guardar(@RequestBody ProfesorDTO dto) {
        ProfesorDTO nuevoProfesor = service.crearProfesor(dto);
        
        ApiResponse<ProfesorDTO> respuesta = new ApiResponse<>(
            "Profesor creado con éxito.",
            nuevoProfesor,
            true
        );
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProfesorDTO>> actualizarProfesor(@PathVariable Long id, @RequestBody ProfesorDTO dto) {
        ProfesorDTO actualizado=service.actualizarProfesor(id, dto);

        ApiResponse<ProfesorDTO> respuesta=new ApiResponse<>(
            "Profesor actualizado correctamente.", 
            actualizado, 
            true
        );
        return new ResponseEntity<>(respuesta,HttpStatus.OK);
    }
}
