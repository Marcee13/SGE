package sistemaestudiantil.sge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import sistemaestudiantil.sge.dto.DashboardDTO;
import sistemaestudiantil.sge.response.ApiResponse;
import sistemaestudiantil.sge.service.EstadisticaService;

@Controller
@RequestMapping("/api/estadisticas")
public class EstadisticaController {
    private final EstadisticaService estadisticaService;

    public EstadisticaController(EstadisticaService estadisticaService) {
        this.estadisticaService = estadisticaService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ADMINISTRATIVO')")
    public ResponseEntity<ApiResponse<DashboardDTO>> obtenerDashboard() {
        
        DashboardDTO datos = estadisticaService.obtenerMetricasDashboard();
        
        return ResponseEntity.ok(new ApiResponse<>(
            "Métricas cargadas exitosamente.",
            datos,
            true
        ));
    }
}
