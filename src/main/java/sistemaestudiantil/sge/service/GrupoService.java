package sistemaestudiantil.sge.service;

import java.util.List;

import org.springframework.stereotype.Service;

import sistemaestudiantil.sge.dto.GrupoDTO;
import sistemaestudiantil.sge.exceptions.DuplicadoException;
import sistemaestudiantil.sge.exceptions.RecursoNoencontradoException;
import sistemaestudiantil.sge.model.Asignatura;
import sistemaestudiantil.sge.model.Grupo;
import sistemaestudiantil.sge.model.Profesor;
import sistemaestudiantil.sge.repository.AsignaturaRepository;
import sistemaestudiantil.sge.repository.GrupoRespository;
import sistemaestudiantil.sge.repository.ProfesorRepository;

@Service
public class GrupoService {
    private final AsignaturaRepository asignaturaRepository;
    private final ProfesorRepository profesorRepository;
    private final GrupoRespository grupoRespository;

    public GrupoService(AsignaturaRepository asignaturaRepository, GrupoRespository grupoRespository, ProfesorRepository profesorRepository){
        this.asignaturaRepository=asignaturaRepository;
        this.profesorRepository=profesorRepository;
        this.grupoRespository=grupoRespository;
    }

    public GrupoDTO creaGrupo(GrupoDTO dto){
        Asignatura asignatura=asignaturaRepository.findById(dto.getIdAsignatura()).orElseThrow(()->new RecursoNoencontradoException("Asignatura no encontrada con el ID: " + dto.getIdAsignatura()));
        Profesor profesor =profesorRepository.findById(dto.getIdProfesor()).orElseThrow(()->new RecursoNoencontradoException("Profesor no encontrado con el ID: " +dto.getIdProfesor()));

        boolean existe = grupoRespository.existsByCodigoGrupoAndAsignaturaAndCiclo(
                dto.getCodigoGrupo(), 
                asignatura, 
                dto.getCiclo()
        );

        if (existe) {
            throw new DuplicadoException(
                "Ya existe el grupo " + dto.getCodigoGrupo() + 
                " para la materia " + asignatura.getName() + 
                " en el ciclo " + dto.getCiclo()
            );
        }

        Grupo grupo =new Grupo();
        grupo.setCodigoGrupo(dto.getCodigoGrupo());
        grupo.setCiclo(dto.getCiclo());
        grupo.setAsignatura(asignatura);
        grupo.setProfesor(profesor);

        Grupo guardado= grupoRespository.save(grupo);

        return convertirADTO(guardado);
    }

    public List<GrupoDTO> listaGrupos(){
        return grupoRespository.findAll().stream().map(this::convertirADTO).toList();
    }

    public GrupoDTO convertirADTO(Grupo g){
        GrupoDTO grupo = new GrupoDTO();
        grupo.setIdGrupo(g.getIdGrupo());
        grupo.setCodigoGrupo(g.getCodigoGrupo());
        grupo.setCiclo(g.getCiclo());
        grupo.setIdAsignatura(g.getAsignatura().getIdAsignatura());
        grupo.setNombreAsignatura(g.getAsignatura().getName());
        grupo.setIdProfesor(g.getProfesor().getIdProfesor());
        grupo.setNombreProfesor(g.getProfesor().getNombre());

        return grupo;
    }
}
