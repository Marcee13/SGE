package sistemaestudiantil.sge.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sistemaestudiantil.sge.model.Arancel;

@Repository
public interface ArancelRepository extends JpaRepository<Arancel, Long>{
    boolean existsByNombre(String nombre);

    Optional<Arancel>findByCodigo(String string);
}
