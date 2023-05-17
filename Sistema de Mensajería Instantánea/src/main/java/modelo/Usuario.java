package modelo;

import modelo.interfaces.IMensajes;
import modelo.interfaces.IObservable;
import modelo.interfaces.IObserver;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class Usuario implements IObservable, IMensajes {
    private final String hostName = "localhost";
    private String nombreDeUsuario;
    private  int puertoPropio;
    private  int puertoDestino;

    private String usuario = "";
    private ServerSocket serverSocket;
    //TODO los socket cliente y server podrian estar dentro de una clase mensajes que implementa IMensajes
    private final SocketIO socketCliente;
    private final SocketIO socketServer;
    private boolean isConnected = false;
    private boolean isRejected = false;
    private boolean isServer = false;
    public boolean isStop = false;
    public boolean modoEscucha = false;
    private Thread receiberThread;
    private Thread serverThread;


    private ArrayList<IObserver> observadores = new ArrayList<>();

    public Usuario(int puertoPropio) {
        this.puertoPropio = puertoPropio;
        this.socketCliente = new SocketIO();
        this.socketServer = new SocketIO();

    }

    public String getNombreDeUsuario() {
        return nombreDeUsuario;
    }

    public void setNombreDeUsuario(String nombreDeUsuario) {
        this.nombreDeUsuario = nombreDeUsuario;
    }

    public Thread getReceiberThread() {
        return receiberThread;
    }

    public void setReceiberThread(Thread receiberThread) {
        this.receiberThread = receiberThread;
    }

    public void crearConexionCliente(int puerto) throws IOException {
        if (puerto == this.puertoPropio)
            throw new IOException();
        Socket socket = new Socket(hostName, puerto);
        this.socketCliente.setSocket(socket);
        this.socketCliente.setOutput(new PrintWriter(socket.getOutputStream(), true));
        this.socketCliente.setInput(new BufferedReader(new InputStreamReader(socket.getInputStream())));
        this.setListenerMensajesComoCliente();
    }

    public void setListenerServidor() {
        try {
            this.serverSocket = new ServerSocket(puertoPropio);
            serverThread = new Thread(new ServerThread(serverSocket, this));
            serverThread.start();
            Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
               this.notifyObservadores("Ventana Emergente","");
            });
        } catch (IOException e) {
            this.desconectar();
        }
    }

    public void setListenerMensajesComoCliente() {
        if (this.socketCliente.getInput() != null) {
            this.receiberThread = new Thread(new ListenerThread(this.socketCliente.getInput(), "Usuario 1", this,this.socketServer));
            Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
               e.getMessage();
            });
            receiberThread.start();
        }
    }

    public void desconectar() {
        try {
            this.isStop = true;
            this.getServerSocket().close();
            this.getSocketCliente().close();
            this.getSocketServer().close();
            this.setRejected(false);
            this.setConnected(false);
            this.setServer(false);
            this.modoEscucha = false;
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

    public int getPuertoPropio() {
        return puertoPropio;
    }

    public void setPuertoPropio(int puertoPropio) {
        this.puertoPropio = puertoPropio;
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
        isConnected = connected;}

    public boolean isRejected() {return isRejected;}

    public void setRejected(boolean rejected) {isRejected = rejected;}

    public boolean isServer() { return isServer; }

    public boolean isModoEscucha() {
        return modoEscucha;
    }

    public void setModoEscucha(boolean stop) {
        modoEscucha = stop;
    }

    public void setServer(boolean cliente) { isServer = cliente; }

    public static void main(String[] args) throws InterruptedException {
        Usuario cliente1 = new Usuario(2888);
        Usuario cliente2 = new Usuario(2887);
        cliente1.setListenerServidor();
        cliente2.setListenerServidor();
        Thread.sleep(7000);
        try {
            cliente2.crearConexionCliente(2888); // CLIENTE2(CLIENTE) ---> CLIENTE (SERVIDOR)
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public void notifyObservadores(String estado, String mensaje) {
        Iterator<IObserver> iter = observadores.iterator();
        while (iter.hasNext()) {
            IObserver obs = iter.next();
            obs.notificarCambio(estado, mensaje);
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