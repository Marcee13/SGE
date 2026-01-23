package sistemaestudiantil.sge.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="carrera")
public class Carrera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCarrera;
    
    @Column(nullable = false,unique = true)
    private String nombreCarrera;
    @Column(name = "es_carrera_educacion",nullable = false)
    private Boolean esCarreraEducacion;
    private String codigoCarrera;
    private String duracionCarrera;
    private String tituloQueOtorga;
    private Integer numeroAsignaturas;

    @ManyToOne
    @JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class, 
        property = "id"
    )
    @JoinColumn(name="id_facultad")
    private Facultad facultad;
}
