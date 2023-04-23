package vista.interfaces;

public interface IVistaNotificacion extends IVista {
    void creaOtraVentana(int ventana, String nombreUsuarioEmisor);

    void setTipoVentana(int tipo, String nombreUsuarioEmisor);
}
