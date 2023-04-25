package modelo.interfaces;

public interface IObservable {
    void notifyObservadores(String estado, String mensaje);

    void agregarObservador(IObserver Observer);
}
