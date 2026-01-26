package sistemaestudiantil.sge.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DetalleCorteDTO {
    private String concepto;
    private BigDecimal total;
    private Long cantidad;
}
