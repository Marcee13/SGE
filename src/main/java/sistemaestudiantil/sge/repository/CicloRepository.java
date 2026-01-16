package sistemaestudiantil.sge.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sistemaestudiantil.sge.model.Ciclo;

@Repository
public interface CicloRepository extends JpaRepository<Ciclo, Long>{
    Optional<Ciclo> findByActivoTrue();

    boolean existsByNombre(String nombre);
}
