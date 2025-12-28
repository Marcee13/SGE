package sistemaestudiantil.sge.model;

import jakarta.persistence.*;
import lombok.Data;
import sistemaestudiantil.sge.enums.CicloAcademico;

@Data
@Entity
@Table(name="grupos")
public class Grupo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idGrupo;
    private String codigoGrupo;
    @Enumerated(EnumType.STRING)
    private CicloAcademico ciclo;
    @ManyToOne
    @JoinColumn(name="idProfesor")
    private Profesor profesor;
    @ManyToOne
    @JoinColumn(name="idAsignatura")
    private Asignatura asignatura;


}
