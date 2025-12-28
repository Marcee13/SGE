package sistemaestudiantil.sge.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import sistemaestudiantil.sge.dto.CarreraDTO;
import sistemaestudiantil.sge.exceptions.DuplicadoException;
import sistemaestudiantil.sge.exceptions.RecursoNoencontradoException;
import sistemaestudiantil.sge.mapper.CarreraMapper;
import sistemaestudiantil.sge.model.Carrera;
import sistemaestudiantil.sge.model.Facultad;
import sistemaestudiantil.sge.repository.CarreraRepository;
import sistemaestudiantil.sge.repository.FacultadRepository;

@Service
public class CarreraService {
    private final CarreraRepository repository;
    private final CarreraMapper mapper;
    private final FacultadRepository facultadRepository;

    public CarreraService(CarreraMapper mapper, CarreraRepository repository, FacultadRepository facultadRepository){
        this.mapper=mapper;
        this.repository=repository;
        this.facultadRepository=facultadRepository;
    }

    public CarreraDTO crearCarrera(CarreraDTO dto){
        if(repository.existsByNombreCarrera(dto.getNombreCarrera())){
            throw new DuplicadoException("La carrera "+dto.getNombreCarrera()+" ya existe");
        }

        Carrera entidad =mapper.toEntity(dto);

        if (dto.getIdFacultad() != null) {
            Facultad facultad = facultadRepository.findById(dto.getIdFacultad())
                .orElseThrow(() -> new EntityNotFoundException("Facultad no encontrada"));
            entidad.setFacultad(facultad);
        }

        return mapper.toDTO(repository.save(entidad));
    }

    public List<CarreraDTO> listarCarreras(){
        return repository.findAll().stream().map(mapper::toDTO).toList();
    }

    public CarreraDTO actualizarCarrera(Long id, CarreraDTO dto){
        Carrera carrera=repository.findById(id).orElseThrow(()->new RecursoNoencontradoException("Carrera con ID "+id+" no encontrada."));

        repository.findByNombreCarrera(dto.getNombreCarrera()).ifPresent(carreraEncontrada ->{
            if(!carreraEncontrada.getIdCarrera().equals(id)){
                throw new DuplicadoException("El nombre "+dto.getNombreCarrera()+" ya esta en uso.");
            }
        });

        if(dto.getNombreCarrera()!=null) carrera.setNombreCarrera((dto.getNombreCarrera()));
        if(dto.getEsCarreraEducacion()!=null) carrera.setEsCarreraEducacion(dto.getEsCarreraEducacion());
        if(dto.getCodigoCarrera()!=null) carrera.setCodigoCarrera(dto.getCodigoCarrera());
        if(dto.getDuracionCarrera()!=null) carrera.setDuracionCarrera(dto.getDuracionCarrera());
        if(dto.getNumeroAsignaturas()!=null) carrera.setNumeroAsignaturas(dto.getNumeroAsignaturas());
        if(dto.getTituloQueOtorga()!=null) carrera.setTituloQueOtorga(dto.getTituloQueOtorga());

        if (dto.getIdFacultad() != null) {
            Facultad facultad = facultadRepository.findById(dto.getIdFacultad()).orElseThrow(() -> new RecursoNoencontradoException("Facultad no encontrada con ID: " + dto.getIdFacultad()));
            
            carrera.setFacultad(facultad);
        }

        return mapper.toDTO(repository.save(carrera));
    }
}
