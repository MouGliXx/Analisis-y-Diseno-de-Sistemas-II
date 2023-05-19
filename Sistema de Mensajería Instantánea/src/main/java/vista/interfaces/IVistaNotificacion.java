package vista.interfaces;

import java.awt.event.ActionListener;
import java.awt.event.WindowListener;

public interface IVistaNotificacion {

    void setActionListener(ActionListener controlador);

    void setWindowListener(WindowListener controlador);

    void ejecutar();

    void cerrarDialogo();

    void cerrarVentana();

    int getTipo();

    void setTipoNotificacion(int tipo, String nombreUsuarioEmisor);
}
