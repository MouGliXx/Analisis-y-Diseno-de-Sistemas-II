package modelo.interfaces;

import java.io.IOException;

public interface IConexion {
    void crearConexion(int puertoDestino) throws IOException;
    void registrar(String nombreDeUsuario) throws IOException;
    void aceptarConexion(int puertoDestino) throws IOException;
    void rechazarConexion(int puertoDestino) throws IOException;
    void mandarTexto(String mensaje) throws IOException;
    void cerrarConexion(String mensaje);
}
