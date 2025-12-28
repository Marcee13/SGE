package sistemaestudiantil.sge.service;

import java.util.List;

import org.springframework.stereotype.Service;

import sistemaestudiantil.sge.dto.FacultadDTO;
import sistemaestudiantil.sge.exceptions.DuplicadoException;
import sistemaestudiantil.sge.exceptions.RecursoNoencontradoException;
import sistemaestudiantil.sge.mapper.FacultadMapper;
import sistemaestudiantil.sge.model.Facultad;
import sistemaestudiantil.sge.repository.FacultadRepository;

@Service
public class FacultadService {
    private final FacultadRepository repository;
    private final FacultadMapper mapper;

    public FacultadService(FacultadRepository repository, FacultadMapper mapper){
        this.mapper=mapper;
        this.repository=repository;
    }

    public List<FacultadDTO> listarFacultades(){
        return repository.findAll().stream().map(mapper::toDTO).toList();
    }

    public FacultadDTO crearFacultad(FacultadDTO dto) {
        if (repository.existsByNombre(dto.getNombre())) {
            throw new DuplicadoException("La facultad '" + dto.getNombre() + "' ya existe.");
        }
        Facultad facultad = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(facultad));
    }

    public FacultadDTO actualizarFacultad(Long idFacultad, FacultadDTO dto){
        Facultad facultad=repository.findById(idFacultad).orElseThrow(()-> new RecursoNoencontradoException("La facultad con ID "+idFacultad+"no se encontró."));

        if (dto.getNombre() != null){
            repository.findByNombre(dto.getNombre()).ifPresent(facultadEncontrada->{
                if(!facultadEncontrada.getIdFacultad().equals(idFacultad)){
                    throw new DuplicadoException("El nombre "+dto.getNombre()+" ya esta en uso.");
                }
            });
            facultad.setNombre(dto.getNombre());
        }
        
        if(dto.getDescripcion()!=null) facultad.setDescripcion(dto.getDescripcion());

        return mapper.toDTO(repository.save(facultad));
    }
}
