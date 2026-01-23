package sistemaestudiantil.sge.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.*;
import lombok.Data;
import sistemaestudiantil.sge.enums.EstadoEstudiante;
import sistemaestudiantil.sge.enums.Generos;
import sistemaestudiantil.sge.enums.Roles;
import sistemaestudiantil.sge.enums.Sexos;
import sistemaestudiantil.sge.enums.TipoDocumento;

@Data
@Entity
@Table (name="estudiantes")
public class Estudiante {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long idEstudiante;
    @Column (name="carnet", unique=true, nullable=true)
    private String carnet;
    private String nombres;
    private String apellidos;
    private String email;
    private String contrasenia;
    private LocalDate fechaNacimiento;
    private String numeroTelefonico;
    @Enumerated(EnumType.STRING)
    private TipoDocumento documento;
    private String numeroDocumento;
    @Enumerated(EnumType.STRING)
    private Generos genero;
    @Enumerated(EnumType.STRING)
    private EstadoEstudiante estado;
    private String paisResidencia;
    @Enumerated(EnumType.STRING)
    private Sexos sexo;
    @Column(nullable = true)
    private Double notaExamenGeneral;
    @Column(nullable = true)
    private Double notaExamenEspecifico;
    @Column(name = "esta_activo")
    private Boolean estaActivo = true;
    @Column(name = "debe_cambiar_clave")
    private Boolean debeCambiarClave=false;
    private Roles rol;
    @Column(name = "foto_perfil")
    private String fotoPerfil;
    @Column(name = "documento_titulo")
    private String documentoTitulo;
    @Column(name = "documento_DUI")
    private String documentoDUI;
    @Column(name = "documento_NIT")
    private String documentoNIT;

    @ManyToOne
    @JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class, 
        property = "id"
    )
    @JoinColumn(name = "id_carrera")
    private Carrera carrera;

    public boolean estaActivo(){
        return this.estado!=null&&this.estado.esActivo();
    }
}