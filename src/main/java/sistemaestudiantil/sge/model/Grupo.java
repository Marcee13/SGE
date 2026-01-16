package sistemaestudiantil.sge.model;

import java.time.LocalTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="grupos")
public class Grupo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idGrupo;
    private String codigoGrupo;
    @ManyToOne
    @JoinColumn(name="idProfesor")
    private Profesor profesor;
    @ManyToOne
    @JoinColumn(name="idAsignatura")
    private Asignatura asignatura;
    @Column(name = "cupos_disponibles", nullable = false)
    private Integer cuposDisponibles;
    @Column(nullable = false)
    private String dias;
    @Column(nullable = false)
    private LocalTime horaInicio;
    @Column(nullable = false)
    private LocalTime horaFin;
    @ManyToOne
    @JoinColumn(name = "id_ciclo", nullable = false)
    private Ciclo ciclo;
}
