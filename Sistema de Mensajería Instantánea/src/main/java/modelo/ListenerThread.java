package modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;

public class ListenerThread implements Runnable {
    public BufferedReader input;
    private String mensaje = "";
    private String usuario = "";
    private Usuario cliente;
    private SocketIO socket;


    public ListenerThread(BufferedReader input, String usuario, Usuario cliente, SocketIO socket) {
        this.input = input;
        this.usuario = usuario;
        this.cliente = cliente;
        this.socket = socket;
    }



    @Override
    public void run() {
        cliente.isStop = false;
        while (!cliente.isStop ) { //TODO el isStop es lo que va a permitir que el modoEscucha funcione o no
            try {
                String mensaje = input.readLine();
                if (mensaje == null) {
                    break;
                } else if (mensaje.equals("Se cierra conexion")) {
                    System.out.println("\nCerrando conexiones...");
                    cliente.desconectar();
                    break;
                }else if (mensaje.equals("Abro ventana sesion")) {
                    System.out.println("\nAbriendo ventana...");
                    cliente.notifyObservadores("Abro ventana sesion", "");
                }
                else if (mensaje.equals("Se cierra conexion y ventana")) { //TODO nunca entra aca
                    System.out.println("\nCerrando conexiones y ventana...");
                    cliente.notifyObservadores("Cierro ventana sesion", "");
                    cliente.desconectar();
                    break;
                } else if (mensaje.equals("Acepto conexion")) {
                    System.out.println("\nSe acepto la conexion...");
                    cliente.notifyObservadores("Acepto conexion", "");
                } else if (mensaje.equals("Rechazo conexion")) {
                    System.out.println("\nRECHAZO CONEXION...");
                    cliente.notifyObservadores("Ventana Emergente", "");
                } else {
                    System.out.printf("\n[%s]: %s%n", usuario, mensaje);
                    cliente.notifyObservadores("Recibo mensaje",mensaje);
                }
                System.out.printf("\nValor de cliente %s", cliente.isModoEscucha());
            } catch (SocketException e) {
                System.out.println("\nEl socket se cerró: " + e.getMessage());
                try {
                    input.close();
                    socket.close();
                } catch (IOException ex) {
                    //TODO manejar el cierre del socket
                    e.printStackTrace();
                }
                break;
            }catch (IOException e){
                e.printStackTrace();

            }
        }
    }
}