package sistemaestudiantil.sge.model;

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
    @JoinColumn(name="id_facultad")
    private Facultad facultad;
}
