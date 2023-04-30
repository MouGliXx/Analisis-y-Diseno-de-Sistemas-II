package modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.SocketException;

public class ServerThread implements Runnable {
    private ServerSocket server;
    private Usuario cliente;

    ServerThread(ServerSocket serverSocket, Usuario cliente) {
        this.server = serverSocket;
        this.cliente = cliente;
    }

    public void run(){
        try {
            System.out.println("\nServidor escuchando en el puerto " + server.getLocalPort());
            cliente.getSocketServer().setSocket(server.accept());
            System.out.printf("\nEl modo escucha es" + cliente.isModoEscucha());
            // Un cliente se intento conectar conmigo si esta en modo Escucha acepto.
            if (cliente.isModoEscucha())
                cliente.notifyObservadores("Abro ventana notificacion", "");
            else {
                System.out.printf("\nno se envio la solicitud");
                cliente.getSocketServer().getSocket().close();
            }
            while (cliente.isModoEscucha()) {// TODO verificar este while true
                cliente.isStop = false;
                setAsServer();
                setMessageListener();
                if (cliente.isConnected()) {
                    cliente.setServer(true);
                    System.out.printf("\nse seteo el server: " + cliente.isServer());
                    cliente.mandarMensajeComoServidor("Abro ventana sesion");
                    break;
                }
                if (cliente.isRejected()) {
                    System.out.println("\nSe rechazo la conexion");
                    cliente.modoEscucha = true; // para detener el Listener de Mensajes
                    cliente.mandarMensajeComoServidor("Se cierra conexion");
                    cliente.desconectar();
                    break;
                }
            }
        } catch (SocketException e) {
            // Socket was closed, stop accepting new clients
            System.out.println("\nServer socket closed: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setAsServer() {
        try {
            SocketIO socketServer = cliente.getSocketServer();
            socketServer.setOutput(new PrintWriter(socketServer.getSocket().getOutputStream(), true));
            socketServer.setInput(new BufferedReader(new InputStreamReader(socketServer.getSocket().getInputStream())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMessageListener() {
        SocketIO socketServer = cliente.getSocketServer();
        if (socketServer.getInput() != null) {
            cliente.setReceiberThread(new Thread(new ListenerThread(socketServer.getInput(), cliente.getUsuario(), cliente,socketServer)));
            cliente.getReceiberThread().start();
        } else {
            System.out.println("\nNo se ha establecido una conexión previa.");
        }
    }
}