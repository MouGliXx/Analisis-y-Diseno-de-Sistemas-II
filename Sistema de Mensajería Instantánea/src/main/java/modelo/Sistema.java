package modelo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

public class Sistema {
    private Cliente cliente = new Cliente(this.obtenerPuertoAleatorio());

    public Sistema() throws Exception {
        // Conecto al cliente con el servidor
        this.cliente.registrarServidor();
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente Cliente) {
        this.cliente = Cliente;
    }

    public int obtenerPuertoAleatorio() {
        int puertoInicial = 1024;
        int puertoFinal = 65535;
        Random rand = new Random();
        int puertoAleatorio = rand.nextInt(puertoFinal - puertoInicial + 1) + puertoInicial;
        return puertoAleatorio;
    }

    public String obtenerIP() throws UnknownHostException {
        InetAddress ip = InetAddress.getLocalHost();
        return ip.getHostAddress();
    }
}
