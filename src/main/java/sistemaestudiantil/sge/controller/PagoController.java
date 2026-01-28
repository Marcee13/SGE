package sistemaestudiantil.sge.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sistemaestudiantil.sge.dto.AnulacionRequestDTO;
import sistemaestudiantil.sge.dto.CorteDiarioDTO;
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

    @GetMapping("/historial")
    public ResponseEntity<ApiResponse<List<PagoDTO>>> verHistorial(Authentication authentication, @RequestParam(required = false) Integer anio) {
        String identificador = authentication.getName();
        List<PagoDTO> talonario = pagoService.listarPagosPorEstudiante(identificador, anio);
        return new ResponseEntity<>(
        new ApiResponse<>("Historial de pagos obtenido exitosamente", talonario, true),
        HttpStatus.OK
    );
    }

    @GetMapping("/pendientes")
    public ResponseEntity<ApiResponse<List<PagoDTO>>> obtenerPendientes(Authentication authentication) {
        String usuarioLogueado = authentication.getName();
        List<PagoDTO> pendientes = pagoService.listarPendientes(usuarioLogueado);
        ApiResponse<List<PagoDTO>> respuesta = new ApiResponse<>(
            "Deuda pendiente", 
            pendientes, 
            true
        );
        return new ResponseEntity<>(respuesta,HttpStatus.OK);
    }

    @PostMapping("/pagar/{codigoPago}")
    public ResponseEntity<ApiResponse<PagoDTO>> cobrar(@PathVariable Long codigoPago) {
        PagoDTO recibo = pagoService.registrarPago(codigoPago);
        return ResponseEntity.ok(new ApiResponse<>("Pago registrado correctamente", recibo, true));
    }

    @PostMapping("/anular")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ADMINISTRATIVO')")
    public ResponseEntity<ApiResponse<Void>> anularPago(@RequestBody AnulacionRequestDTO request) {
        pagoService.anularPago(request.getCodigoPago(), request.getMotivo(), request.isRegenerar());
        return ResponseEntity.ok(new ApiResponse<>("Pago anulado correctamente", null, true));
    }

    @GetMapping("/corte-diario")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ADMINISTRATIVO')")
    public ResponseEntity<ApiResponse<CorteDiarioDTO>> verCorteDiario(@RequestParam(required = false) LocalDate fecha) {
        CorteDiarioDTO reporte = pagoService.generarCorteDiario(fecha);
        return ResponseEntity.ok(new ApiResponse<>("Corte de caja generado exitosamente", reporte, true)
        );
    }
}

