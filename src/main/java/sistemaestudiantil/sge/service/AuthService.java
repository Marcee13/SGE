package sistemaestudiantil.sge.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import sistemaestudiantil.sge.dto.LoginDTO;
import sistemaestudiantil.sge.enums.EstadoEstudiante;
import sistemaestudiantil.sge.exceptions.CredencialesInvalidasException;
import sistemaestudiantil.sge.exceptions.OperacionNoPermitidaException;
import sistemaestudiantil.sge.model.Estudiante;
import sistemaestudiantil.sge.repository.EstudianteRepository;

@Service
public class AuthService {
    private final EstudianteRepository estudianteRepository;

    public AuthService(EstudianteRepository estudianteRepository) {
        this.estudianteRepository = estudianteRepository;
    }

    public Estudiante login(LoginDTO loginDTO) {
        String input = loginDTO.getIdentificador();
        String pass = loginDTO.getContrasenia();
        
        Optional<Estudiante> usuarioOpt;

        if (input.contains("@")) {
            usuarioOpt = estudianteRepository.findByEmail(input);
        } else {
            usuarioOpt = estudianteRepository.findByCarnet(input);
        }

        if (usuarioOpt.isEmpty()) {
            throw new CredencialesInvalidasException("Usuario no encontrado o credenciales incorrectas");
        }

        Estudiante estudiante = usuarioOpt.get();

        if (input.contains("@") && estudiante.getEstado() == EstadoEstudiante.ESTUDIANTE) {
             throw new OperacionNoPermitidaException("Usted ya es estudiante inscrito. Por favor ingrese con su Carnet.");
        }

        if (!estudiante.getContrasenia().equals(pass)) {
            throw new CredencialesInvalidasException("Contraseña incorrecta");
        }

        return estudiante;
    }
}
