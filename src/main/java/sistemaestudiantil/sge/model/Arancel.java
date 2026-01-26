package sistemaestudiantil.sge.model;

import java.math.BigDecimal;

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
    @Column(precision = 10, scale = 2)
    private BigDecimal costo;
    @Column(name = "es_porcentaje")
    private Boolean esPorcentaje;
}
