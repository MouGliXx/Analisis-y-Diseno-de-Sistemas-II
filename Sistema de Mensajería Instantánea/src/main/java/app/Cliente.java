package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Cliente{

    private  String hostName = "localhost";
    private int puertoPropio;
    private String usuario = "";
    private ServerSocket serverSocket;
    private SocketIO socketCliente;
    private SocketIO socketServer;
    private boolean modoEscucha;

    public Cliente(int puertoPropio) {
        this.puertoPropio = puertoPropio;
        this.socketCliente = new SocketIO();
        this.socketServer = new SocketIO();
    }

    public void crearConexionCliente(int puerto) {
        try {
            System.out.println("Se creo conexion como cliente");
            Socket socket = new Socket(hostName, puerto);
            this.socketCliente.setSocket(socket);
            this.socketCliente.setOutput(new PrintWriter(socket.getOutputStream(), true));
            this.socketCliente.setInput(new BufferedReader(new InputStreamReader(socket.getInputStream())));
            this.setListenerMensajesComoCliente();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setListenerServidor() {
        try {
            this.serverSocket = new ServerSocket(puertoPropio);
            Thread serverThread = new Thread(new ServerThread(serverSocket, this));
            serverThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setListenerMensajesComoCliente() {
        if (this.socketCliente.getInput() != null) {
            Thread receiverThread = new Thread(new ListenerThread(this.socketCliente.getInput(), "Usuario 1"));
            receiverThread.start();
        } else {
            System.out.println("No se ha establecido una conexión previa.");
        }
    }

    public void desconectar() {
        try {
            this.socketServer.getSocket().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public boolean isModoEscucha() {
        return modoEscucha;
    }

    public void setModoEscucha(boolean modoEscucha) {
        this.modoEscucha = modoEscucha;
    }

    public void mandarMensajeComoCliente(String mensaje){this.getSocketCliente().mandarMensaje(mensaje);}

    public void mandarMensajeComoServidor(String mensaje){this.getSocketServer().mandarMensaje(mensaje);}

    public SocketIO getSocketCliente() {
        return socketCliente;
    }

    public SocketIO getSocketServer() {
        return socketServer;
    }

    public static void main(String[] args) throws InterruptedException {
        Cliente cliente1 = new Cliente(2888);
        Cliente cliente2 = new Cliente(2887);
        cliente1.setListenerServidor();
        cliente2.setListenerServidor();
        Thread.sleep(7000);
        cliente2.crearConexionCliente(2888); // CLIENTE2(CLIENTE) ---> CLIENTE (SERVIDOR)
        Thread.sleep(7000);
        //cliente1.crearConexionServer();
        Thread.sleep(15000);
        cliente2.getSocketCliente().mandarMensaje("Holaaaa");
        Thread.sleep(1000);
        cliente2.getSocketCliente().mandarMensaje("Todo bien???");
        Thread.sleep(1000);
        cliente1.getSocketServer().mandarMensaje("Todo bien???");
    }

}