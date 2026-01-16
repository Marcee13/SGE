package sistemaestudiantil.sge.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name="asignatura")
public class Asignatura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAsignatura;
    private String name;
    private int uv;
    @Column(unique = true) 
    private String codigo;
    @Column(nullable = false)
    private Integer nivelCiclo;
    @OneToMany(mappedBy = "asignatura", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Grupo> grupos;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "materia_prerrequisitos",
        joinColumns = @JoinColumn(name = "id_asignatura"),
        inverseJoinColumns = @JoinColumn(name = "id_prerrequisito")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Asignatura> prerrequisitos = new ArrayList<>();
}
