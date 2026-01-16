package sistemaestudiantil.sge.dto;

import java.util.List;
import java.time.LocalDate;

import lombok.Data;

@Data
public class ComprobanteDTO {
    private String carnet;
    private String estudiante;
    private String carrera;
    private String ciclo; 
    private LocalDate fechaGeneracion;
    private List<DetalleComprobanteDTO> materias;
}
