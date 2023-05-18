package vista.interfaces;

public interface IVistaMensajes extends IVista{

    void setKeyListener();

    void setModelos();

    void setUsuarios(String UsuarioEmisor, String UsuarioReceptor);

    void agregarNuevoRecibido(String mensaje);

    void agregarNuevoEnviado(String mensaje);

    String getMensajeEnviado();

    void creaVentanaInicio();

    void cerrarVentana();
}
