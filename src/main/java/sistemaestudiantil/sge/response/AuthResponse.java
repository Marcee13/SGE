package sistemaestudiantil.sge.response;

import lombok.Data;
import sistemaestudiantil.sge.dto.EstudianteDTO;

@Data
public class AuthResponse {
    private String token;
    private EstudianteDTO estudiante;
}
