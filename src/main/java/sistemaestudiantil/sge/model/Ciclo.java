package sistemaestudiantil.sge.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ciclos")
public class Ciclo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCiclo;
    @Column(nullable = false, unique = true)
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDate inicioInscripcion; 
    private LocalDate finInscripcion;
    @Column(nullable = false)
    private Integer numeroCiclo;

    @Column(nullable = false)
    private Boolean activo;
}
