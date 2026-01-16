package sistemaestudiantil.sge.dto;

import java.util.List;

import lombok.Data;

@Data
public class CicloKardexDTO {
    private String nombreCiclo;
    private List<MateriaKardexDTO> materias; 
}