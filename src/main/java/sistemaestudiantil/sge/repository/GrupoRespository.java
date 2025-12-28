package sistemaestudiantil.sge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sistemaestudiantil.sge.enums.CicloAcademico;
import sistemaestudiantil.sge.model.Asignatura;
import sistemaestudiantil.sge.model.Grupo;

@Repository
public interface GrupoRespository extends JpaRepository<Grupo,Long>{
    boolean existsByCodigoGrupoAndAsignaturaAndCiclo(String codigoGrupo, Asignatura asignatura, CicloAcademico ciclo);
}
