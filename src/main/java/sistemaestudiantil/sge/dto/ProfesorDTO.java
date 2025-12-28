package sistemaestudiantil.sge.dto;

import lombok.Data;
import sistemaestudiantil.sge.enums.Generos;
import sistemaestudiantil.sge.enums.Sexos;
import sistemaestudiantil.sge.enums.TipoContratacion;
import sistemaestudiantil.sge.enums.TipoDocumento;

@Data
public class ProfesorDTO {
    private Long idProfesor;
    private String nombre;
    private String email;
    private TipoDocumento documento;
    private String numeroDocumento;
    private TipoContratacion tipoContratacion;
    private Generos genero;
    private Sexos sexo;
    private String paisResidencia;
}
