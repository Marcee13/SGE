package sistemaestudiantil.sge.mapper;

import org.springframework.stereotype.Component;

import sistemaestudiantil.sge.dto.ArancelDTO;
import sistemaestudiantil.sge.model.Arancel;

@Component
public class ArancelMapper {
    public ArancelDTO toDTO(Arancel arancel){
        ArancelDTO dto=new ArancelDTO();
        dto.setCodigo(arancel.getCodigo());
        dto.setCosto(arancel.getCosto());
        dto.setIdArancel(arancel.getIdArancel());
        dto.setNombre(arancel.getNombre());
        return dto;
    }

    public Arancel toEntity(ArancelDTO dto){
        Arancel arancel=new Arancel();
        arancel.setCodigo(dto.getCodigo());
        arancel.setCosto(dto.getCosto());
        arancel.setIdArancel(dto.getIdArancel());
        arancel.setNombre(dto.getNombre());
        return arancel;
    }
}
