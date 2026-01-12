package sistemaestudiantil.sge.repository;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sistemaestudiantil.sge.enums.CicloAcademico;
import sistemaestudiantil.sge.model.Asignatura;
import sistemaestudiantil.sge.model.Grupo;

@Repository
public interface GrupoRespository extends JpaRepository<Grupo,Long>{
    boolean existsByCodigoGrupoAndAsignaturaAndCiclo(String codigoGrupo, Asignatura asignatura, CicloAcademico ciclo);

    @Query("SELECT g FROM Grupo g " +
           "WHERE g.profesor.idProfesor = :idProfesor " +
           "AND g.ciclo = :ciclo " +
           "AND g.dias = :dias " + 
           "AND ( " +
           "   (:horaInicio < g.horaFin) AND (:horaFin > g.horaInicio) " +
           ")")
    List<Grupo> encontrarGruposEnConflicto(@Param("idProfesor") Long idProfesor,
                                           @Param("ciclo") CicloAcademico ciclo,
                                           @Param("dias") String dias,
                                           @Param("horaInicio") LocalTime horaInicio,
                                           @Param("horaFin") LocalTime horaFin);

    @Query("SELECT COUNT(g) > 0 FROM Grupo g " +
           "WHERE g.profesor.idProfesor = :idProfesor " +
           "AND g.ciclo = :ciclo " +
           "AND g.dias = :dias " +
           "AND (" +
           "   (:horaInicio BETWEEN g.horaInicio AND g.horaFin) OR " +
           "   (:horaFin BETWEEN g.horaInicio AND g.horaFin) OR " +
           "   (g.horaInicio BETWEEN :horaInicio AND :horaFin)" +
           ")")
    boolean existeChoqueHorario(@Param("idProfesor") Long idProfesor,
                                @Param("ciclo") CicloAcademico ciclo,
                                @Param("dias") String dias,
                                @Param("horaInicio") LocalTime horaInicio,
                                @Param("horaFin") LocalTime horaFin);
}
