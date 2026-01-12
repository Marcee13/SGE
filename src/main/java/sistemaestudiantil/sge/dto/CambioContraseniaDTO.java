package sistemaestudiantil.sge.dto;

import lombok.Data;

@Data
public class CambioContraseniaDTO {
    private Long idEstudiante;
    private String contraseniaActual;
    private String nuevaContrasenia;
}