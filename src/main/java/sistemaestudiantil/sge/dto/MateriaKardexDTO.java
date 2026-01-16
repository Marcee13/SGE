package sistemaestudiantil.sge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MateriaKardexDTO {
    private String codigo;
    private String nombreAsignatura;
    private Integer uv;
    private Double notaFinal;
    private String estado;
    private Integer convocatoria;
}
