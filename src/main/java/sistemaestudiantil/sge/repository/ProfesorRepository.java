package sistemaestudiantil.sge.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sistemaestudiantil.sge.model.Profesor;

@Repository
public interface ProfesorRepository extends JpaRepository<Profesor, Long>{
    boolean existsByEmail(String email);
    boolean existsByNumeroDocumento(String numeroDocumeto);

    Optional<Profesor> findByEmail(String email);
    Optional<Profesor> findByNumeroDocumento(String numeroDocumento);
}
