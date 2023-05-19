package modelo.interfaces;

public interface IObserver {

    void notificarCambio(String estado, String mensaje, String nombreUsuarioEmisor);
    
    void notificarCambio(String estado,int puerto, String nombreUsuarioEmisor);
}
