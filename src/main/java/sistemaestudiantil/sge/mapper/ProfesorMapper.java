package sistemaestudiantil.sge.mapper;

import org.springframework.stereotype.Component;

import sistemaestudiantil.sge.dto.ProfesorDTO;
import sistemaestudiantil.sge.model.Profesor;

@Component
public class ProfesorMapper {
    public ProfesorDTO toDTO(Profesor profesor){
        ProfesorDTO dto = new ProfesorDTO();
        dto.setIdProfesor(profesor.getIdProfesor());
        dto.setNombre(profesor.getNombre());
        dto.setEmail(profesor.getEmail());
        dto.setDocumento(profesor.getDocumento());
        dto.setNumeroDocumento(profesor.getNumeroDocumento());
        dto.setTipoContratacion(profesor.getTipoContratacion());
        dto.setPaisResidencia(profesor.getPaisResidencia());
        dto.setGenero(profesor.getGenero());
        dto.setSexo(profesor.getSexo());
        return dto;
    }

    public Profesor toEntity(ProfesorDTO dto){
        Profesor profesor = new Profesor();
        profesor.setIdProfesor(dto.getIdProfesor());
        profesor.setNombre(dto.getNombre());
        profesor.setEmail(dto.getEmail());
        profesor.setDocumento(dto.getDocumento());
        profesor.setNumeroDocumento(dto.getNumeroDocumento());
        profesor.setTipoContratacion(dto.getTipoContratacion());
        profesor.setGenero(dto.getGenero());
        profesor.setSexo(dto.getSexo());
        profesor.setPaisResidencia(dto.getPaisResidencia());
        return profesor;
    }
}
