package modelo;

import modelo.interfaces.IObservable;
import modelo.interfaces.IObserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class Cliente implements IObservable{
    private final String hostName = "localhost";


    private String nombreDeUsuario;
    private  int puertoPropio;
    private  int puertoServer = 1234;
    
    

    private String usuario = "";
    private ServerSocket serverSocket;
    //TODO los socket cliente y server podrian estar dentro de una clase mensajes que implementa IMensajes
    private Conexion conexion = new Conexion();
    private boolean isConnected = false;
    private boolean isRejected = false;
    private boolean isServer = false;
    public boolean isStop = false;
    public boolean modoEscucha = false;
    private Thread receiberThread;
    private Thread serverThread;


    private ArrayList<IObserver> observadores = new ArrayList<>();

    public Cliente(int puertoPropio) {
        this.puertoPropio = puertoPropio;
    }

    public void registrarServidor() throws IOException{
//        if (puertoDestino == this.puertoPropio)
//            throw new IOException();
        System.out.printf("Intentando conectarse");
        Socket socket = new Socket(hostName, puertoServer);
        this.conexion.setSocket(socket);
        this.conexion.setOutput(new ObjectOutputStream(socket.getOutputStream()));
        this.conexion.setInput(new ObjectInputStream(socket.getInputStream()));
        Thread listenerMensajes = new Thread(()-> listenerMensajes());
        listenerMensajes.start();
        this.registrar();

    }

    // TODO que lance una excepcion cuando no aceptan conexion
    public void crearConexion(int puertoDestino) throws IOException{
        Mensaje mensaje = new Mensaje(this.puertoPropio,puertoDestino,"CONECTAR","");
        this.conexion.mandarMensaje(mensaje);
        //this.conexion.getOutput()
    }

    public void mandarMensaje(int puertoDestino,String mensajeControl, String text){
        Mensaje mensaje = new Mensaje(this.puertoPropio,puertoDestino,mensajeControl,text);
        this.conexion.mandarMensaje(mensaje);
    }

    private void listenerMensajes() {
        try {
            Mensaje mensaje;
            System.out.printf("entro");
            while ((mensaje = (Mensaje)this.conexion.getInput().readObject()) != null) {
                System.out.printf("\n[" + mensaje.getPuertoOrigen() + "] : " + mensaje.getMensaje());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getNombreDeUsuario() {
        return nombreDeUsuario;
    }

    public void setNombreDeUsuario(String nombreDeUsuario) {
        this.nombreDeUsuario = nombreDeUsuario;
    }

    public void registrar(){
        this.mandarMensaje(puertoServer,"REGISTRAR","");
    }

    public void aceptarConexion(int puertoDestino){
        this.mandarMensaje(puertoDestino,"ACEPTAR","");
    }

    public void mandarTexto(int puertoDestino , String mensaje){
        this.mandarMensaje(puertoDestino,"TEXTO",mensaje);
    }



    public void desconectar() {
        try {
            this.isStop = true;
            this.getServerSocket().close();
            this.getSocketCliente().close();
            this.setRejected(false);
            this.setConnected(false);
            this.setServer(false);
            this.modoEscucha = false;
            this.isStop = true;

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


    public Conexion getSocketCliente() {
        return conexion;
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