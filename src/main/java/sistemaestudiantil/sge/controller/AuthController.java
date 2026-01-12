package sistemaestudiantil.sge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sistemaestudiantil.sge.dto.CambioContraseniaDTO;
import sistemaestudiantil.sge.dto.LoginDTO;
import sistemaestudiantil.sge.response.ApiResponse;
import sistemaestudiantil.sge.response.AuthResponse;
import sistemaestudiantil.sge.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginDTO dto) {
        AuthResponse authResponse=authService.login(dto);
        ApiResponse<AuthResponse> respuesta = new ApiResponse<>(
            "Login exitoso", 
            authResponse, 
            true
        );
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/cambiar-password")
    public ResponseEntity<ApiResponse<String>> cambiarPassword(@RequestBody CambioContraseniaDTO dto) {
        authService.cambiarContrasenia(dto);
        ApiResponse<String> respuesta = new ApiResponse<>(
            "Cambio de contraseña exitoso. Por favor inicie sesión nuevamente.", 
            null,
            true
        );
        return ResponseEntity.ok(respuesta);
    }
}
