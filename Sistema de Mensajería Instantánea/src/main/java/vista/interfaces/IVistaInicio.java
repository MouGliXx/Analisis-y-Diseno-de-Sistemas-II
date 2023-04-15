package vista.interfaces;

public interface IVistaInicio extends IVista {
    void setKeyListener();

    String getNombreDeUsuario();

    String getDireccionIP();

    void setMiDireccionIP(String texto);

    String getPuerto();
}
