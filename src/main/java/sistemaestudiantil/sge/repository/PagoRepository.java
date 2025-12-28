package sistemaestudiantil.sge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sistemaestudiantil.sge.model.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago,Long>{
    List<Pago> findByEstudiante_IdEstudianteOrderByFechaVencimientoAsc(Long idEstudiante);
}
