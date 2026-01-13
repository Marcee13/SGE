package sistemaestudiantil.sge.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.ToString;
import sistemaestudiantil.sge.dto.CambioContraseniaDTO;
import sistemaestudiantil.sge.dto.LoginDTO;
import sistemaestudiantil.sge.enums.EstadoEstudiante;
import sistemaestudiantil.sge.enums.Roles;
import sistemaestudiantil.sge.exceptions.CredencialesInvalidasException;
import sistemaestudiantil.sge.exceptions.OperacionNoPermitidaException;
import sistemaestudiantil.sge.exceptions.RecursoNoencontradoException;
import sistemaestudiantil.sge.mapper.EstudianteMapper;
import sistemaestudiantil.sge.model.Estudiante;
import sistemaestudiantil.sge.repository.EstudianteRepository;
import sistemaestudiantil.sge.response.AuthResponse;
import sistemaestudiantil.sge.security.JwtUtil;

@Service
public class AuthService {
    private final EstudianteRepository estudianteRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final EstudianteMapper estudianteMapper;

    public AuthService(EstudianteRepository estudianteRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, EstudianteMapper estudianteMapper) {
        this.estudianteRepository = estudianteRepository;
        this.jwtUtil = jwtUtil;
        this.estudianteMapper = estudianteMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse login(LoginDTO loginDTO) {

        if (loginDTO == null || loginDTO.getIdentificador() == null || loginDTO.getContrasenia() == null) {
            throw new CredencialesInvalidasException("Debe enviar usuario y contraseña.");
        }

        String input = loginDTO.getIdentificador();
        String rawPass = loginDTO.getContrasenia();
        
        Optional<Estudiante> usuarioOpt;
        boolean esCorreo = input.contains("@");

        if (esCorreo) {
            usuarioOpt = estudianteRepository.findByEmail(input);
        } else {
            usuarioOpt = estudianteRepository.findByCarnet(input);
        }

        if (usuarioOpt.isEmpty()) {
            throw new CredencialesInvalidasException("Credenciales incorrectas.");
        }

        Estudiante estudiante = usuarioOpt.get();

        if (esCorreo && estudiante.getEstado() == EstadoEstudiante.ESTUDIANTE&&!estudiante.getRol().equals(Roles.ROLE_ADMIN)) {
            throw new OperacionNoPermitidaException("Usted ya es estudiante oficial. Por favor ingrese con su Carnet.");
        }

        if (!passwordEncoder.matches(rawPass, estudiante.getContrasenia())) {
            throw new CredencialesInvalidasException("Credenciales incorrectas.");
        }

        String rolUsuario = estudiante.getRol().toString();

        String username = estudiante.getCarnet() != null ? estudiante.getCarnet() : estudiante.getEmail();
        
        String token = jwtUtil.generateToken(username, rolUsuario);

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setEstudiante(estudianteMapper.toDTO(estudiante));

        return response;
    }

    public void cambiarContrasenia(CambioContraseniaDTO dto) {
        Estudiante estudiante = estudianteRepository.findById(dto.getIdEstudiante())
                .orElseThrow(() -> new RecursoNoencontradoException("Estudiante no encontrado"));

        if (!passwordEncoder.matches(dto.getContraseniaActual(), estudiante.getContrasenia())) {
            throw new CredencialesInvalidasException("La contraseña actual es incorrecta.");
        }

        if (dto.getNuevaContrasenia().length() < 6) {
             throw new OperacionNoPermitidaException("La nueva contraseña debe tener al menos 6 caracteres.");
        }

        estudiante.setContrasenia(passwordEncoder.encode(dto.getNuevaContrasenia()));
        estudiante.setDebeCambiarClave(false);
        
        estudianteRepository.save(estudiante);
    }
}
