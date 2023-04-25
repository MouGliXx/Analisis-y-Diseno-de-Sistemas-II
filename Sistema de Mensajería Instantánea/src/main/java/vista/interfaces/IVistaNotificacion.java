package vista.interfaces;

public interface IVistaNotificacion extends IVista {
    void creaOtraVentana(int tipo);

    void setTipoVentana(int tipo, String nombreUsuarioEmisor);
}
