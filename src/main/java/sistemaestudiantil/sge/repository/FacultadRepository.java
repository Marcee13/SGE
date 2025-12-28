package sistemaestudiantil.sge.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sistemaestudiantil.sge.model.Facultad;

public interface FacultadRepository extends JpaRepository<Facultad, Long>{
    boolean existsByNombre(String nombre);
    
    Optional<Facultad> findByNombre(String nombre);
}
