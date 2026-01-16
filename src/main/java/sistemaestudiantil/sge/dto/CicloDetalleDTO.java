package sistemaestudiantil.sge.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CicloDetalleDTO {
    private String nombreCiclo;
    private List<MateriaKardexDTO> materias;
}
