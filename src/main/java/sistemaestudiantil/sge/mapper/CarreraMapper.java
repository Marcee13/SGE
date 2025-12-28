package sistemaestudiantil.sge.mapper;

import org.springframework.stereotype.Component;

import sistemaestudiantil.sge.dto.CarreraDTO;
import sistemaestudiantil.sge.model.Carrera;

@Component
public class CarreraMapper {
    public CarreraDTO toDTO(Carrera carrera){
        CarreraDTO dto=new CarreraDTO();
        dto.setIdCarrera(carrera.getIdCarrera());
        dto.setNombreCarrera(carrera.getNombreCarrera());
        dto.setEsCarreraEducacion(carrera.getEsCarreraEducacion());
        dto.setCodigoCarrera(carrera.getCodigoCarrera());
        dto.setDuracionCarrera(carrera.getDuracionCarrera());
        dto.setNumeroAsignaturas(carrera.getNumeroAsignaturas());
        dto.setTituloQueOtorga(carrera.getTituloQueOtorga());
        if (carrera.getFacultad() != null) {
            dto.setIdFacultad(carrera.getFacultad().getIdFacultad());
            dto.setNombreFacultad(carrera.getFacultad().getNombre());
        }
        return dto;
    }

    public Carrera toEntity(CarreraDTO dto){
        Carrera carrera=new Carrera();
        carrera.setIdCarrera(dto.getIdCarrera());
        carrera.setNombreCarrera(dto.getNombreCarrera());
        carrera.setEsCarreraEducacion(dto.getEsCarreraEducacion());
        carrera.setCodigoCarrera(dto.getCodigoCarrera());
        carrera.setDuracionCarrera(dto.getDuracionCarrera());
        carrera.setNumeroAsignaturas(dto.getNumeroAsignaturas());
        carrera.setTituloQueOtorga(dto.getTituloQueOtorga());
        return carrera;
    }
}
