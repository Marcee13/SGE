package sistemaestudiantil.sge.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import sistemaestudiantil.sge.dto.ProfesorDTO;
import sistemaestudiantil.sge.enums.TipoDocumento;
import sistemaestudiantil.sge.exceptions.DuplicadoException;
import sistemaestudiantil.sge.mapper.ProfesorMapper;
import sistemaestudiantil.sge.model.Profesor;
import sistemaestudiantil.sge.repository.ProfesorRepository;

@Service
public class ProfesorService {
    private final ProfesorRepository repository;
    private final ProfesorMapper mapper;

    public ProfesorService(ProfesorRepository repository, ProfesorMapper mapper){
        this.repository=repository;
        this.mapper=mapper;
    }

    public ProfesorDTO crearProfesor(ProfesorDTO dto){

        validarUnico(dto);

        String numeroLimpio = dto.getNumeroDocumento() != null ? dto.getNumeroDocumento().trim() : null;
        if(dto.getDocumento()!=null&&numeroLimpio!=null){
            dto.getDocumento().validar(numeroLimpio);
        }

        Profesor entidad =mapper.toEntity(dto);
        Profesor guardado=repository.save(entidad);
        return mapper.toDTO(guardado);
    }

    public void validarUnico(ProfesorDTO dto){
        if(repository.existsByEmail(dto.getEmail())){
            throw new DuplicadoException("Ya existe un profesor registrado con el email "+dto.getEmail()+", por favor revise.");
        }
        if (dto.getDocumento()==TipoDocumento.NIE) {
            throw new IllegalArgumentException("Un profesor no se puede ser registrado con NIE, por favor revise.");
        }
        if(repository.existsByNumeroDocumento(dto.getNumeroDocumento())){
            throw new DuplicadoException("Ya se encuentra un registro con el número de documento: " + dto.getNumeroDocumento());
        }
    }

    public List<ProfesorDTO> obtenerTodos(){
        List<Profesor> lista = repository.findAll();
        return lista.stream().map(mapper::toDTO).toList();
    }

    public ProfesorDTO actualizarProfesor(Long id, ProfesorDTO dto){
        Profesor profesorExiste=repository.findById(id).orElseThrow(()->new EntityNotFoundException("No se encontro el profesor con ID: "+id));

        validarUnicoParaActualizar(id,dto);

        String numeroLimpio = dto.getNumeroDocumento() != null ? dto.getNumeroDocumento().trim() : null;
        if(dto.getDocumento()!=null&&numeroLimpio!=null){
            dto.getDocumento().validar(numeroLimpio);
        }

        actualizarCampos(profesorExiste,dto);

        Profesor guardado=repository.save(profesorExiste);
        return mapper.toDTO(guardado);
    }

    private void validarUnicoParaActualizar(Long idActual, ProfesorDTO dto){
        repository.findByEmail(dto.getEmail()).ifPresent(profesorEncontrado->{
            if(!profesorEncontrado.getIdProfesor().equals(idActual)){
                throw new DuplicadoException("El correo: "+dto.getEmail()+" ya esta siendo utilizado por otro profesor.");
            }
        });

        repository.findByNumeroDocumento(dto.getNumeroDocumento()).ifPresent(profesorEncontrado->{
            if(!profesorEncontrado.getIdProfesor().equals(idActual)){
                throw new DuplicadoException("El número de documento: "+dto.getNumeroDocumento()+" ya pertenece a otro profesor.");
            }
        });
    }

    private void actualizarCampos(Profesor entidad, ProfesorDTO dto){
        entidad.setNombre(dto.getNombre());
        entidad.setEmail(dto.getEmail());
        entidad.setDocumento(dto.getDocumento());
        entidad.setNumeroDocumento(dto.getNumeroDocumento());
        entidad.setTipoContratacion(dto.getTipoContratacion());
        entidad.setPaisResidencia(dto.getPaisResidencia());
        entidad.setSexo(dto.getSexo());
        entidad.setGenero(dto.getGenero());
    }
}
