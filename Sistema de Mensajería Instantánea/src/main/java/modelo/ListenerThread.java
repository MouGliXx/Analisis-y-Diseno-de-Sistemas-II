package modelo;

import java.io.BufferedReader;
import java.io.IOException;


public class ListenerThread implements Runnable {
    public BufferedReader input;
    private String mensaje = "";
    private String usuario = "";
    private Usuario cliente;

    public ListenerThread(BufferedReader input, String usuario, Usuario cliente) {
        this.input = input;
        this.usuario = usuario;
        this.cliente = cliente;
    }

    @Override
    public void run() {
        cliente.isStop = false;
        while (!cliente.isStop) { //TODO el isStop es lo que va a permitir que el modoEscucha funcione o no
            try {
                String mensaje = input.readLine();
                if (mensaje == null) {
                    break;
                } else if (mensaje.equals("Se cierra conexion")) {
                    System.out.println("Cerrando conexiones...");
                    cliente.desconectar();
                    break;
                } else if (mensaje.equals("Se cierra conexion y ventana")) { //TODO nunca entra aca
                    System.out.println("Cerrando conexiones y ventana...");
                    cliente.notifyObservadores("Cierro ventana sesion", "");
                    cliente.desconectar();
                    break;
                } else if (mensaje.equals("Abro ventana sesion")) {
                    System.out.println("Abriendo ventana...");
                    cliente.notifyObservadores("Abro ventana sesion", "");
                } else {
                    System.out.printf("[%s]: %s%n", usuario, mensaje);
                    cliente.notifyObservadores("Recibo mensaje",mensaje);
                }
                System.out.printf("\nValor de cliente %s", cliente.isStop);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}