package sistemaestudiantil.sge.model;

import java.time.LocalDate;
import java.util.List;

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
    @JoinColumn(name="idEstudiante", nullable = false)
    private Estudiante estudiante;
    @ManyToOne
    @JoinColumn(name="idGrupo",nullable=false)
    private Grupo grupo;
    @OneToMany(mappedBy = "inscripcion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Evaluacion> evaluaciones;
}
