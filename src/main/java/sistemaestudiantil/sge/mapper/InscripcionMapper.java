package sistemaestudiantil.sge.mapper;

import org.springframework.stereotype.Component;

import sistemaestudiantil.sge.dto.InscripcionDTO;
import sistemaestudiantil.sge.model.Inscripcion;

@Component
public class InscripcionMapper {
    public InscripcionDTO toDTO(Inscripcion inscripcion) {
        InscripcionDTO dto = new InscripcionDTO();
        dto.setIdInscripcion(inscripcion.getIdInscripcion());
        dto.setIdEstudiante(inscripcion.getEstudiante().getIdEstudiante());
        dto.setNombreEstudiante(inscripcion.getEstudiante().getNombres() + " " + inscripcion.getEstudiante().getApellidos());
        dto.setIdGrupo(inscripcion.getGrupo().getIdGrupo());
        dto.setCodigoGrupo(inscripcion.getGrupo().getCodigoGrupo());
        dto.setNombreMateria(inscripcion.getGrupo().getAsignatura().getName());
        dto.setFechaInscripcion(inscripcion.getFechaInscripcion());
        dto.setEstado(inscripcion.getEstadoInscripcion());
        dto.setNotaFinal(inscripcion.getNotaFinal());
        return dto;
    }
}
