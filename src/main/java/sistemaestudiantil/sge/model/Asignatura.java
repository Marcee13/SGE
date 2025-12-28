package sistemaestudiantil.sge.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="asignatura")
public class Asignatura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAsignatura;

    private String name;
    private int uv;

    @OneToMany(mappedBy = "asignatura", cascade = CascadeType.ALL)
    private List<Grupo> grupos;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "materia_prerrequisitos",
        joinColumns = @JoinColumn(name = "id_asignatura"),
        inverseJoinColumns = @JoinColumn(name = "id_prerrequisito")
    )
    private List<Asignatura> prerrequisitos = new ArrayList<>();
}
