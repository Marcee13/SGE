package sistemaestudiantil.sge.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lowagie.text.DocumentException;

import jakarta.servlet.http.HttpServletResponse;
import sistemaestudiantil.sge.dto.ComprobanteDTO;
import sistemaestudiantil.sge.response.ApiResponse;
import sistemaestudiantil.sge.service.ReporteService;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {
    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/kardex/mis-notas")
    public void descargarMiKardex(Authentication authentication, HttpServletResponse response) throws IOException, DocumentException {
        String carnetLogueado = authentication.getName(); 

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=kardex_personal.pdf");

        reporteService.generarKardexPdfPorUsuario(response, carnetLogueado);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/kardex-admin/{idEstudiante}")
    public void descargarKardexAdmin(@PathVariable Long idEstudiante, HttpServletResponse response) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=kardex_" + idEstudiante + ".pdf";
        response.setHeader(headerKey, headerValue);
        
        reporteService.generarKardexPdf(response, idEstudiante);
    }

    @GetMapping("/inscripcion/mis-datos")
    public ResponseEntity<ApiResponse<ComprobanteDTO>> obtenerMisDatosInscripcion(Authentication authentication) {
        
        String carnetLogueado = authentication.getName();

        ComprobanteDTO datos = reporteService.obtenerDatosComprobantePorUsuario(carnetLogueado);
        
        return ResponseEntity.ok(new ApiResponse<>(
            "Datos de inscripción recuperados.",
            datos,
            true
        ));
    }

    @GetMapping("/inscripcion/mi-boleta")
    public void descargarMiBoletaInscripcion(Authentication authentication, HttpServletResponse response) throws IOException, DocumentException {

        String carnetLogueado = authentication.getName();

        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=boleta_inscripcion_personal.pdf";
        response.setHeader(headerKey, headerValue);

        reporteService.generarBoletaPdfPorUsuario(response, carnetLogueado);
    }
}
