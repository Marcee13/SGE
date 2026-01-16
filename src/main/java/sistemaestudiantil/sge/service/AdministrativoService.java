package sistemaestudiantil.sge.service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import sistemaestudiantil.sge.dto.CicloDTO;
import sistemaestudiantil.sge.exceptions.DuplicadoException;
import sistemaestudiantil.sge.exceptions.RecursoNoencontradoException;
import sistemaestudiantil.sge.model.Asignatura;
import sistemaestudiantil.sge.model.Ciclo;
import sistemaestudiantil.sge.model.Grupo;
import sistemaestudiantil.sge.repository.AsignaturaRepository;
import sistemaestudiantil.sge.repository.CicloRepository;
import sistemaestudiantil.sge.repository.GrupoRespository;

@Service
public class AdministrativoService {
    private final CicloRepository cicloRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final GrupoRespository grupoRepository;

    public AdministrativoService(CicloRepository cicloRepository, GrupoRespository grupoRepository, AsignaturaRepository asignaturaRepository) {
        this.cicloRepository = cicloRepository;
        this.grupoRepository=grupoRepository;
        this.asignaturaRepository=asignaturaRepository;
    }

    @Transactional
    public Ciclo crearCiclo(CicloDTO dto) {
        if (cicloRepository.existsByNombre(dto.getNombre())) {
            throw new DuplicadoException("El ciclo " + dto.getNombre() + " ya existe.");
        }

        Ciclo nuevoCiclo = new Ciclo();
        nuevoCiclo.setNombre(dto.getNombre());
        nuevoCiclo.setFechaInicio(dto.getFechaInicio());
        nuevoCiclo.setFechaFin(dto.getFechaFin());
        nuevoCiclo.setInicioInscripcion(dto.getInicioInscripcion());
        nuevoCiclo.setFinInscripcion(dto.getFinInscripcion());
        nuevoCiclo.setNumeroCiclo(dto.getNumeroCiclo());

        if (Boolean.TRUE.equals(dto.getActivo())) {
            desactivarCicloActual();
            nuevoCiclo.setActivo(true);
        } else {
            nuevoCiclo.setActivo(false);
        }

        return cicloRepository.save(nuevoCiclo);
    }

    @Transactional
    public Ciclo activarCiclo(Long idCiclo) {
        Ciclo ciclo = cicloRepository.findById(idCiclo).orElseThrow(() -> new RuntimeException("Ciclo no encontrado"));

        desactivarCicloActual();
        
        ciclo.setActivo(true);
        return cicloRepository.save(ciclo);
    }

    private void desactivarCicloActual() {
        Optional<Ciclo> cicloActual = cicloRepository.findByActivoTrue();
        if (cicloActual.isPresent()) {
            Ciclo actual = cicloActual.get();
            actual.setActivo(false);
            cicloRepository.save(actual);
        }
    }
    
    public List<Ciclo> listarTodos() {
        return cicloRepository.findAll();
    }

    @Transactional
    public String generarOfertaAcademica(Long idCiclo) {
        Ciclo ciclo = cicloRepository.findById(idCiclo).orElseThrow(() -> new RecursoNoencontradoException("Ciclo no encontrado"));

        List<Asignatura> asignaturasHabilitadas;

        if (ciclo.getNumeroCiclo() == 1) {
            asignaturasHabilitadas = asignaturaRepository.buscarMateriasCicloImpar();
        } else {
            asignaturasHabilitadas = asignaturaRepository.buscarMateriasCicloPar();
        }

        int gruposCreados = 0;

        for (Asignatura materia : asignaturasHabilitadas) {

            String codigoGrupoBase = "01"; 

            boolean existe = grupoRepository.existsByCodigoGrupoAndAsignaturaAndCiclo(
                    codigoGrupoBase, materia, ciclo
            );

            if (!existe) {
                Grupo nuevoGrupo = new Grupo();
                nuevoGrupo.setCiclo(ciclo);
                nuevoGrupo.setAsignatura(materia);
                nuevoGrupo.setCodigoGrupo(codigoGrupoBase);
                
                // Valores por defecto (Para que el Admin los edite luego)
                nuevoGrupo.setCuposDisponibles(40); 
                nuevoGrupo.setDias("POR DEFINIR");
                nuevoGrupo.setHoraInicio(LocalTime.of(7, 0));
                nuevoGrupo.setHoraFin(LocalTime.of(8, 0));

                nuevoGrupo.setProfesor(null); 

                grupoRepository.save(nuevoGrupo);
                gruposCreados++;
            }
        }

        return "Se generaron " + gruposCreados + " grupos para el ciclo " + ciclo.getNombre() + " (" + (ciclo.getNumeroCiclo() == 1 ? "IMPAR" : "PAR") + ")";
    }
}