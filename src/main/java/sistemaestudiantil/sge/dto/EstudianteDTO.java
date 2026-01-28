package sistemaestudiantil.sge.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import sistemaestudiantil.sge.enums.EstadoEstudiante;
import sistemaestudiantil.sge.enums.Generos;
import sistemaestudiantil.sge.enums.Roles;
import sistemaestudiantil.sge.enums.Sexos;
import sistemaestudiantil.sge.enums.TipoDocumento;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EstudianteDTO {
    private Long idEstudiante;
    private String carnet;
    private String nombres;
    private String apellidos;
    private String email;
    private String contrasenia;
    @JsonFormat(pattern = "dd-MM-yyyy") //Setea el formato a la fecha sino por defecto es AAAA-MM-DD
    private LocalDate fechaNacimiento;
    private String numeroTelefonico;
    private TipoDocumento documento;
    private String numeroDocumento;
    private Generos genero;
    private Sexos sexo;
    private EstadoEstudiante estado;
    private Double notaExamenGeneral;
    private Double notaExamenEspecifico;
    private String paisResidencia;
    private boolean debeCambiarClave;
    private Roles rol;
    private String documentoNIT;
    private String documentoDUI;
    private String documentoTitulo;
    private String fotoPerfil;

    private Long idCarrera;
    private String nombreCarrera;
}
