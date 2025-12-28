package sistemaestudiantil.sge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EvaluacionDTO {
    private Long idInscripcion;
    private String nombreActividad;
    private Double porcentaje;
    private Double notaObtenida;
}