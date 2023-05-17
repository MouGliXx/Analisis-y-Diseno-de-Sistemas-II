package modelo;

import modelo.interfaces.IObservable;
import modelo.interfaces.IObserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64;
import java.util.Iterator;

import static modelo.Cifrado.desencriptar;
import static modelo.Cifrado.encriptar;

import static modelo.Cifrado.desencriptar;
import static modelo.Cifrado.encriptar;

public class Cliente implements IObservable{
    private final String hostName = "localhost";
    private String nombreDeUsuario;
    private  int puertoPropio;
    private  int puertoServer = 1234;
    private String usuario = "";
    private ArrayList<IObserver> observadores = new ArrayList<>();

    //TODO los socket cliente y server podrian estar dentro de una clase mensajes que implementa IMensajes
    private Conexion conexion = new Conexion();
    private boolean isConnected = false;
    private boolean isRejected = false;
    private boolean isServer = false;
    public boolean isStop = false;
    public boolean modoEscucha = false;
    private Thread receiberThread;
    private Thread serverThread;


    public Cliente(int puertoPropio) {
        this.puertoPropio = puertoPropio;
    }

    public void registrarServidor() throws Exception {
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


    private void listenerMensajes() {
        try {
            Mensaje mensaje;
            while ((mensaje = (Mensaje) this.conexion.getInput().readObject()) != null) {
                procesarMensaje(mensaje);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void procesarMensaje(Mensaje mensaje) throws Exception {
        String mensajeControl = mensaje.getMensajeControl();
        System.out.printf("\n[" + mensaje.getPuertoOrigen() + "] : " + mensaje.getMensaje());
        byte[] textoEncriptado =  Base64.getDecoder().decode(mensaje.getMensaje());
        String textoOriginal = desencriptar("12345678",textoEncriptado, "DES");
        System.out.println(textoOriginal);
        switch (mensajeControl) {
            case "Abro ventana sesion":
                notifyObservadores("Abro ventana sesion", "");
                break;
            case "NUEVA_CONEXION":
                System.out.println("Entre a nueva conexion");
                notifyObservadores("Abro ventana notificacion", mensaje.getPuertoOrigen());
                break;
            case "Cerrar sesion":
                notifyObservadores("Cierro ventana sesion", "");
                break;
            case "Acepto conexion":
                notifyObservadores("Acepto conexion", "");
                break;
            case "Rechazo conexion":
                notifyObservadores("Ventana Emergente", "");
                break;
            default:
                notifyObservadores("Recibo mensaje", mensaje.getMensaje());
                break;
        }
    }

    // TIPOS DE MENSAJES


    public void mandarMensaje(int puertoDestino,String mensajeControl, String text) throws Exception {
        Mensaje mensaje = new Mensaje(this.puertoPropio,puertoDestino,mensajeControl,text);
        byte[] textoEncriptado = encriptar("12345678", mensaje.getMensaje(), "DES");
        String textoEncriptadoBase64 = Base64.getEncoder().encodeToString(textoEncriptado);
        this.conexion.mandarMensaje(mensaje);
    }

    public void registrar() throws Exception {this.mandarMensaje(puertoServer, "REGISTRAR", "");}
    public void aceptarConexion(int puertoDestino) throws Exception {this.mandarMensaje(puertoDestino,"ACEPTAR","");}

    public void mandarTexto(String mensaje) {
        try {
            mandarMensaje(-1, "TEXTO", mensaje);
        } catch (Exception e) { //TODO getionar excepcion
            e.printStackTrace();
        }
    }

    public void cerrarConexion(String mensaje) {
        try {
            mandarMensaje(-1, "DESCONECTAR", mensaje);
        } catch (Exception e) { //TODO getionar excepcion
            e.printStackTrace();
        }
    }

    // METODOS PARA EL OBSERVER
    @Override
    public void notifyObservadores(String estado, String mensaje) {
        Iterator<IObserver> iter = observadores.iterator();
        while (iter.hasNext()) {
            IObserver obs = iter.next();
            obs.notificarCambio(estado, mensaje);
        }
    }

    @Override
    public void notifyObservadores(String estado, int puerto) {
        Iterator<IObserver> iter = observadores.iterator();
        while (iter.hasNext()) {
            IObserver obs = iter.next();
            obs.notificarCambio(estado, puerto);
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

    // GETTERS AND SETTERS.

    public int getPuertoPropio() {
        return puertoPropio;
    }



    public boolean isConnected() {return isConnected;}

    public void setConnected(boolean connected) {
        isConnected = connected;}


    public void setModoEscucha(boolean stop) {
        modoEscucha = stop;
    }

    public String getNombreDeUsuario() {
        return nombreDeUsuario;
    }

    public void setNombreDeUsuario(String nombreDeUsuario) {
        this.nombreDeUsuario = nombreDeUsuario;
    }



}