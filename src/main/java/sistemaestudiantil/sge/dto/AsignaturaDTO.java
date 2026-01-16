package sistemaestudiantil.sge.dto;

import java.util.List;

import lombok.Data;
    
@Data //IMPORTANTE, no olvidar las etiquetas
public class AsignaturaDTO {
    private Long idAsignatura;
    private String name;
    private int uv;
    private String codigo;
    private List<Long> idsPrerrequisitos;
    private Integer nivelCiclo;
}
