package vista.interfaces;

import modelo.Sistema;

public interface IVistaNotificacion extends IVista {
    public void creaOtraVentana(Sistema sistema, int tipo);

    void setTipoVentana(int tipo, String nombreUsuarioEmisor);
}
