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
    private ArrayList<IObserver> observadores = new ArrayList<>();
    //TODO los socket cliente y server podrian estar dentro de una clase mensajes que implementa IMensajes
    private Conexion conexion = new Conexion();
    public boolean modoEscucha = false;

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
        Mensaje mensaje = new Mensaje(this.puertoPropio,puertoDestino,"CONECTAR","",this.nombreDeUsuario);
        this.conexion.mandarMensaje(mensaje);
        //this.conexion.getOutput()
    }


    private void listenerMensajes() throws Exception {
        Mensaje mensaje;
        while ((mensaje = (Mensaje) this.conexion.getInput().readObject()) != null ) {
            System.out.print("\nEL MODO ESCUCHA ES" + this.modoEscucha);
            if (modoEscucha){
                procesarMensaje(mensaje);
            } else {
                mandarMensaje(mensaje.getPuertoOrigen(),"ERROR CONEXION","");
            }
        }
    }

    private void procesarMensaje(Mensaje mensaje) throws Exception {
        String mensajeControl = mensaje.getMensajeControl();
        System.out.print("\nel mensaje de CONTROL RECIBIDO: " + mensajeControl);
        switch (mensajeControl) {
            case "Abro ventana sesion" -> {
                System.out.print("INTENTANDO ABRIR VENTANA 1");
                notifyObservadores("Abro ventana sesion", "",mensaje.getNombreUsuarioEmisor());
            }
            case "NUEVA_CONEXION" -> {
                System.out.print("\nDiciendole al puerto que entro la solicitud" + mensaje.getPuertoDestino());
                mandarMensaje(mensaje.getPuertoOrigen(),"CONEXION CORRECTA","");
                notifyObservadores("Abro ventana notificacion", mensaje.getPuertoOrigen(),mensaje.getNombreUsuarioEmisor());
            }
            case "CIERRO VENTANA SESION" -> {
                System.out.print("Se va a cerrar la sesion");
                notifyObservadores("CIERRO VENTANA SESION", "", mensaje.getNombreUsuarioEmisor());
            }
            case "Acepto conexion" -> notifyObservadores("Acepto conexion", "",mensaje.getNombreUsuarioEmisor());
            case "Rechazo conexion" -> notifyObservadores("Rechazo invitacion sesion", "",mensaje.getNombreUsuarioEmisor());
            case "ERROR CONEXION" ->notifyObservadores("ERROR CONEXION","",mensaje.getNombreUsuarioEmisor());
            case "CONEXION CORRECTA"->notifyObservadores("CONEXION CORRECTA","",mensaje.getNombreUsuarioEmisor());
            case "SOLICITAR NOMBRE" ->mandarMensaje(mensaje.getPuertoDestino(),"SOLICITAR NOMBRE","" );
            case "NOMBRE"-> {
                System.out.print("\nse seteo el nombre: " + mensaje.getMensaje());
                this.setNombreDeUsuarioReceptor(mensaje.getMensaje());
            }
            default -> {
                byte[] textoEncriptado = Base64.getDecoder().decode(mensaje.getMensaje());
                String textoOriginal = desencriptar("12345678", textoEncriptado, "DES");
                notifyObservadores("Recibo mensaje", textoOriginal,mensaje.getNombreUsuarioEmisor());
            }
        }
    }

    // TIPOS DE MENSAJES
    public void mandarMensaje(int puertoDestino, String mensajeControl, String text) {
        Mensaje mensaje = new Mensaje(this.puertoPropio,puertoDestino,mensajeControl,text,this.nombreDeUsuario);
        this.conexion.mandarMensaje(mensaje);
    }

    public void setearNombreReceptor(int puertoDestino ){
        Mensaje mensaje = new Mensaje(this.puertoPropio,puertoDestino,"SOLICITAR NOMBRE","",this.nombreDeUsuario);
        this.conexion.mandarMensaje(mensaje);
    }

    public void registrar(String nombreDeUsuario) {
        this.mandarMensaje(puertoServer, "REGISTRAR", nombreDeUsuario);
    }

    public void cerrarVentanaSesion() {
        System.out.print("Mandamos mensaje para cerrar sesion");
        this.mandarMensaje(puertoServer, "CIERRO VENTANA SESION", "");
    }

    public void cerrarVentanaSesionLocal() {
        System.out.print("Mandamos mensaje para cerrar sesion");
        this.mandarMensaje(puertoServer, "CIERRO VENTANA SESION LOCAL", "");
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
}