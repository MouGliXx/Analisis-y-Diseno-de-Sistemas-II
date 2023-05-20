package modelo.interfaces;

public interface IMensajes {

    void mandarMensajeComoServidor(String mensaje);

    void mandarMensajeComoCliente(String mensaje);
}
