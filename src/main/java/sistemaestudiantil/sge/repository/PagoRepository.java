package sistemaestudiantil.sge.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sistemaestudiantil.sge.enums.EstadoPago;
import sistemaestudiantil.sge.model.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago,Long>{
    List<Pago> findByEstudiante_IdEstudianteOrderByFechaVencimientoAsc(Long idEstudiante);

    List<Pago> findByEstudiante_IdEstudianteAndEstadoOrderByFechaVencimientoAsc(Long idEstudiante, EstadoPago estado);

    boolean existsByEstudiante_IdEstudianteAndArancel_CodigoAndEstado(Long idEstudiante, String codigo, EstadoPago estado);

    Optional<Pago> findByCodigoPago(Long codigoPago);

    @Query("SELECT p.arancel.nombre, SUM(p.monto), COUNT(p) " +
           "FROM Pago p " +
           "WHERE p.fechaPago = :fecha AND p.estado = 'PAGADO' " +
           "GROUP BY p.arancel.nombre")
    List<Object[]> obtenerResumenPorFecha(@Param("fecha") LocalDate fecha);

    @Query("SELECT p FROM Pago p WHERE p.estudiante.idEstudiante = :idEstudiante AND YEAR(p.fechaVencimiento) = :anio ORDER BY p.fechaVencimiento ASC")
    List<Pago> findByEstudianteAndAnio(@Param("idEstudiante") Long idEstudiante, @Param("anio") int anio);

    @Query("SELECT COUNT(p) > 0 FROM Pago p " +
           "WHERE p.estudiante.idEstudiante = :idEstudiante " +
           "AND p.arancel.codigo = 'MATRICULA' " +
           "AND YEAR(p.fechaVencimiento) = :anio")
    boolean existeTalonario(@Param("idEstudiante") Long idEstudiante, @Param("anio") int anio);
}
