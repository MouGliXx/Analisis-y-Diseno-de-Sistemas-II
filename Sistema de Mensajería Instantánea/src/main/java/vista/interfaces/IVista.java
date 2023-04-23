package vista.interfaces;

import java.awt.event.ActionListener;
import java.awt.event.WindowListener;

public interface IVista {
    void setActionListener(ActionListener controlador);

    void setWindowListener(WindowListener controlador);

    void ejecutar();

    void cerrarVentana();

    void creaOtraVentana(int ventana, String nombreUsuarioEmisor);

    void lanzarVentanaEmergente(String mensaje);
}
