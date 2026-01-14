package sistemaestudiantil.sge.repository;

import org.springframework.stereotype.Repository;

import sistemaestudiantil.sge.enums.EstadoEstudiante;
import sistemaestudiantil.sge.model.Estudiante;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante,Long> {
    boolean existsByEmail(String email);
    boolean existsByNumeroDocumento(String numeroDocumento);

    Optional<Estudiante> findByEmail(String email);
    Optional<Estudiante> findByNumeroDocumento(String numeroDocumento);

    List<Estudiante> findByCarrera_Facultad_IdFacultad(Long idFacultad);

    @Query("SELECT e FROM Estudiante e WHERE e.estado IN :estados")
    List<Estudiante> buscarPorEstados(@Param("estados") List<EstadoEstudiante> estados);

    @Query("SELECT e.carnet FROM Estudiante e WHERE e.carnet LIKE :prefijo% ORDER BY e.carnet DESC LIMIT 1")
    Optional<String> findUltimoCarnet(@Param("prefijo") String prefijo);

    List<Estudiante> findByEstadoOrderByApellidosAsc(EstadoEstudiante estado);

    Optional<Estudiante> findByCarnet(String carnet);

    Optional<Estudiante> findByCarnetOrEmail(String carnet, String email);
}
