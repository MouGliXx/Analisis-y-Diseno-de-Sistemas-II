package modelo.interfaces;

public interface IObserver {
    void notificarCambio(String estado,String mensaje);
    void notificarCambio(String estado,int puerto);
}
