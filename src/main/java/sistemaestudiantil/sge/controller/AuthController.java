package sistemaestudiantil.sge.controller;

import java.util.Map;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sistemaestudiantil.sge.dto.EstudianteDTO;
import sistemaestudiantil.sge.dto.LoginDTO;
import sistemaestudiantil.sge.mapper.EstudianteMapper;
import sistemaestudiantil.sge.model.Estudiante;
import sistemaestudiantil.sge.response.ApiResponse;
import sistemaestudiantil.sge.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final EstudianteMapper estudianteMapper;

    public AuthController(AuthService authService, EstudianteMapper estudianteMapper) {
        this.authService = authService;
        this.estudianteMapper=estudianteMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<EstudianteDTO>> login(@RequestBody LoginDTO dto) {
        Estudiante estudianteEntity = authService.login(dto);
        EstudianteDTO respuestaDTO = estudianteMapper.toDTO(estudianteEntity);
        ApiResponse<EstudianteDTO> respuesta = new ApiResponse<>(
            "Login exitoso", 
            respuestaDTO, 
            true
        );
        return ResponseEntity.ok(respuesta);
    }
}
