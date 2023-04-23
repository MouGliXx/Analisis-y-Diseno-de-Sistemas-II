package vista.interfaces;

import java.awt.event.KeyListener;

public interface IVistaInicio extends IVista {
    void setKeyListener();

    void setChangeListener();

    void setMiDireccionIP(String texto);

    String getNombreDeUsuario();

    String getDireccionIP();

    boolean getModoEscucha();

    int getPuerto();

    void creaOtraVentana(int ventana, String nombreUsuarioEmisor);
}
