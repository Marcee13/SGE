package sistemaestudiantil.sge.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sistemaestudiantil.sge.response.ApiResponse;
import sistemaestudiantil.sge.service.GrupoService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import sistemaestudiantil.sge.dto.GrupoDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("api/grupos")
@CrossOrigin("*")
public class GrupoController {
    private final GrupoService service;

    public GrupoController(GrupoService service){
        this.service=service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GrupoDTO>>>listar(){
        List<GrupoDTO> lista =service.listaGrupos();
        return new ResponseEntity<>(new ApiResponse<>("Lista de grupos", lista, true),HttpStatus.OK);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<GrupoDTO>> guardar(@RequestBody GrupoDTO dto){
        GrupoDTO nuevoGrupo=service.creaGrupo(dto);
        ApiResponse<GrupoDTO> respuesta = new ApiResponse<>(
            "Grupo creado con éxito",
            nuevoGrupo,
            true
        );
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }
    
    @PatchMapping("/{idGrupo}/cupos")
    public ResponseEntity<ApiResponse<GrupoDTO>> modificarCupos(
            @PathVariable Long idGrupo, 
            @RequestParam Integer nuevosCupos
    ) {
        GrupoDTO grupoActualizado = service.actualizarCupos(idGrupo, nuevosCupos);
        
        return ResponseEntity.ok(new ApiResponse<>(
            "Cupos actualizados correctamente", 
            grupoActualizado, 
            true
        ));
    }

    @PutMapping("/{idGrupo}/asignar-profesor/{idProfesor}")
    public ResponseEntity<ApiResponse<GrupoDTO>> asignarProfesor(
            @PathVariable Long idGrupo, 
            @PathVariable Long idProfesor) {
            
        GrupoDTO grupoActualizado = service.asignarProfesor(idGrupo, idProfesor);
        
        ApiResponse<GrupoDTO> response = new ApiResponse<>(
            "Profesor asignado correctamente al grupo", 
            grupoActualizado, 
            true
        );
        return ResponseEntity.ok(response);
    }
}
