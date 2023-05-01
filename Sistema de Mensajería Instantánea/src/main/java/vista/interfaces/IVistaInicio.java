package vista.interfaces;

import modelo.Sistema;

public interface IVistaInicio extends IVista {
    void setKeyListener();

    void setChangeListener();

    void setMiDireccionIP(String IP);

    void setMiPuerto(String puerto);

    String getNombreDeUsuario();

    String getDireccionIP();

    void setModoEscucha(Boolean activado);

    boolean getModoEscucha();

    int getPuerto();

   void creaOtraVentana(Sistema sistema, int tipo, String nombreUsuarioEmisor);
}
