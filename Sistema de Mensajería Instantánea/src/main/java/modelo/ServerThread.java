package modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.SocketException;

public class ServerThread implements Runnable {
    private  ServerSocket server;
    private  Usuario cliente;

    ServerThread(ServerSocket serverSocket, Usuario cliente) {
        this.server = serverSocket;
        this.cliente = cliente;
    }

    public void run(){
        try {
            System.out.println("\nServidor escuchando en el puerto " + server.getLocalPort());
            cliente.getSocketServer().setSocket(server.accept());
            // Un cliente se intento conectar conmigo si esta en modo Escucha acepto.
            if (cliente.isModoEscucha()){
                cliente.notifyObservadores("Abro ventana notificacion", "");
            }
            cliente.isStop = false;
            setAsServer();
            setMessageListener();
            if (!cliente.isModoEscucha()){
                cliente.mandarMensajeComoServidor("Rechazo conexion");
                cliente.getSocketServer().getSocket().close();
                cliente.desconectar();
            } else{
                cliente.mandarMensajeComoServidor("Acepto conexion");
            }
            while (cliente.isModoEscucha()) {
                if (cliente.isConnected()) {
                    cliente.setServer(true);
                    cliente.mandarMensajeComoServidor("Abro ventana sesion");
                }
                if (cliente.isRejected()) {
                    cliente.modoEscucha = true; // para detener el Listener de Mensajes
                    cliente.mandarMensajeComoServidor("Se cierra conexion");
                    cliente.desconectar();
                }
            }
        } catch (SocketException e) {
            // Socket was closed, stop accepting new clients
            e.getMessage();
        } catch (IOException e) {
            e.getMessage();
        }
    }

    private void setAsServer() {
        try {
            SocketIO socketServer = cliente.getSocketServer();
            socketServer.setOutput(new PrintWriter(socketServer.getSocket().getOutputStream(), true));
            socketServer.setInput(new BufferedReader(new InputStreamReader(socketServer.getSocket().getInputStream())));
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void setMessageListener() {
        SocketIO socketServer = cliente.getSocketServer();
        if (socketServer.getInput() != null) {
            cliente.setReceiberThread(new Thread(new ListenerThread(socketServer.getInput(), cliente.getUsuario(), cliente,socketServer)));
            cliente.getReceiberThread().start();
        }
    }
}