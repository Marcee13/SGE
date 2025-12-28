package sistemaestudiantil.sge.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "aranceles")
public class Arancel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idArancel;
    private String nombre;
    private String codigo;
    private Double costo;
}
