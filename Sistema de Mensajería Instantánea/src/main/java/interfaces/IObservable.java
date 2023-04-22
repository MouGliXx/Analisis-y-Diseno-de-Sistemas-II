package interfaces;

public interface IObservable {
    void notifyObservadores();
    void agregarObservador(IObserver Observer);
}
