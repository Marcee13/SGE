package sistemaestudiantil.sge.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ArancelDTO {
    private Long idArancel;
    private String nombre;
    private String codigo;
    private BigDecimal costo;
    private Boolean esPorcentaje;
}
