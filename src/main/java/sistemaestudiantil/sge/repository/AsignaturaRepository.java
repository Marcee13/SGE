package sistemaestudiantil.sge.repository;

import org.springframework.stereotype.Repository;

import sistemaestudiantil.sge.model.Asignatura;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository //IMPORTANTE, no olvidar las etiquetas
public interface AsignaturaRepository extends JpaRepository<Asignatura,Long>{
    boolean existsByName(String name);
}
