package sistemaestudiantil.sge.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import sistemaestudiantil.sge.dto.AsignaturaDTO;
import sistemaestudiantil.sge.exceptions.DuplicadoException;
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
}
