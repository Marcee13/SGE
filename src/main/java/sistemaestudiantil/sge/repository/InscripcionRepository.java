package sistemaestudiantil.sge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sistemaestudiantil.sge.model.Estudiante;
import sistemaestudiantil.sge.model.Grupo;
import sistemaestudiantil.sge.model.Inscripcion;
import java.util.List;


@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long>{
    boolean existsByEstudianteAndGrupo(Estudiante estudiante, Grupo grupo);

    List<Inscripcion> findByEstudiante_IdEstudiante(Long idEstudiante);

    @Query("SELECT i FROM Inscripcion i LEFT JOIN FETCH i.evaluaciones WHERE i.estudiante.idEstudiante = :idEstudiante")
    List<Inscripcion> findHistorialCompleto(@Param("idEstudiante") Long idEstudiante);

    @Query("SELECT COUNT(i) > 0 FROM Inscripcion i " +
           "WHERE i.estudiante.idEstudiante = :idEstudiante " +
           "AND i.grupo.asignatura.idAsignatura = :idAsignatura " + 
           "AND i.estadoInscripcion = 'APROBADO'")
    boolean haAprobadoMateria(@Param("idEstudiante") Long idEstudiante, 
                              @Param("idAsignatura") Long idAsignatura);
}
