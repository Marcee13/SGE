package sistemaestudiantil.sge.dto;

import lombok.Data;

@Data
public class DetalleComprobanteDTO {
    private String codigo;
    private String asignatura;
    private String grupo;
    private String dias;
    private String horario;
}
