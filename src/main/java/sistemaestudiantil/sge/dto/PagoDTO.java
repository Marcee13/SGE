package sistemaestudiantil.sge.dto;

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
    private Long idEstudiante;
    private String nombreArancel;
    private Double monto;
    private LocalDate fechaVencimiento;
    private LocalDate fechaPago;
    private EstadoPago estado;
}
