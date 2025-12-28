package sistemaestudiantil.sge.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import sistemaestudiantil.sge.response.ApiResponse;
import sistemaestudiantil.sge.service.GrupoService;
import org.springframework.web.bind.annotation.GetMapping;
import sistemaestudiantil.sge.dto.GrupoDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
@RequestMapping("api/grupos")
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
    public ResponseEntity<ApiResponse<GrupoDTO>> guardar(@RequestBody GrupoDTO dto){
        GrupoDTO nuevoGrupo=service.creaGrupo(dto);
        ApiResponse<GrupoDTO> respuesta = new ApiResponse<>(
            "Grupo creado con éxito",
            nuevoGrupo,
            true
        );
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }
    
}
