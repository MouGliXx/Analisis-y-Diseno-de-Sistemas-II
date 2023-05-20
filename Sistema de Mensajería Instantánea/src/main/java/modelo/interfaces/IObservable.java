package modelo.interfaces;

public interface IObservable {

    void notifyObservadores(String estado, String mensaje,String nombreUsuarioEmisor);

    void notifyObservadores(String estado, int puerto, String nombreUsuarioEmisor);

    void agregarObservador(IObserver Observer);
}
