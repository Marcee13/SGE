package sistemaestudiantil.sge.mapper;

import org.springframework.stereotype.Component;

import sistemaestudiantil.sge.dto.CicloDTO;
import sistemaestudiantil.sge.model.Ciclo;

@Component
public class CicloMapper {
    public CicloDTO toDTO(Ciclo ciclo){
        CicloDTO dto=new CicloDTO();
        dto.setActivo(ciclo.getActivo());
        dto.setFechaFin(ciclo.getFechaFin());
        dto.setFechaInicio(ciclo.getFechaInicio());
        dto.setFinInscripcion(ciclo.getFinInscripcion());
        dto.setIdCiclo(ciclo.getIdCiclo());
        dto.setInicioInscripcion(ciclo.getInicioInscripcion());
        dto.setNombre(ciclo.getNombre());
        dto.setNumeroCiclo(ciclo.getNumeroCiclo());
        return dto;
    }

    public Ciclo toEntity(CicloDTO dto){
        Ciclo ciclo=new Ciclo();
        ciclo.setActivo(dto.getActivo());
        ciclo.setFechaFin(dto.getFechaFin());
        ciclo.setFechaInicio(dto.getFechaInicio());
        ciclo.setFinInscripcion(dto.getFinInscripcion());
        ciclo.setIdCiclo(dto.getIdCiclo());
        ciclo.setInicioInscripcion(dto.getInicioInscripcion());
        ciclo.setNombre(dto.getNombre());
        ciclo.setNumeroCiclo(dto.getNumeroCiclo());
        return ciclo;
    }
}
