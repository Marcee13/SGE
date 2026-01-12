package sistemaestudiantil.sge.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sistemaestudiantil.sge.dto.PagoDTO;
import sistemaestudiantil.sge.response.ApiResponse;
import sistemaestudiantil.sge.service.PagoService;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin("*")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<ApiResponse<List<PagoDTO>>> obtenerTalonario(@PathVariable Long idEstudiante) {
        List<PagoDTO> talonario = pagoService.listarPagosPorEstudiante(idEstudiante);
        return new ResponseEntity<>(new ApiResponse<>("Talonario de pago generado", talonario, true),HttpStatus.OK);
    }

    @GetMapping("/pendientes/{idEstudiante}")
    public ResponseEntity<ApiResponse<List<PagoDTO>>> obtenerPendientes(@PathVariable Long idEstudiante) {
        List<PagoDTO> pendientes = pagoService.listarPendientes(idEstudiante);
        ApiResponse<List<PagoDTO>> respuesta = new ApiResponse<>(
            "Deuda pendiente", 
            pendientes, 
            true
        );
        return new ResponseEntity<>(respuesta,HttpStatus.OK);
    }

    @PostMapping("/{idPago}/pagar")
    public ResponseEntity<ApiResponse<PagoDTO>> cobrar(@PathVariable Long idPago) {
        PagoDTO recibo = pagoService.registrarPago(idPago);
        return ResponseEntity.ok(new ApiResponse<>("Pago registrado correctamente", recibo, true));
    }
}

