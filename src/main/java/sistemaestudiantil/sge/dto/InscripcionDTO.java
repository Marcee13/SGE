package sistemaestudiantil.sge.dto;

import java.time.LocalDate;

import lombok.Data;
import sistemaestudiantil.sge.enums.EstadoInscripcion;

@Data
public class InscripcionDTO {
    private Long idInscripcion;
    private Long idEstudiante;
    private Long idGrupo;
    private String nombreEstudiante;
    private String codigoGrupo;
    private LocalDate fechaInscripcion;
    private String nombreMateria;
    private EstadoInscripcion estado;
    private Double notaFinal;
}
