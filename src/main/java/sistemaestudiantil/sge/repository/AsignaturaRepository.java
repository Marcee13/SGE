package sistemaestudiantil.sge.repository;

import org.springframework.stereotype.Repository;

import sistemaestudiantil.sge.model.Asignatura;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Repository //IMPORTANTE, no olvidar las etiquetas
public interface AsignaturaRepository extends JpaRepository<Asignatura,Long>{
    boolean existsByName(String name);

    @Query("SELECT a FROM Asignatura a WHERE MOD(a.nivelCiclo, 2) <> 0")
    List<Asignatura> buscarMateriasCicloImpar();

    @Query("SELECT a FROM Asignatura a WHERE MOD(a.nivelCiclo, 2) = 0")
    List<Asignatura> buscarMateriasCicloPar();
}
