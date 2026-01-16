package sistemaestudiantil.sge.dto;

import java.time.LocalTime;

import lombok.Data;

@Data
public class ProgramacionGrupoDTO {
    private Long idProfesor;
    private String dias;
    private LocalTime horaInicio;
    private LocalTime horaFin;
}
