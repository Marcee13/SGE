package sistemaestudiantil.sge.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import sistemaestudiantil.sge.enums.Generos;
import sistemaestudiantil.sge.enums.Sexos;
import sistemaestudiantil.sge.enums.TipoContratacion;
import sistemaestudiantil.sge.enums.TipoDocumento;

@Data
@Entity
@Table(name="profesores")
public class Profesor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProfesor;
    private String nombre;
    private String apellidos;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private TipoDocumento documento;
    private String numeroDocumento;
    @Enumerated(EnumType.STRING)
    private TipoContratacion tipoContratacion;
    @Enumerated(EnumType.STRING)
    private Generos genero;
    @Enumerated(EnumType.STRING)
    private Sexos sexo;
    private String paisResidencia;
    private String especialidad;
    private Boolean activo=true;
    @Column(nullable = false, unique = true)
    private String codigoEmpleado;

    @OneToMany(mappedBy = "profesor")
    private List<Grupo> grupos;
}
