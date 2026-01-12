package sistemaestudiantil.sge.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sistemaestudiantil.sge.dto.ArancelDTO;
import sistemaestudiantil.sge.response.ApiResponse;
import sistemaestudiantil.sge.service.ArancelService;

@RestController
@RequestMapping("/api/aranceles")
public class ArancelController {
    private final ArancelService arancelService;

    public ArancelController(ArancelService arancelService) {
        this.arancelService = arancelService;
    }

    @PostMapping
    public ResponseEntity<ArancelDTO> configurarPrecio(@RequestBody ArancelDTO dto) {
        ArancelDTO resultado = arancelService.guardarArancel(dto);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping
     public ResponseEntity<ApiResponse<List<ArancelDTO>>> listarTodos(){
        List<ArancelDTO> lista = arancelService.obtenerTodos();
        ApiResponse<List<ArancelDTO>> respuesta = new ApiResponse<>(
            "Lista de aranceles obtenida",
            lista,
            true
        );
        return ResponseEntity.ok(respuesta);
    }
}
