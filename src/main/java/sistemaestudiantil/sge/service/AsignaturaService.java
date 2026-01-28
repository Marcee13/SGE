package sistemaestudiantil.sge.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import sistemaestudiantil.sge.dto.AsignaturaDTO;
import sistemaestudiantil.sge.exceptions.DuplicadoException;
import sistemaestudiantil.sge.exceptions.OperacionNoPermitidaException;
import sistemaestudiantil.sge.exceptions.RecursoNoencontradoException;
import sistemaestudiantil.sge.mapper.AsignaturaMapper;
import sistemaestudiantil.sge.model.Asignatura;
import sistemaestudiantil.sge.repository.AsignaturaRepository;

@Service //IMPORTANTE, no olvidar las etiquetas
public class AsignaturaService {
    private final AsignaturaRepository repository;
    private final AsignaturaMapper mapper;

    public AsignaturaService(AsignaturaRepository repository, AsignaturaMapper mapper){
        this.repository=repository;
        this.mapper=mapper;
    }
    
    @Transactional
    public AsignaturaDTO crearAsignatura(AsignaturaDTO dto){
        if(repository.existsByName(dto.getName())){
            throw new DuplicadoException("Ya existe una asignatura con los datos ingresados, por favor verifique.");
        }

        Asignatura entidad = mapper.toEntity(dto);
        entidad.setIdAsignatura(null);

        if(dto.getIdsPrerrequisitos()!=null&&!dto.getIdsPrerrequisitos().isEmpty()){
            List<Asignatura> prerrequisitos=repository.findAllById(dto.getIdsPrerrequisitos());
            if(prerrequisitos.size()!=dto.getIdsPrerrequisitos().size()){
                throw new RecursoNoencontradoException("Uno o más prerrequisitos no existen.");
            }
            validarCiclos(dto.getIdAsignatura(), prerrequisitos);
            entidad.setPrerrequisitos(prerrequisitos);
        }

        Asignatura guardado= repository.save(entidad);

        return mapper.toDTO(guardado);
    }

    public List<AsignaturaDTO> obtenerTodos() {
        List<Asignatura> lista = repository.findAll();
        return lista.stream()
                    .map(mapper::toDTO)
                    .toList();
    }

    @Transactional
    public AsignaturaDTO actualizarAsignatura(Long id, AsignaturaDTO dto) {
        Asignatura entidad = repository.findById(id).orElseThrow(() -> new RecursoNoencontradoException("Asignatura no encontrada"));

        mapper.actualizarEntidad(entidad, dto);

        if (dto.getIdsPrerrequisitos() != null) {
            List<Asignatura> nuevosPrerrequisitos = repository.findAllById(dto.getIdsPrerrequisitos());
            
            if (nuevosPrerrequisitos.size() != dto.getIdsPrerrequisitos().size()) {
                throw new RecursoNoencontradoException("Uno o más prerrequisitos no existen.");
            }

            validarCiclos(id, nuevosPrerrequisitos);

            entidad.setPrerrequisitos(nuevosPrerrequisitos);
        }

        Asignatura guardado = repository.save(entidad);
        return mapper.toDTO(guardado);
    }

    private void validarCiclos(Long idAsignaturaOriginal, List<Asignatura> candidatos) {
        if (idAsignaturaOriginal == null) return; 

        for (Asignatura candidato : candidatos) {
            if (candidato.getIdAsignatura().equals(idAsignaturaOriginal)) {
                throw new OperacionNoPermitidaException("Error: La asignatura '" + candidato.getName() + "' no puede ser prerrequisito de sí misma.");
            }

            if (candidato.getPrerrequisitos() != null && !candidato.getPrerrequisitos().isEmpty()) {
                validarCiclos(idAsignaturaOriginal, candidato.getPrerrequisitos());
            }
        }
    }
}
