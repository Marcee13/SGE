package sistemaestudiantil.sge.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sistemaestudiantil.sge.dto.FacultadDTO;
import sistemaestudiantil.sge.response.ApiResponse;
import sistemaestudiantil.sge.service.FacultadService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/facultades")
public class FacultadController {
    private final FacultadService service;

    public FacultadController(FacultadService service){
        this.service=service;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')or hasAuthority('ROLE_ADMINISTRATIVO')")
    public ResponseEntity<ApiResponse<List<FacultadDTO>>> listar() {
        return ResponseEntity.ok(new ApiResponse<>(
            "Lista de facultades", 
            service.listarFacultades(), 
            true));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')or hasAuthority('ROLE_ADMINISTRATIVO')")
    public ResponseEntity<ApiResponse<FacultadDTO>> crear(@RequestBody FacultadDTO dto) {
        return ResponseEntity.ok(new ApiResponse<>(
            "Facultad creada", 
            service.crearFacultad(dto), 
            true));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')or hasAuthority('ROLE_ADMINISTRATIVO')")
    public ResponseEntity<ApiResponse<FacultadDTO>> actualizar(@PathVariable Long id, @RequestBody FacultadDTO dto) {
        FacultadDTO actualizar = service.actualizarFacultad(id, dto);
        ApiResponse<FacultadDTO> respuesta= new ApiResponse<>(
            "Facultad actualizada correctamente",
            actualizar,
            true
        );

        return new ResponseEntity<>(respuesta,HttpStatus.OK);
    }
}
