package sistemaestudiantil.sge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sistemaestudiantil.sge.dto.ArancelDTO;
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
}
