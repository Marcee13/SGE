package sistemaestudiantil.sge.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sistemaestudiantil.sge.enums.EstadoPago;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagoDTO {
    private Long idPago;
    private Long codigoPago;
    private Long idEstudiante;
    private String nombreArancel;
    private BigDecimal monto;
    private LocalDate fechaVencimiento;
    private LocalDate fechaPago;
    private EstadoPago estado;
    private String observaciones;
}
