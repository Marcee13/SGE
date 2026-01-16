package sistemaestudiantil.sge.dto;

import java.util.List;

import lombok.Data;

@Data
public class AvanceAcademicoDTO {
    private String nombreEstudiante;
    private String carnet;
    private String nombreCarrera;
    private Integer materiasAprobadas;
    private Integer totalMateriasPlan;
    private Double porcentajeAvance;
    private Double cum;
    private List<CicloDetalleDTO> ciclos;
}
