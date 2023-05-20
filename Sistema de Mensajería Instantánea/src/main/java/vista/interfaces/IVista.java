package vista.interfaces;

import java.awt.event.ActionListener;

public interface IVista {

    void setActionListener(ActionListener controlador);

    void ejecutar();

    void lanzarVentanaEmergente(String mensaje);

    void cerrarVentana();
}
