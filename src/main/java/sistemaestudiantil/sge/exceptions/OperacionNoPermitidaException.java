package sistemaestudiantil.sge.exceptions;

public class OperacionNoPermitidaException extends RuntimeException{
    public OperacionNoPermitidaException(String mensaje){
        super(mensaje);
    }
}
