package sistemaestudiantil.sge.mapper;

import org.springframework.stereotype.Component;

import sistemaestudiantil.sge.dto.GrupoDTO;
import sistemaestudiantil.sge.model.Asignatura;
import sistemaestudiantil.sge.model.Ciclo;
import sistemaestudiantil.sge.model.Grupo;
import sistemaestudiantil.sge.model.Profesor;

@Component
public class GrupoMapper {
    public GrupoDTO toDTO (Grupo grupo){
        GrupoDTO dto=new GrupoDTO();
        dto.setIdGrupo(grupo.getIdGrupo());
        dto.setCodigoGrupo(grupo.getCodigoGrupo());
        dto.setCuposDisponibles(grupo.getCuposDisponibles());
        dto.setHoraFin(grupo.getHoraFin());
        dto.setHoraInicio(grupo.getHoraInicio());
        dto.setDias(grupo.getDias());
        if (grupo.getCiclo() != null) {
            dto.setIdCiclo(grupo.getCiclo().getIdCiclo());
            dto.setNombreCiclo(grupo.getCiclo().getNombre());
        }

        if (grupo.getAsignatura() != null) {
            dto.setIdAsignatura(grupo.getAsignatura().getIdAsignatura());
            dto.setNombreAsignatura(grupo.getAsignatura().getName());
        }

        if (grupo.getProfesor() != null) {
            dto.setIdProfesor(grupo.getProfesor().getIdProfesor());
            dto.setNombreProfesor(grupo.getProfesor().getNombre() + " " + grupo.getProfesor().getApellidos());
        }

        return dto;
    }

    public Grupo toEntity(GrupoDTO dto){
        Grupo grupo =new Grupo();
        grupo.setIdGrupo(dto.getIdGrupo());
        grupo.setCodigoGrupo(dto.getCodigoGrupo());
        grupo.setCuposDisponibles(dto.getCuposDisponibles());
        grupo.setDias(dto.getDias());
        grupo.setHoraFin(dto.getHoraFin());
        grupo.setHoraInicio(dto.getHoraInicio());
        
        if (dto.getIdAsignatura() != null) {
            Asignatura asignatura = new Asignatura();
            asignatura.setIdAsignatura(dto.getIdAsignatura());
            grupo.setAsignatura(asignatura);
        }

        if (dto.getIdProfesor() != null) {
            Profesor profesor = new Profesor();
            profesor.setIdProfesor(dto.getIdProfesor());
            grupo.setProfesor(profesor);
        }

        if (dto.getIdCiclo() != null) {
            Ciclo ciclo = new Ciclo();
            ciclo.setIdCiclo(dto.getIdCiclo());
            grupo.setCiclo(ciclo);
        }

        return grupo;
    }
}
