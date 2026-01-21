package sistemaestudiantil.sge.dto;

import java.util.List;

import lombok.Data;

@Data
public class DashboardDTO {
    private Long totalEstudiantes;
    private Long totalDocentes;
    private Long materiasInscritasTotal;
    private List<ItemEstadisticoDTO> estudiantesPorCarrera;
    private List<ItemEstadisticoDTO> topMateriasReprobadas;
}
