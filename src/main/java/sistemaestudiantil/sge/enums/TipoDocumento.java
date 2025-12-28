package sistemaestudiantil.sge.enums;

public enum TipoDocumento {
    DUI ("^\\d{8}-\\d{1}$", "Formato de DUI inválido. Debe usar el formato 00000000-0."),
    NIE("^\\d{5,15}$", "Formato de NIE inválido. Debe ser solo números."),
    PASAPORTE("^[A-Z]\\d{6}$", "Formato de Pasaporte inválido. Solo letras mayúsculas y números con formato X000000");

    private final String regex;
    private final String mensajeError;

    TipoDocumento(String regex, String mensajeError){
        this.mensajeError=mensajeError;
        this.regex=regex;
    }

    public void validar(String numero){
        if(numero==null||!numero.matches(this.regex)){
            throw new IllegalArgumentException(this.mensajeError);
        }
    }
}
