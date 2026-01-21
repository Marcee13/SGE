package sistemaestudiantil.sge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import sistemaestudiantil.sge.dto.ItemEstadisticoDTO;
import sistemaestudiantil.sge.enums.EstadoInscripcion;
import sistemaestudiantil.sge.model.Ciclo;
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
       Optional<Inscripcion> findInscripcionActiva(@Param("idEstudiante") Long idEstudiante, @Param("idAsignatura") Long idAsignatura, @Param("ciclo") Ciclo ciclo);

       @Query("SELECT i FROM Inscripcion i WHERE i.grupo.ciclo = :ciclo AND i.estadoInscripcion = 'INSCRITO'")
       List<Inscripcion> findPendientesDeCierre(@Param("ciclo") Ciclo ciclo);

       @Query("SELECT i FROM Inscripcion i WHERE i.estudiante.idEstudiante = :idEstudiante " +
       "AND (i.estadoInscripcion = 'APROBADO' OR i.estadoInscripcion = 'REPROBADO') " +
       "ORDER BY i.grupo.ciclo.fechaInicio ASC") 
       List<Inscripcion> findHistorialConNotas(@Param("idEstudiante") Long idEstudiante);

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
              @Param("ciclo") Ciclo ciclo,
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
              "AND i.grupo.ciclo = :ciclo " +
              "AND i.estadoInscripcion = 'INSCRITO'")
       boolean yaEstaInscritoEnLaMateria(@Param("idEstudiante") Long idEstudiante, @Param("idAsignatura") Long idAsignatura, @Param("ciclo") Ciclo ciclo);

       @Query("SELECT COUNT(i) > 0 FROM Inscripcion i " +
              "WHERE i.estudiante.idEstudiante = :idEstudiante " +
              "AND i.grupo.asignatura.idAsignatura = :idAsignatura " + 
              "AND i.estadoInscripcion = :estado")
       boolean haAprobadoMateria(@Param("idEstudiante") Long idEstudiante, @Param("idAsignatura") Long idAsignatura, @Param("estado") EstadoInscripcion estado);

       @Query("SELECT new sistemaestudiantil.sge.dto.ItemEstadisticoDTO(i.grupo.asignatura.name, COUNT(i)) " +
              "FROM Inscripcion i " +
              "WHERE i.estadoInscripcion = 'REPROBADO' " +
              "GROUP BY i.grupo.asignatura.name " +
              "ORDER BY COUNT(i) DESC")
       List<ItemEstadisticoDTO> encontrarTopMateriasReprobadas(Pageable pageable);

       @Query("SELECT COUNT(i) FROM Inscripcion i WHERE i.estadoInscripcion = 'INSCRITO'")
       long contarInscripcionesActivas();
}
