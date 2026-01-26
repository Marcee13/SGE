package sistemaestudiantil.sge.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class CorteDiarioDTO {
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate fecha;
    private BigDecimal totalIngresos;
    private Long totalTransacciones;
    private List<DetalleCorteDTO> desglose;
}
