package sistemaestudiantil.sge.exceptions;

public class CredencialesInvalidasException extends RuntimeException{
    public CredencialesInvalidasException(String mensaje){
        super(mensaje);
    }
}
