package modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;

public class ListenerThread implements Runnable {
    public BufferedReader input;
    private final String mensaje = "";
    private String usuario = "";
    private  Usuario cliente;
    private  SocketIO socket;


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
                    cliente.desconectar();
                    break;
                }else if (mensaje.equals("Abro ventana sesion")) {
                    cliente.notifyObservadores("Abro ventana sesion", "");
                }
                else if (mensaje.equals("Se cierra conexion y ventana")) { //TODO nunca entra aca
                    cliente.notifyObservadores("Cierro ventana sesion", "");
                    cliente.desconectar();
                    break;
                } else if (mensaje.equals("Acepto conexion")) {
                    cliente.notifyObservadores("Acepto conexion", "");
                } else if (mensaje.equals("Rechazo conexion")) {
                    cliente.notifyObservadores("Ventana Emergente", "");
                } else {
                    cliente.notifyObservadores("Recibo mensaje",mensaje);
                }
            } catch (SocketException e) {
                try {
                    input.close();
                    socket.close();
                } catch (IOException ex) {
                    //TODO manejar el cierre del socket
                    e.getMessage();
                }
                break;
            }catch (IOException e){
                e.getMessage();

            }
        }
    }
}