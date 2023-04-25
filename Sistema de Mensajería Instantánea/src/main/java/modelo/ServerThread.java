package modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;

public class ServerThread implements Runnable {
    private ServerSocket server;
    private Usuario cliente;

    ServerThread(ServerSocket serverSocket, Usuario cliente) {
        this.server = serverSocket;
        this.cliente = cliente;
    }

    public void run() {
        try {
            System.out.println("Servidor escuchando en el puerto " + server.getLocalPort());
            cliente.getSocketServer().setSocket(server.accept());
            cliente.notifyObservadores("Abro ventana notificacion", "");

            while (true) {// TODO verificar este while true
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
                    cliente.isStop = true; // para detener el Listener de Mensajes
                    cliente.mandarMensajeComoServidor("Se cierra conexion");
                    cliente.desconectar();
                    break;
                }
            }
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
            Thread receiverThread = new Thread(new ListenerThread(socketServer.getInput(), cliente.getUsuario(), cliente));
            receiverThread.start();
        } else {
            System.out.println("\nNo se ha establecido una conexión previa.");
        }
    }
}