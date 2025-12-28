package sistemaestudiantil.sge.mapper;

import org.springframework.stereotype.Component;

import sistemaestudiantil.sge.dto.EstudianteDTO;
import sistemaestudiantil.sge.model.Carrera;
import sistemaestudiantil.sge.model.Estudiante;

@Component
public class EstudianteMapper {
    public EstudianteDTO toDTO(Estudiante estudiante){
        EstudianteDTO dto = new EstudianteDTO();
        dto.setIdEstudiante(estudiante.getIdEstudiante());
        dto.setCarnet(estudiante.getCarnet());
        dto.setNombres(estudiante.getNombres());
        dto.setApellidos(estudiante.getApellidos());
        dto.setEmail(estudiante.getEmail());
        dto.setFechaNacimiento(estudiante.getFechaNacimiento());
        dto.setNumeroTelefonico(estudiante.getNumeroTelefonico());
        dto.setDocumento(estudiante.getDocumento());
        dto.setNumeroDocumento(estudiante.getNumeroDocumento());
        dto.setGenero(estudiante.getGenero());
        dto.setSexo(estudiante.getSexo());
        dto.setEstado(estudiante.getEstado());
        dto.setNotaExamenGeneral(estudiante.getNotaExamenGeneral());
        dto.setNotaExamenEspecifico(estudiante.getNotaExamenEspecifico());
        dto.setPaisResidencia(estudiante.getPaisResidencia());

        if (estudiante.getCarrera() != null) {
        dto.setIdCarrera(estudiante.getCarrera().getIdCarrera());
        dto.setNombreCarrera(estudiante.getCarrera().getNombreCarrera());
        }
        return dto;
    }

    public Estudiante toEntity(EstudianteDTO dto){
        Estudiante estudiante = new Estudiante();
        estudiante.setIdEstudiante(dto.getIdEstudiante());
        estudiante.setCarnet(dto.getCarnet());
        estudiante.setNombres(dto.getNombres());
        estudiante.setApellidos(dto.getApellidos());
        estudiante.setEmail(dto.getEmail());
        estudiante.setFechaNacimiento(dto.getFechaNacimiento());
        estudiante.setNumeroTelefonico(dto.getNumeroTelefonico());
        estudiante.setDocumento(dto.getDocumento());
        estudiante.setNumeroDocumento(dto.getNumeroDocumento());
        estudiante.setGenero(dto.getGenero());
        estudiante.setSexo(dto.getSexo());
        estudiante.setEstado(dto.getEstado());
        estudiante.setNotaExamenGeneral(dto.getNotaExamenGeneral());
        estudiante.setNotaExamenEspecifico(dto.getNotaExamenEspecifico());
        estudiante.setPaisResidencia(dto.getPaisResidencia());

        if (dto.getIdCarrera() != null) {
        Carrera carrera = new Carrera();
        carrera.setIdCarrera(dto.getIdCarrera());
        estudiante.setCarrera(carrera); 
        }
        return estudiante;
    }
}