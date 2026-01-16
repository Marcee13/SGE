package sistemaestudiantil.sge.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import sistemaestudiantil.sge.dto.AsignaturaDTO;
import sistemaestudiantil.sge.model.Asignatura;

@Component //IMPORTANTE, no olvidar las etiquetas
public class AsignaturaMapper {
    public AsignaturaDTO toDTO(Asignatura asignatura){
        AsignaturaDTO dto = new AsignaturaDTO();
        dto.setIdAsignatura(asignatura.getIdAsignatura());
        dto.setName(asignatura.getName());
        dto.setUv(asignatura.getUv());
        dto.setCodigo(asignatura.getCodigo());
        dto.setNivelCiclo(asignatura.getNivelCiclo());
        if (asignatura.getPrerrequisitos() != null && !asignatura.getPrerrequisitos().isEmpty()) {
            List<Long> ids = asignatura.getPrerrequisitos().stream()
                .map(Asignatura::getIdAsignatura)
                .toList();
            
            dto.setIdsPrerrequisitos(ids);
        }
        return dto;
    }

    public Asignatura toEntity(AsignaturaDTO dto){
        Asignatura asignatura= new Asignatura();
        asignatura.setIdAsignatura(dto.getIdAsignatura());
        asignatura.setName(dto.getName());
        asignatura.setUv(dto.getUv());
        asignatura.setCodigo(dto.getCodigo());
        asignatura.setNivelCiclo(dto.getNivelCiclo());
        return asignatura;
    }
}