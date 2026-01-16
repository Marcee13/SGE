package sistemaestudiantil.sge.dto;

import java.time.LocalTime;

import lombok.Data;
@Data
public class GrupoDTO {
    private Long idGrupo;
    private String codigoGrupo;
    private Long idAsignatura; 
    private Long idProfesor;
    private String nombreAsignatura;
    private String nombreProfesor;
    private Integer cuposDisponibles;
    private String dias;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Long idCiclo;
    private String nombreCiclo;
}
