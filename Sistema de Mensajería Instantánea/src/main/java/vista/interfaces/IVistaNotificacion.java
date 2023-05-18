package vista.interfaces;

import java.awt.event.WindowListener;

public interface IVistaNotificacion extends IVista {

    void setWindowListener(WindowListener controlador);
    
    void creaOtraVentana(int tipo, String nombreUsuarioEmisor);

    int getTipo();

    void setTipoVentana(int tipo, String nombreUsuarioEmisor);
}
