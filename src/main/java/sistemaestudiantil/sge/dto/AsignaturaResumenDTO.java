package sistemaestudiantil.sge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AsignaturaResumenDTO {
    private Long id;
    private String codigo;
    private String nombre;
}
