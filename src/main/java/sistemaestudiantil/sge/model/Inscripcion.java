package sistemaestudiantil.sge.model;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.*;
import lombok.Data;
import sistemaestudiantil.sge.enums.EstadoInscripcion;

@Data
@Entity
@Table(name="inscripciones")
public class Inscripcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInscripcion;
    @Column(nullable = false)
    private LocalDate fechaInscripcion;
    @Enumerated(EnumType.STRING)
    private EstadoInscripcion estadoInscripcion;
    private Double notaFinal;
    
    @ManyToOne
    @JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class, 
        property = "id"
    )
    @JoinColumn(name="idEstudiante", nullable = false)
    private Estudiante estudiante;
    
    @ManyToOne
    @JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class, 
        property = "id"
    )
    @JoinColumn(name="idGrupo",nullable=false)
    private Grupo grupo;
    
    @OneToMany(mappedBy = "inscripcion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class, 
        property = "id"
    )
    private List<Evaluacion> evaluaciones;
}
