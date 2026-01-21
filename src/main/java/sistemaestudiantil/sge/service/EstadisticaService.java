package sistemaestudiantil.sge.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import sistemaestudiantil.sge.dto.DashboardDTO;
import sistemaestudiantil.sge.dto.ItemEstadisticoDTO;
import sistemaestudiantil.sge.repository.EstudianteRepository;
import sistemaestudiantil.sge.repository.InscripcionRepository;
import sistemaestudiantil.sge.repository.ProfesorRepository;

@Service
public class EstadisticaService {
    private final EstudianteRepository estudianteRepository;
    private final InscripcionRepository inscripcionRepository;
    private final ProfesorRepository profesorRepository;

    public EstadisticaService(EstudianteRepository estudianteRepository, ProfesorRepository profesorRepository, InscripcionRepository inscripcionRepository) {
        this.estudianteRepository = estudianteRepository;
        this.profesorRepository=profesorRepository;
        this.inscripcionRepository = inscripcionRepository;
    }

    public DashboardDTO obtenerMetricasDashboard() {
        DashboardDTO dashboard = new DashboardDTO();

        dashboard.setTotalEstudiantes(estudianteRepository.count());
        dashboard.setTotalDocentes(profesorRepository.count()); 
        dashboard.setMateriasInscritasTotal(inscripcionRepository.contarInscripcionesActivas());

        List<ItemEstadisticoDTO> porCarrera = estudianteRepository.contarEstudiantesPorCarrera();
        dashboard.setEstudiantesPorCarrera(porCarrera);

        List<ItemEstadisticoDTO> topReprobadas = inscripcionRepository.encontrarTopMateriasReprobadas(PageRequest.of(0, 5));
        dashboard.setTopMateriasReprobadas(topReprobadas);

        return dashboard;
    }
}
