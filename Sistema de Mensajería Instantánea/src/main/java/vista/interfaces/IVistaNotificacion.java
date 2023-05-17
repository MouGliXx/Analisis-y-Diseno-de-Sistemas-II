package vista.interfaces;

import modelo.Sistema;

public interface IVistaNotificacion extends IVista {
    void creaOtraVentana(Sistema sistema, int tipo, String nombreUsuarioEmisor);
    int getTipo();

    void setTipoVentana(int tipo, String nombreUsuarioEmisor);
}
