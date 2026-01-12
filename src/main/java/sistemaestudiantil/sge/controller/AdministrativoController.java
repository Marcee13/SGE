package sistemaestudiantil.sge.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sistemaestudiantil.sge.dto.EstudianteDTO;
import sistemaestudiantil.sge.enums.CicloAcademico;
import sistemaestudiantil.sge.response.ApiResponse;
import sistemaestudiantil.sge.service.EstudianteService;
import sistemaestudiantil.sge.service.InscripcionService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class AdministrativoController {
    private final InscripcionService inscripcionService;
    private final EstudianteService estudianteService;

    public AdministrativoController(InscripcionService inscripcionService, EstudianteService estudianteService){
        this.inscripcionService=inscripcionService;
        this.estudianteService=estudianteService;
    }

    @PostMapping("/cierre-ciclo")
    public ResponseEntity<ApiResponse<String>> ejecutarCierre(@RequestParam CicloAcademico ciclo){
        String resultado=inscripcionService.cerrarCiclo(ciclo);
        ApiResponse<String> respuesta=new ApiResponse<>(
            "Proceso finalizado.", 
            resultado, 
            true);
        return new ResponseEntity<>(respuesta,HttpStatus.OK);
    }

    @PostMapping("/cierre-admision")
    public ResponseEntity<ApiResponse<List<EstudianteDTO>>> cerrarAdmision(@RequestParam Integer anioIngreso) {
        
        List<EstudianteDTO> nuevosEstudiantes = estudianteService.cerrarCicloAdmision(anioIngreso);
        
        ApiResponse<List<EstudianteDTO>> respuesta = new ApiResponse<>(
            "Cierre de admisión ejecutado correctamente. Se han generado carnets y usuarios.",
            nuevosEstudiantes,
            true
        );
        
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }
}
