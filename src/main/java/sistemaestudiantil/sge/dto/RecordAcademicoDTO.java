package sistemaestudiantil.sge.dto;

import java.util.List;

import lombok.Data;

@Data
public class RecordAcademicoDTO {
    private String carnet;
    private String nombreCompleto;
    private String carrera;
    private Double cumActual;       
    private Integer uvAprobadas;    

    private List<CicloKardexDTO> ciclos;
}
