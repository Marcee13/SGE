package sistemaestudiantil.sge.response;

import lombok.Data;
import sistemaestudiantil.sge.dto.EstudianteDTO;
import sistemaestudiantil.sge.dto.ProfesorDTO;

@Data
public class AuthResponse {
    private String token;
    private EstudianteDTO estudiante;
    private ProfesorDTO profesor;
}
