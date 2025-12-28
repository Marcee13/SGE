package sistemaestudiantil.sge.enums;

//Segun entiendo se declaran los valores que no cambian como por ejemplo los estados. Esta clase puede tener logica dentro de ella.
public enum EstadoEstudiante {
    ASPIRANTE(true),
    ASPIRANTE_FASE_2(true),
    CONDICIONADO(true),
    SELECCIONADO(true),
    ESTUDIANTE(true),
    RETIRADO(false),
    REPROBADO(false),
    EGRESADO(false),
    GRADUADO(false);

    private final boolean activo;

    EstadoEstudiante(boolean activo){
        this.activo=activo;
    }

    public boolean esActivo(){
        return this.activo;
    }
}
