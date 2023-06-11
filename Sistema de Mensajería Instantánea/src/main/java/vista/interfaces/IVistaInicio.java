package vista.interfaces;

public interface IVistaInicio extends IVista {

    void setKeyListener();

    void setChangeListener();

    void setModelos();

    void setMiDireccionIP(String IP);

    String getMiDireccionIP();

    void setMiPuerto(String puerto);

    void setModoConectar();

    String getNombreDeUsuario();

    String getDireccionIP();

    void setModoEscucha(Boolean activado);

    boolean getModoEscucha();

    int getPuerto();

    void actualizarTablaUsuarios(String mensaje);

    IVistaNotificacion lanzarNotificacion();

    void mostrarVentana();

    void ocultarVentana();

    void creaVentanaMensajes(String nombreUsuarioEmisor);
}
