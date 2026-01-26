package sistemaestudiantil.sge.dto;

import lombok.Data;

@Data
public class AnulacionRequestDTO {
    private Long codigoPago;
    private String motivo;
    private boolean regenerar;
}
