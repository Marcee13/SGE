package sistemaestudiantil.sge.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sistemaestudiantil.sge.dto.CarreraDTO;
import sistemaestudiantil.sge.response.ApiResponse;
import sistemaestudiantil.sge.service.CarreraService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/carreras")
public class CarreraController {
    private CarreraService service;

    public CarreraController(CarreraService service){
        this.service=service;
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<CarreraDTO>>> listarTodos(){
        List<CarreraDTO> lista=service.listarCarreras();
        ApiResponse<List<CarreraDTO>> respuesta= new ApiResponse<>(
            "Listado de carreras obtenido", 
            lista, 
            true
        );
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<CarreraDTO>> guardarCarrera(@RequestBody CarreraDTO dto) {
        CarreraDTO nuevaCarrera = service.crearCarrera(dto);
        
        ApiResponse<CarreraDTO> respuesta = new ApiResponse<>(
            "Carrera creada con éxito",
            nuevaCarrera,
            true
        );
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CarreraDTO>> actualizarCarrera(@PathVariable Long id, @RequestBody CarreraDTO dto) {
        CarreraDTO actualizado=service.actualizarCarrera(id, dto);

        ApiResponse<CarreraDTO> respuesta=new ApiResponse<>(
            "Carrera actualizada correctamente.", 
            actualizado, 
            true
        );
        return new ResponseEntity<>(respuesta,HttpStatus.OK);
    }
}
