package sistemaestudiantil.sge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sistemaestudiantil.sge.model.Carrera;
import java.util.Optional;


@Repository
public interface CarreraRepository extends JpaRepository<Carrera,Long>{
    boolean existsByNombreCarrera(String nombreCarrera);

    Optional<Carrera> findByNombreCarrera(String nombreCarrera);
}

