package sistemaestudiantil.sge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sistemaestudiantil.sge.enums.EstadoPago;
import sistemaestudiantil.sge.model.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago,Long>{
    List<Pago> findByEstudiante_IdEstudianteOrderByFechaVencimientoAsc(Long idEstudiante);

    List<Pago> findByEstudiante_IdEstudianteAndEstadoOrderByFechaVencimientoAsc(Long idEstudiante, EstadoPago estado);

    boolean existsByEstudiante_IdEstudianteAndArancel_CodigoAndEstado(Long idEstudiante, String codigo, EstadoPago estado);

    Optional<Pago> findByCodigoPago(Long codigoPago);
}
