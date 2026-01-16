package sistemaestudiantil.sge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sistemaestudiantil.sge.model.Evaluacion;

public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long>{
    List<Evaluacion> findByInscripcion_IdInscripcion(Long idInscripcion);

    @Query("SELECT COALESCE(SUM(e.porcentaje), 0) FROM Evaluacion e WHERE e.inscripcion.idInscripcion = :idInscripcion")
    Double obtenerPorcentajeAcumulado(@Param("idInscripcion") Long idInscripcion);

    List<Evaluacion> findByInscripcionIdInscripcion(Long idInscripcion);
}
