package modelo.interfaces;

public interface IConexion {
    void crearConexion(int puertoDestino);
    void registrarServidor();
    void listenerMensajes();
}
