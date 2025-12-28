package sistemaestudiantil.sge.dto;

import lombok.Data;

@Data
public class ArancelDTO {
    private Long idArancel;
    private String nombre;
    private String codigo;
    private Double costo;
}
