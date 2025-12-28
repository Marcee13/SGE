package sistemaestudiantil.sge.dto;

import lombok.Data;
import sistemaestudiantil.sge.enums.CicloAcademico;

@Data
public class GrupoDTO {
    private Long idGrupo;
    private String codigoGrupo;
    private CicloAcademico ciclo;
    private Long idAsignatura; 
    private Long idProfesor;
    private String nombreAsignatura;
    private String nombreProfesor;
}
