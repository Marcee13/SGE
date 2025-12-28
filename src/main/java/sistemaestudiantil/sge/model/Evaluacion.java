package sistemaestudiantil.sge.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="evaluaciones")
public class Evaluacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEvaluacion;
    @Column(nullable = false)
    private String nombreActividad;
    @Column(nullable = false)
    private Double porcentaje;
    @Column(nullable = false)
    private Double notaObtenida;
    @ManyToOne
    @JoinColumn(name="idInscripcion", nullable=false)
    private Inscripcion inscripcion;
}
