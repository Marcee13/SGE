package sistemaestudiantil.sge.mapper;

import org.springframework.stereotype.Component;

import sistemaestudiantil.sge.dto.FacultadDTO;
import sistemaestudiantil.sge.model.Facultad;

@Component
public class FacultadMapper {
    public FacultadDTO toDTO(Facultad facultad){
        FacultadDTO dto =new FacultadDTO();
        dto.setIdFacultad(facultad.getIdFacultad());
        dto.setNombre(facultad.getNombre());
        dto.setDescripcion(facultad.getDescripcion());
        return dto;
    }

    public Facultad toEntity(FacultadDTO dto){
        Facultad facultad=new Facultad();
        facultad.setIdFacultad(dto.getIdFacultad());
        facultad.setNombre(dto.getNombre());
        facultad.setDescripcion(dto.getDescripcion());
        return facultad;
    }
}
