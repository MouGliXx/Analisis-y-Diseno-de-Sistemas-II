package vista.interfaces;

public interface IVistaNotificacion extends IVista {
    void creaOtraVentana(int tipo, String nombreUsuarioEmisor);

    int getTipo();

    void setTipoVentana(int tipo, String nombreUsuarioEmisor);
}
