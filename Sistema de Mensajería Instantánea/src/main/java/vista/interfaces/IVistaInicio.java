package vista.interfaces;

public interface IVistaInicio extends IVista {

    void setKeyListener();

    void setChangeListener();

    void setMiDireccionIP(String IP);

    void setMiPuerto(String puerto);

    void setModoConectar();

    String getNombreDeUsuario();

    String getDireccionIP();

    void setModoEscucha(Boolean activado);

    boolean getModoEscucha();

    int getPuerto();

    void creaOtraVentana(int tipo, String nombreUsuarioEmisor);
}
