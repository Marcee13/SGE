package sistemaestudiantil.sge.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class CorteDiarioDTO {
    private LocalDate fecha;
    private BigDecimal totalIngresos;
    private Long totalTransacciones;
    private List<DetalleCorteDTO> desglose;
}
