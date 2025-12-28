package sistemaestudiantil.sge.dto;

import java.util.List;

import lombok.Data;

@Data
public class HistorialDTO {
    private String nombreMateria;
    private String ciclo;
    private String estado;
    private Double notaFinal;
    private List<EvaluacionDTO> evaluaciones;
}
