package sistemaestudiantil.sge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sistemaestudiantil.sge.enums.CicloAcademico;
import sistemaestudiantil.sge.enums.EstadoInscripcion;
import sistemaestudiantil.sge.model.Estudiante;
import sistemaestudiantil.sge.model.Grupo;
import sistemaestudiantil.sge.model.Inscripcion;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long>{
    boolean existsByEstudianteAndGrupo(Estudiante estudiante, Grupo grupo);

    boolean existsByIdInscripcionAndEstudiante_IdEstudiante(Long idInscripcion, Long idEstudiante);
    
    long countByGrupo(Grupo grupo);

    List<Inscripcion> findByEstudiante_IdEstudiante(Long idEstudiante);
    
    boolean existsByIdInscripcionAndGrupo_Profesor_Email(Long idInscripcion, String emailProfesor);

    @Query("SELECT i FROM Inscripcion i " + "WHERE i.estudiante.idEstudiante = :idEstudiante " + "AND i.grupo.asignatura.idAsignatura = :idAsignatura " + "AND i.grupo.ciclo = :ciclo")
    Optional<Inscripcion> findInscripcionActiva(@Param("idEstudiante") Long idEstudiante, @Param("idAsignatura") Long idAsignatura, @Param("ciclo") CicloAcademico ciclo);
    
    @Query("SELECT i FROM Inscripcion i WHERE i.grupo.ciclo = :ciclo AND i.estadoInscripcion = 'INSCRITO'")
    List<Inscripcion> findPendientesDeCierre(@Param("ciclo") CicloAcademico ciclo);

    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END " +
           "FROM Inscripcion i " +
           "JOIN i.grupo g " +
           "JOIN g.profesor p " +
           "WHERE i.idInscripcion = :idInscripcion " +
           "AND (p.email = :identificador OR p.codigoEmpleado = :identificador)")
    boolean esInscripcionDeProfesor(@Param("idInscripcion") Long idInscripcion, 
                                    @Param("identificador") String identificador);

    @Query("SELECT i FROM Inscripcion i " +
           "JOIN i.grupo g " +
           "WHERE i.estudiante.idEstudiante = :idEstudiante " +
           "AND g.ciclo = :ciclo " +
           "AND g.dias = :dias " +
           "AND i.estadoInscripcion = 'INSCRITO' " +
           "AND ( " +
           "   (:horaInicio < g.horaFin) AND (:horaFin > g.horaInicio) " +
           ")")
    List<Inscripcion> encontrarChoquesHorarioEstudiante(
           @Param("idEstudiante") Long idEstudiante,
           @Param("ciclo") CicloAcademico ciclo,
           @Param("dias") String dias,
           @Param("horaInicio") LocalTime horaInicio,
           @Param("horaFin") LocalTime horaFin);

    @Query("SELECT i FROM Inscripcion i " +
           "JOIN FETCH i.grupo g " +
           "JOIN FETCH g.asignatura a " +
           "LEFT JOIN FETCH i.evaluaciones e " +
           "WHERE i.estudiante.idEstudiante = :idEstudiante " +
           "ORDER BY i.fechaInscripcion DESC")
    List<Inscripcion> findHistorialCompleto(@Param("idEstudiante") Long idEstudiante);

    @Query("SELECT COUNT(i) > 0 FROM Inscripcion i " +
           "WHERE i.estudiante.idEstudiante = :idEstudiante " +
           "AND i.grupo.asignatura.idAsignatura = :idAsignatura " +
           "AND i.grupo.ciclo = :ciclo")
    boolean yaEstaInscritoEnLaMateria(@Param("idEstudiante") Long idEstudiante, @Param("idAsignatura") Long idAsignatura, @Param("ciclo") CicloAcademico ciclo);

    @Query("SELECT COUNT(i) > 0 FROM Inscripcion i " +
           "WHERE i.estudiante.idEstudiante = :idEstudiante " +
           "AND i.grupo.asignatura.idAsignatura = :idAsignatura " + 
           "AND i.estadoInscripcion = :estado")
    boolean haAprobadoMateria(@Param("idEstudiante") Long idEstudiante, @Param("idAsignatura") Long idAsignatura, @Param("estado") EstadoInscripcion estado);
}
