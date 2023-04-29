package vista.interfaces;

import java.awt.event.ActionListener;

public interface IVista {
    void setActionListener(ActionListener controlador);

    void ejecutar();

    void cerrarVentana();

    void lanzarVentanaEmergente(String mensaje);


}
