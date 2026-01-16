package sistemaestudiantil.sge.exceptions;

import java.util.Arrays;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import sistemaestudiantil.sge.response.ApiResponse;
import tools.jackson.databind.exc.InvalidFormatException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DuplicadoException.class)
    public ResponseEntity<ApiResponse<Object>> manejarDuplicado(DuplicadoException ex) {
        ApiResponse<Object> respuesta = new ApiResponse<>(
            ex.getMessage(),
            null,            
            false 
        );
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> manejarErrorBaseDatos(DataIntegrityViolationException ex) {
        String mensajeUsuario = "Error de integridad de datos. Verifique que los valores (como Estado, ID o Referencias) sean válidos y existan.";

        ApiResponse<Object> respuesta = new ApiResponse<>(
            mensajeUsuario,
            null,
            false
        );
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> manejarJsonMalFormado(HttpMessageNotReadableException ex) {
        
        String mensajeUsuario = "Error en el formato del JSON enviado. Verifique fechas (dd-MM-YYYY) y tipos de datos.";

        if (ex.getCause() instanceof InvalidFormatException ifx 
                && ifx.getTargetType() != null 
                && ifx.getTargetType().isEnum()) {
                
            String valorInvalido = ifx.getValue().toString();
            String valoresPermitidos = Arrays.toString(ifx.getTargetType().getEnumConstants());
            
            mensajeUsuario = String.format("El valor '%s' no es válido. Valores permitidos: %s", valorInvalido, valoresPermitidos);
        }

        ApiResponse<Object> respuesta = new ApiResponse<>(
            mensajeUsuario,
            null,
            false
        );

        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> manejarErrorDeParametros(MethodArgumentTypeMismatchException ex) {
        
        String mensajeUsuario = "Parámetro inválido en la solicitud.";
        Class<?> requiredType = ex.getRequiredType();

        if (requiredType != null && requiredType.isEnum()) {
            Object valorRecibido=ex.getValue();
            String valorInvalido = (valorRecibido != null) ? valorRecibido.toString() : "null";
            String valoresPermitidos = Arrays.toString(requiredType.getEnumConstants());
            
            mensajeUsuario = String.format("El valor '%s' no es válido para el parámetro '%s'. Valores permitidos: %s", valorInvalido, ex.getName(), valoresPermitidos);
        }

        ApiResponse<Object> respuesta = new ApiResponse<>(
            mensajeUsuario,
            null,
            false
        );

        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> manejarArgumentoInvalido(IllegalArgumentException ex){
        String mensaje = "";
        
        if (ex.getMessage() != null && ex.getMessage().contains("No enum constant")) {
            mensaje = "El valor enviado no coincide con ninguna opción válida (Enum).";
            
            String[] partes = ex.getMessage().split("\\.");
            if (partes.length > 0) {
                 mensaje += " Verifique el valor para: " + partes[partes.length - 1]; 
            }
        } else {
            mensaje = ex.getMessage(); 
        }

        ApiResponse<Object> respuesta = new ApiResponse<>(
            mensaje,
            null,
            false
        );

        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RecursoNoencontradoException.class)
    public ResponseEntity<ApiResponse<Object>> manejarNoEncontrado(RecursoNoencontradoException ex){
        ApiResponse<Object> respuesta=new ApiResponse<>(
            ex.getMessage(),
            null,
            false
        );
        return new ResponseEntity<>(respuesta,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Object>> manejarEstadoIlegal(IllegalStateException ex){
        ApiResponse<Object> respuesta=new ApiResponse<>(
            ex.getMessage(),
            null,
            false
        );
        return new ResponseEntity<>(respuesta,HttpStatus.CONFLICT);
    }

    @ExceptionHandler(OperacionNoPermitidaException.class)
    public ResponseEntity<ApiResponse<Void>> manejarOperacionNoPermitida(OperacionNoPermitidaException ex) {
        ApiResponse<Void> respuesta = new ApiResponse<>(
            ex.getMessage(), 
            null, 
            false
        );
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ApiResponse<Object>> manejarCredenciales(CredencialesInvalidasException ex) {
        ApiResponse<Object> respuesta=new ApiResponse<>(
            ex.getMessage(),
            null,
            false
        );
        return new ResponseEntity<>(respuesta, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> manejarAccesoDenegado(AccessDeniedException ex) {
        ApiResponse<Object> respuesta = new ApiResponse<>(
            "No tiene permisos para acceder a este recurso. Requiere un rol superior.",
            null,
            false
        );
        return new ResponseEntity<>(respuesta, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> manejarErrorAutenticacion(AuthenticationException ex) {
        ApiResponse<Object> respuesta = new ApiResponse<>(
            "No autorizado. Por favor inicie sesión nuevamente.",
            null,
            false
        );
        return new ResponseEntity<>(respuesta, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> manejarErrorGeneral(Exception ex) {
        ApiResponse<Object> respuesta = new ApiResponse<>(
            "Ocurrió un error interno en el servidor: " + ex.getMessage(),
            null,
            false
        );
        return new ResponseEntity<>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNotFound(NoResourceFoundException ex) {
        
        ApiResponse<String> response = new ApiResponse<>(
            "El endpoint solicitado no existe. Por favor verifique la URL.",
            "Ruta no encontrada: /" + ex.getResourcePath(),
            false
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
