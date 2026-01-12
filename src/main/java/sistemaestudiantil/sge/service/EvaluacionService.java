package sistemaestudiantil.sge.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import sistemaestudiantil.sge.dto.EvaluacionDTO;
import sistemaestudiantil.sge.enums.EstadoInscripcion;
import sistemaestudiantil.sge.exceptions.OperacionNoPermitidaException;
import sistemaestudiantil.sge.exceptions.RecursoNoencontradoException;
import sistemaestudiantil.sge.mapper.EvaluacionMapper;
import sistemaestudiantil.sge.model.Evaluacion;
import sistemaestudiantil.sge.model.Inscripcion;
import sistemaestudiantil.sge.repository.EvaluacionRepository;
import sistemaestudiantil.sge.repository.InscripcionRepository;

@Service
public class EvaluacionService {
    private final EvaluacionRepository evaluacionRepository;
    private final InscripcionRepository inscripcionRepository;
    private final EvaluacionMapper evaluacionMapper;

    public EvaluacionService(EvaluacionRepository evaluacionRepository, EvaluacionMapper evaluacionMapper, InscripcionRepository inscripcionRepository){
        this.evaluacionRepository=evaluacionRepository;
        this.evaluacionMapper=evaluacionMapper;
        this.inscripcionRepository=inscripcionRepository;
    }

    @Transactional
    public EvaluacionDTO agregarNota(EvaluacionDTO dto){
        if(dto.getNotaObtenida()<0||dto.getNotaObtenida()>10){
            throw new IllegalArgumentException("La nota debe estar entre 0.0 y 10.0. Por favor revise.");
        }
        Inscripcion inscripcion=inscripcionRepository.findById(dto.getIdInscripcion()).orElseThrow(()-> new RecursoNoencontradoException("La inscipcion indicada con ID: " +dto.getIdInscripcion()+" no fue encontrada."));

        if (inscripcion.getEstadoInscripcion() != EstadoInscripcion.INSCRITO) {
            throw new OperacionNoPermitidaException(
                "No se puede registrar notas. El estado de la inscripción es: " + inscripcion.getEstadoInscripcion()
            );
        }

        Double porcentajeYaRegistrado = evaluacionRepository.obtenerPorcentajeAcumulado(dto.getIdInscripcion());
        
        double nuevoTotal = porcentajeYaRegistrado + dto.getPorcentaje();

        if (nuevoTotal > 100.0) {
            double restante = 100.0 - porcentajeYaRegistrado;
            throw new IllegalArgumentException(
                "No se puede registrar la nota. El porcentaje excede el 100%. " +
                "Acumulado actual: " + porcentajeYaRegistrado + "%. " +
                "Solo puedes agregar hasta: " + restante + "%."
            );
        }

        Evaluacion evaluacion=new Evaluacion();
        evaluacion.setNombreActividad(dto.getNombreActividad());
        evaluacion.setNotaObtenida(dto.getNotaObtenida());
        evaluacion.setPorcentaje(dto.getPorcentaje());
        evaluacion.setInscripcion(inscripcion);

        Evaluacion guardada = evaluacionRepository.save(evaluacion);

        actualizarPromedioInscripcion(inscripcion);

        dto.setIdEvaluacion(guardada.getIdEvaluacion());

        return dto;
    }

    private void actualizarPromedioInscripcion(Inscripcion inscripcion) {
        List<Evaluacion> notas = evaluacionRepository.findByInscripcion_IdInscripcion(inscripcion.getIdInscripcion());
        
        double promedioAcumulado = 0.0;

        for (Evaluacion e : notas) {
            promedioAcumulado += e.getNotaObtenida() * (e.getPorcentaje() / 100.0);
        }

        inscripcion.setNotaFinal(Math.round(promedioAcumulado * 100.0) / 100.0);

        inscripcionRepository.save(inscripcion);
    }

    public List<EvaluacionDTO> obtenerNotasPorInscripcion(Long idInscripcion) {
        return evaluacionRepository.findByInscripcion_IdInscripcion(idInscripcion)
                .stream()
                .map(evaluacionMapper::toDTO).toList();
    }
}
