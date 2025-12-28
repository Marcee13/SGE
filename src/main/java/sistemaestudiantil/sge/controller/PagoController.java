package sistemaestudiantil.sge.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sistemaestudiantil.sge.dto.PagoDTO;
import sistemaestudiantil.sge.service.PagoService;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<List<PagoDTO>> obtenerTalonario(@PathVariable Long idEstudiante) {
        List<PagoDTO> talonario = pagoService.listarPagosPorEstudiante(idEstudiante);
        return ResponseEntity.ok(talonario);
    }
}

