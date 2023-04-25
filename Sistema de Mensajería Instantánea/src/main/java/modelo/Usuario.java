package modelo;

import modelo.interfaces.IObservable;
import modelo.interfaces.IObserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observer;

public class Usuario implements IObservable {
    private String hostName = "localhost";
    private int puertoPropio;
    private String usuario = "";
    private ServerSocket serverSocket;
    private SocketIO socketCliente;
    private SocketIO socketServer;
    private boolean modoEscucha;
    private boolean isConnected = false;
    private boolean isRejected = false;
    private boolean isServer = false;
    public boolean isStop = false;
    private Thread receiberThread;
    private Thread serverThread;

    private ArrayList<IObserver> observadores = new ArrayList<>();

    public Usuario(int puertoPropio) {
        this.puertoPropio = puertoPropio;
        this.socketCliente = new SocketIO();
        this.socketServer = new SocketIO();
    }

    public void crearConexionCliente(int puerto) {
        try {
            System.out.println("\nSe creó conexión como cliente con el puerto" + puerto);
            Socket socket = new Socket(hostName, puerto);
            this.socketCliente.setSocket(socket);
            this.socketCliente.setOutput(new PrintWriter(socket.getOutputStream(), true));
            this.socketCliente.setInput(new BufferedReader(new InputStreamReader(socket.getInputStream())));
            this.setListenerMensajesComoCliente();
        } catch (UnknownHostException e) {
            System.out.printf("PARECIERA QUE SE RECHAZÓ LA CONEXIÓN");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.printf("PARECIERA QUE SE RECHAZÓ LA CONEXIÓN");
            e.printStackTrace();
        }
    }

    public void setListenerServidor() {
        try {
            this.serverSocket = new ServerSocket(puertoPropio);
            serverThread = new Thread(new ServerThread(serverSocket, this));
            serverThread.start();
        } catch (IOException e) {
            this.desconectar();
        }
    }

    public void setListenerMensajesComoCliente() {
        if (this.socketCliente.getInput() != null) {
            this.receiberThread = new Thread(new ListenerThread(this.socketCliente.getInput(), "Usuario 1", this));
            Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
                System.out.println("Caught " + e);
            });
            receiberThread.start();
        }
    }

    public void desconectar() {
        try {
            this.getServerSocket().close();
            this.getSocketCliente().close();
            this.getServerSocket().close();
            System.out.printf("se desconectó todo");
            this.setRejected(false);
            this.setConnected(false);
            this.setServer(false);
            this.isStop = true;
            setListenerServidor();
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

    public void setModoEscucha(boolean modoEscucha) {
        this.modoEscucha = modoEscucha;
    }

    public void mandarMensajeComoCliente(String mensaje) {
        this.getSocketCliente().mandarMensaje(mensaje);
    }

    public void mandarMensajeComoServidor(String mensaje){this.getSocketServer().mandarMensaje(mensaje);}

    public SocketIO getSocketCliente() {
        return socketCliente;
    }

    public SocketIO getSocketServer() {
        return socketServer;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public boolean isConnected() {return isConnected;}

    public void setConnected(boolean connected) {
        System.out.printf("se cambio el estadodo e is connected");
        isConnected = connected;}

    public boolean isRejected() {return isRejected;}

    public void setRejected(boolean rejected) {isRejected = rejected;}

    public boolean isServer() { return isServer; }

    public void setServer(boolean cliente) { isServer = cliente; }

    public static void main(String[] args) throws InterruptedException {
        Usuario cliente1 = new Usuario(2888);
        Usuario cliente2 = new Usuario(2887);
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

    @Override
    public void notifyObservadores(String estado,String mensaje) {
        Iterator<IObserver> iter = observadores.iterator();
        while (iter.hasNext()) {
            IObserver obs = iter.next();
            obs.notificarCambio(estado,mensaje);
        }
    }

    @Override
    public void agregarObservador(IObserver observer) {
        this.observadores.add(observer);
    }

    public ArrayList<IObserver> getObservadores() {
        return observadores;
    }

    public void setObservadores(ArrayList<IObserver> observadores) {
        this.observadores = observadores;
    }
}