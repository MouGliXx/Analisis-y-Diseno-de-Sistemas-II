package modelo;

import static modelo.Cifrado.desencriptar;
import static modelo.Cifrado.encriptar;

import modelo.interfaces.IObservable;
import modelo.interfaces.IObserver;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;

public class Cliente implements IObservable{
    private final String hostName = "localhost";
    private String nombreDeUsuario;
    private String nombreDeUsuarioReceptor;
    private  int puertoPropio;
    private  int puertoServer = 1234;
    private String usuario = "";
    private ArrayList<IObserver> observadores = new ArrayList<>();

    //TODO los socket cliente y server podrian estar dentro de una clase mensajes que implementa IMensajes
    private Conexion conexion = new Conexion();
    public boolean modoEscucha = false;
    private boolean enSesion = false;

    public Cliente(int puertoPropio) {
        this.puertoPropio = puertoPropio;
    }

    public void registrarServidor(String nombreDeUsuario) throws Exception {
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
        this.registrar(nombreDeUsuario);
    }

    // TODO que lance una excepcion cuando no aceptan conexion
    public void crearConexion(int puertoDestino){
        Mensaje mensaje = new Mensaje(this.puertoPropio,puertoDestino,"NUEVA CONEXION","",this.nombreDeUsuario);
        this.conexion.mandarMensaje(mensaje);
        //this.conexion.getOutput()
    }


    private void listenerMensajes() throws Exception {
        Mensaje mensaje;
        while ((mensaje = (Mensaje) this.conexion.getInput().readObject()) != null ) {
            System.out.printf("\nEL MODO ESCUCHA ES" + this.modoEscucha);
            System.out.printf("\nEl modo sesion es " + this.enSesion);
            if (modoEscucha){
                procesarMensaje(mensaje);
            } else {
                mandarMensaje(mensaje.getPuertoOrigen(),"ERROR CONEXION","");
            }
        }
    }

    private void procesarMensaje(Mensaje mensaje) throws Exception {
        String mensajeControl = mensaje.getMensajeControl();
        System.out.printf("\nel mensaje de CONTROL RECIBIDO: " + mensajeControl);

        switch (mensajeControl) {
            case "ACEPTAR" ->{
                this.enSesion = true;
                System.out.printf("" + "\nACEPTAR --- se cambio el enSesion");
                System.out.printf("\n En sesion " + this.enSesion);
                notifyObservadores("Abro ventana sesion", "", mensaje.getNombreUsuarioEmisor());
            }
            case "NUEVA CONEXION" -> procesarNuevaConexion(mensaje);
            case "CIERRO VENTANA SESION" -> procesarCierreSesion(mensaje);
            case "Acepto conexion" -> {
                notifyObservadores("Acepto conexion", "", mensaje.getNombreUsuarioEmisor());
            }
            case "RECHAZAR" -> notifyObservadores("Rechazo invitacion sesion", "", mensaje.getNombreUsuarioEmisor());
            case "ERROR CONEXION" -> notifyObservadores("ERROR CONEXION", "", mensaje.getNombreUsuarioEmisor());
            case "CONEXION CORRECTA" -> notifyObservadores("CONEXION CORRECTA", "", mensaje.getNombreUsuarioEmisor());
            case "SOLICITAR NOMBRE" -> mandarMensaje(mensaje.getPuertoDestino(), "SOLICITAR NOMBRE", "");
            case "NOMBRE" -> procesarNombre(mensaje);
            default -> procesarMensajeRecibido(mensaje);
        }
    }



    private void procesarNuevaConexion(Mensaje mensaje) {
        System.out.printf("\nDiciendole al puerto que entro la solicitud" + mensaje.getPuertoDestino());
        if (!enSesion) {
            mandarMensaje(mensaje.getPuertoOrigen(), "CONEXION CORRECTA", "");
            notifyObservadores("Abro ventana notificacion", mensaje.getPuertoOrigen(), mensaje.getNombreUsuarioEmisor());
        }
        else{
            mandarMensaje(mensaje.getPuertoOrigen(),"ERROR CONEXION","");
        }
        }

    private void procesarCierreSesion(Mensaje mensaje) {
        System.out.printf("Se va a cerrar la sesion");
        notifyObservadores("CIERRO VENTANA SESION", "", mensaje.getNombreUsuarioEmisor());
    }

    private void procesarNombre(Mensaje mensaje) {
        System.out.printf("se seteo el nombre");
        System.out.printf("se seteo el nombre");
        this.setNombreDeUsuarioReceptor(mensaje.getMensaje());
    }

    private void procesarMensajeRecibido(Mensaje mensaje) throws Exception{
        byte[] textoEncriptado = Base64.getDecoder().decode(mensaje.getMensaje());
        String textoOriginal = desencriptar("12345678", textoEncriptado, "DES");
        notifyObservadores("Recibo mensaje", textoOriginal, mensaje.getNombreUsuarioEmisor());
    }

    // TIPOS DE MENSAJES

    private void mandarMensaje(int puertoDestino, String mensajeControl, String text) {
        Mensaje mensaje = new Mensaje(this.puertoPropio,puertoDestino,mensajeControl,text,this.nombreDeUsuario);
        this.conexion.mandarMensaje(mensaje);
    }

    public void setearNombreReceptor(int puertoDestino ){
        this.mandarMensaje(puertoDestino,"SOLICITAR NOMBRE","");
    }

    public void registrar(String nombreDeUsuario) {
        this.mandarMensaje(puertoServer, "REGISTRAR", nombreDeUsuario);
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

    public void cerrarVentanaSesion() {
        System.out.printf("Mandamos mensaje para cerrar sesion");
        this.mandarMensaje(puertoServer, "CIERRO VENTANA SESION", "");
    }

    public void cerrarVentanaSesionLocal() {
        System.out.printf("Mandamos mensaje para cerrar sesion");
        this.mandarMensaje(puertoServer, "CIERRO VENTANA SESION LOCAL", "");
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
    public void notifyObservadores(String estado, String mensaje,String nombreUsuarioEmisor) {
        Iterator<IObserver> iter = observadores.iterator();
        while (iter.hasNext()) {
            IObserver obs = iter.next();
            obs.notificarCambio(estado, mensaje,nombreUsuarioEmisor);
        }
    }

    @Override
    public void notifyObservadores(String estado, int puerto, String nombreUsuarioEmisor) {
        Iterator<IObserver> iter = observadores.iterator();
        while (iter.hasNext()) {
            IObserver obs = iter.next();
            obs.notificarCambio(estado, puerto,nombreUsuarioEmisor);
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

    public void setModoEscucha(boolean stop) {
        modoEscucha = stop;
    }

    public String getNombreDeUsuarioReceptor() {
        return nombreDeUsuarioReceptor;
    }

    public void setNombreDeUsuarioReceptor(String nombreDeUsuarioReceptor) {
        this.nombreDeUsuarioReceptor = nombreDeUsuarioReceptor;
    }

    public String getNombreDeUsuario() {
        return nombreDeUsuario;
    }

    public void setNombreDeUsuario(String nombreDeUsuario) {
        this.nombreDeUsuario = nombreDeUsuario;
    }

    public boolean isEnSesion() {
        return enSesion;
    }

    public void setEnSesion(boolean enSesion) {
        this.enSesion = enSesion;
    }
}