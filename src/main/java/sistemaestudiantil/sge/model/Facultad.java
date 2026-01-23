package sistemaestudiantil.sge.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="facultades")
public class Facultad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFacultad;
    @Column(nullable = false, unique = true)
    private String nombre;
    @Column(length = 500)
    private String descripcion;

    @OneToMany(mappedBy = "facultad", cascade = CascadeType.ALL)
    @JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class, 
        property = "id"
    )
    private List<Carrera> carreras;
}
