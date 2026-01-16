package sistemaestudiantil.sge.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CicloDTO {
    private Long idCiclo;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDate inicioInscripcion;
    private LocalDate finInscripcion;
    private Boolean activo;
    private Integer numeroCiclo;
}
