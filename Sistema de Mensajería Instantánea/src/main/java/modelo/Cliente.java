package modelo;

import static modelo.Cifrado.desencriptar;
import static modelo.Cifrado.encriptar;

import modelo.interfaces.IObservable;
import modelo.interfaces.IObserver;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;

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

    public Cliente(int puertoPropio) {
        this.puertoPropio = puertoPropio;
    }

    public void registrarServidor() throws Exception {
//        if (puertoDestino == this.puertoPropio)
//            throw new IOException();
        System.out.print("Intentando conectarse");
        Socket socket = new Socket(hostName, puertoServer);
        this.conexion.setSocket(socket);
        this.conexion.setOutput(new ObjectOutputStream(socket.getOutputStream()));
        this.conexion.setInput(new ObjectInputStream(socket.getInputStream()));
        Thread listenerMensajes = new Thread(() -> {
            try {
                listenerMensajes();
            } catch (Exception e) { //TODO propagar excepcion
                e.printStackTrace();
            }
        });
        listenerMensajes.start();
        this.registrar();
    }

    // TODO que lance una excepcion cuando no aceptan conexion
    public void crearConexion(int puertoDestino){
        Mensaje mensaje = new Mensaje(this.puertoPropio,puertoDestino,"CONECTAR","");
        this.conexion.mandarMensaje(mensaje);
        //this.conexion.getOutput()
    }


    private void listenerMensajes() throws Exception {
        Mensaje mensaje;
        while ((mensaje = (Mensaje) this.conexion.getInput().readObject()) != null ) {
            System.out.printf("\nEL MODO ESCUCHA ES" + this.modoEscucha);
            if (modoEscucha){
                procesarMensaje(mensaje);
            } else {
                System.out.printf("entro aca");
                notifyObservadores("ERROR CONEXION","");
            }
        }
    }

    private void procesarMensaje(Mensaje mensaje) throws Exception {
        String mensajeControl = mensaje.getMensajeControl();
        System.out.printf("\nel mensaje de CONTROL RECIBIDO: " + mensajeControl);
        switch (mensajeControl) {
            case "Abro ventana sesion" -> {
                System.out.print("INTENTANDO ABRIR VENTANA 1");
                notifyObservadores("Abro ventana sesion", "");
            }
            case "NUEVA_CONEXION" -> {
                System.out.printf("\nDiciendole al puerto que entro la solicitud" + mensaje.getPuertoDestino());
                mandarMensaje(mensaje.getPuertoOrigen(),"CONEXION CORRECTA","");
                notifyObservadores("Abro ventana notificacion", mensaje.getPuertoOrigen());
            }
            case "CIERRO VENTANA SESION" -> {
                System.out.printf("Se va a cerrar la sesion");
                notifyObservadores("CIERRO VENTANA SESION", "");
            }
            case "Acepto conexion" -> notifyObservadores("Acepto conexion", "");
            case "Rechazo conexion" -> notifyObservadores("Rechazo invitacion sesion", "");
            case "ERROR CONEXION" ->notifyObservadores("ERROR CONEXION","");
            case "CONEXION CORRECTA"->notifyObservadores("CONEXION CORRECTA","");
            default -> {
                byte[] textoEncriptado = Base64.getDecoder().decode(mensaje.getMensaje());
                String textoOriginal = desencriptar("12345678", textoEncriptado, "DES");
                notifyObservadores("Recibo mensaje", textoOriginal);
            }
        }
    }

    // TIPOS DE MENSAJES
    public void mandarMensaje(int puertoDestino, String mensajeControl, String text) {
        Mensaje mensaje = new Mensaje(this.puertoPropio,puertoDestino,mensajeControl,text);
        this.conexion.mandarMensaje(mensaje);
    }

    public void registrar() {
        this.mandarMensaje(puertoServer, "REGISTRAR", "");
    }

    public void cerrarVentanaSesion() {
        System.out.printf("Mandamos mensaje para cerrar sesion");
        this.mandarMensaje(puertoServer, "CIERRO VENTANA SESION", "");
    }

    public void aceptarConexion(int puertoDestino) {
        System.out.print("se acepto la conexion con puerto destino:" + puertoDestino);
        this.mandarMensaje(puertoDestino,"ACEPTAR","");
    }

    public void rechazarConexion(int puertoDestino){
        System.out.print("se rechazo la conexion con el puerto destino");
        this.mandarMensaje(puertoDestino,"RECHAZAR","");
    }

    public void mandarTexto(String mensaje) {
        try {
            byte[] textoEncriptado = encriptar("12345678", mensaje, "DES");
            String textoEncriptadoBase64 = Base64.getEncoder().encodeToString(textoEncriptado);
            mandarMensaje(-1, "TEXTO", textoEncriptadoBase64);
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
        for (IObserver obs : observadores) {
            obs.notificarCambio(estado, mensaje);
        }
    }

    @Override
    public void notifyObservadores(String estado, int puerto) {
        for (IObserver obs : observadores) {
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

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

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