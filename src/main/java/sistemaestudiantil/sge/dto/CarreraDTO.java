package sistemaestudiantil.sge.dto;

import lombok.Data;

@Data
public class CarreraDTO {
    private Long idCarrera;
    private String nombreCarrera;
    private Boolean esCarreraEducacion;
    private String codigoCarrera;
    private String duracionCarrera;
    private String tituloQueOtorga;
    private Integer numeroAsignaturas;

    private Long idFacultad;
    private String nombreFacultad;
}
