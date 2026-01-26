package sistemaestudiantil.sge.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.*;
import lombok.Data;
import sistemaestudiantil.sge.enums.EstadoPago;

@Data
@Entity
@Table(name = "pagos")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPago;
    private Long codigoPago;
    private String observaciones;

    @ManyToOne
    @JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class, 
        property = "id"
    )
    
    @JoinColumn(name = "id_estudiante")
    private Estudiante estudiante;
    
    @ManyToOne
    @JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class, 
        property = "id"
    )
    @JoinColumn(name = "id_arancel")
    private Arancel arancel;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal monto;
    private LocalDate fechaVencimiento;
    private LocalDate fechaPago;
    @Enumerated(EnumType.STRING)
    private EstadoPago estado;
}
