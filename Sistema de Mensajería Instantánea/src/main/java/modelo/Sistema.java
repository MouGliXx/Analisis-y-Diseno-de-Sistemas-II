package modelo;

import modelo.interfaces.IObserver;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

public class Sistema {
    private Usuario usuario = new Usuario(this.obtenerPuertoAleatorio());
    private Sesion sesion = new Sesion();
    private ArrayList<IObserver> observadores = new ArrayList<>();

    public Sistema() {
        //this.cliente.setListenerServidor();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int obtenerPuertoAleatorio() {
        int puertoInicial = 1024;
        int puertoFinal = 65535;
        Random rand = new Random();
        int puertoAleatorio = rand.nextInt(puertoFinal - puertoInicial + 1) + puertoInicial;
        return puertoAleatorio;
    }

    public Sesion getSesion() {
        return sesion;
    }

    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    public String obtenerIP() throws UnknownHostException {
        InetAddress ip = InetAddress.getLocalHost();
        return ip.getHostAddress();
    }
}
