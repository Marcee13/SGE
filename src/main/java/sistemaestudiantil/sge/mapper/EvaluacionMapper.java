package sistemaestudiantil.sge.mapper;

import org.springframework.stereotype.Component;

import sistemaestudiantil.sge.dto.EvaluacionDTO;
import sistemaestudiantil.sge.model.Evaluacion;
import sistemaestudiantil.sge.model.Inscripcion;

@Component
public class EvaluacionMapper {
    public EvaluacionDTO toDTO(Evaluacion evaluacion){
        EvaluacionDTO dto = new EvaluacionDTO();
        dto.setIdEvaluacion(evaluacion.getIdEvaluacion());
        dto.setNombreActividad(evaluacion.getNombreActividad());
        dto.setPorcentaje(evaluacion.getPorcentaje());
        dto.setNotaObtenida(evaluacion.getNotaObtenida());
        if (evaluacion.getInscripcion() != null) {
            dto.setIdInscripcion(evaluacion.getInscripcion().getIdInscripcion());
        }
        return dto;
    }

    public Evaluacion toEntity(EvaluacionDTO dto) {
        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setIdEvaluacion(dto.getIdEvaluacion());
        evaluacion.setNombreActividad(dto.getNombreActividad());
        evaluacion.setPorcentaje(dto.getPorcentaje());
        evaluacion.setNotaObtenida(dto.getNotaObtenida());
        if (dto.getIdInscripcion() != null) {
            Inscripcion inscripcion = new Inscripcion();
            inscripcion.setIdInscripcion(dto.getIdInscripcion());
            evaluacion.setInscripcion(inscripcion);
        }

        return evaluacion;
    }
}
