package modelo.interfaces;

public interface IConexion {
    void crearConexion(int puertoDestino);
    void registrar(String nombreDeUsuario);
    void aceptarConexion(int puertoDestino);
    void rechazarConexion(int puertoDestino);
    void mandarTexto(String mensaje);
    void cerrarConexion(String mensaje);
}
