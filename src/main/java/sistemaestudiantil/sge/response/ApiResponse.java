package sistemaestudiantil.sge.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor //IMPORTANTE, no olvidar las etiquetas
public class ApiResponse<T> {
    private String mensaje;
    private T datos;
    private boolean exito;
}
