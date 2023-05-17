package modelo.interfaces;

public interface IObservable {
    void notifyObservadores(String estado, String mensaje);
    void notifyObservadores(String estado, int puerto);
    void agregarObservador(IObserver Observer);
}
