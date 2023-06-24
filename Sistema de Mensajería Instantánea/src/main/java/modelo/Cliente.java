package modelo;

import static modelo.Cifrado.desencriptar;
import static modelo.Cifrado.encriptar;

import modelo.interfaces.IConexion;
import modelo.interfaces.IObservable;
import modelo.interfaces.IObserver;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;

public class Cliente implements IObservable, IConexion {
    private final String hostName = "localhost";
    private String nombreDeUsuario;
    private String nombreDeUsuarioReceptor;
    private  int puertoPropio;
    private  int puertoServer = 1235;
    private final int[] PUERTOS = {1235,1234};
    private String usuario = "";
    private ArrayList<IObserver> observadores = new ArrayList<>();
    private ArrayList<Integer> servidores = new ArrayList<>();
    private HashMap<Integer,Conexion> conexiones = new HashMap<>();


    //TODO los socket cliente y server podrian estar dentro de una clase mensajes que implementa IMensajes
    private Conexion conexion = null;
    public boolean modoEscucha = false;
    private boolean enSesion = false;
    private boolean redundancia = false;

    public Cliente(int puertoPropio) {
        this.puertoPropio = puertoPropio;
        for (int puerto:PUERTOS){
            servidores.add(puerto);
        }
    }

    public void registrarServidor(String nombreDeUsuario) throws Exception {
        System.out.print("Intentando conectarse");
        for (Integer puerto : servidores) {
            Thread thread = new Thread(() -> {
                try {
                    Conexion socket = conectar(puerto);
                    System.out.printf("\nNos conectamos al puerto: " + puerto);
                    conexiones.put(puerto,socket); // Almacena la referencia al socket en la lista
                    if (this.conexion == null) {
                        this.conexion = socket; // Asigna el primer servidor conectado como principal
                        System.out.println("\nConectado al servidor principal: " + socket);
                    }
                    listenerMensajes(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("No se pudo establecer la conexión con el servidor: " + puerto);
                }
                catch(Exception e1){
                    e1.printStackTrace();
                    System.out.printf("Error en la recepcion de mensajes");
                }
            });
            thread.start();
            Thread.sleep(1000);
        }
        this.registrar(nombreDeUsuario);
    }

    private static Conexion conectar(int puerto) throws IOException {
        Socket socket = new Socket("localhost", puerto);
        Conexion conexion = new Conexion();
        conexion.setSocket(socket);
        conexion.setOutput(new ObjectOutputStream(socket.getOutputStream()));
        conexion.setInput(new ObjectInputStream(socket.getInputStream()));
        return conexion;
    }

    private void listenerMensajes(Conexion conexion) throws Exception {
        Mensaje mensaje;
        Object obj;
        try {
            while ((obj = conexion.getInput().readObject()) != null) {
                System.out.printf("Se recibio mensaje");
                if (obj instanceof Mensaje) {
                    System.out.printf("\n Instancia de mensaje");
                    mensaje = (Mensaje) obj;
                    System.out.printf("\nEL MODO ESCUCHA ES" + this.modoEscucha);
                    System.out.printf("\nEl modo sesion es " + this.enSesion);
                    if (modoEscucha) {
                        procesarMensaje(mensaje);
                    } else {
                        mandarMensaje(mensaje.getPuertoOrigen(), "ERROR CONEXION", "");
                    }
                } else {
                    System.out.printf("\nNo es instancia de mensaje");
                    String mensaje2 = (String) obj;
                    System.out.printf(mensaje2);
                }

            }
        }
        catch (SocketException e){
            System.out.println("Error al enviar el mensaje al servidor principal " );
            // Intentar reconexión con el servidor principal
            Conexion servidorPrincipal = reconectar();
            if (servidorPrincipal == null){
                this.notifyObservadores("SERVIDOR OUT","",null);
            }
            else {
                this.registrar(nombreDeUsuario);
                System.out.println("Se ha seleccionado un nuevo servidor principal: ");
            }
        }
    }

    public Conexion reconectar(){
        this.conexion.close();

        // Intenta establecer una nueva conexión con el servidor principal
        Conexion nuevoSocket = null;
        for (Integer puerto : servidores) {
            try {
                nuevoSocket = conectar(puerto);
                System.out.printf("Nos conectamos nuevamente al puerto" + puerto);
                break;
            } catch (IOException e) {
                System.out.println("No se pudo establecer la conexión con el servidor " + puerto);
            }
        }

        if (nuevoSocket != null) {
            // Reemplaza el socket principal con el nuevo socket
            this.conexion = nuevoSocket;

            // Inicia la recepción de mensajes del nuevo socket en un nuevo hilo
            Thread thread = new Thread(() -> {
                try {
                    listenerMensajes(conexion);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Se cerro por segunda vez la conexion nueva! " );
                }
            });
            thread.start();
        }

        // Retorna el nuevo socket principal (o null si no se pudo establecer la conexión)
        return nuevoSocket;
    }

    public void cambiarServidor(int index){
        System.out.printf("\n Las conexiones son" + conexiones);
        //aaathis.conexion = conexiones.get(index);
    }

    // TODO que lance una excepcion cuando no aceptan conexion
    public void crearConexion(int puertoDestino){
        System.out.printf("\nLAS CONEXIONES DE LOS SERVIDORES SON" + conexiones.toString());
//        Mensaje mensaje = new Mensaje(this.puertoPropio,puertoDestino,"NUEVA CONEXION","",this.nombreDeUsuario);
        this.mandarMensaje(puertoDestino,"NUEVA CONEXION","");

        System.out.printf("\nSe mando mensaje");
        //this.conexion.getOutput()
    }



    public void cerrarConexion(){
        this.mandarMensaje(-1,"CERRAR CONEXION","");
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
            case "Acepto conexion" -> notifyObservadores("Acepto conexion", "", mensaje.getNombreUsuarioEmisor());
            case "RECHAZAR" -> notifyObservadores("Rechazo invitacion sesion", "", mensaje.getNombreUsuarioEmisor());
            case "ERROR CONEXION" -> notifyObservadores("ERROR CONEXION", "", mensaje.getNombreUsuarioEmisor());
            case "CONEXION CORRECTA" -> notifyObservadores("CONEXION CORRECTA", "", mensaje.getNombreUsuarioEmisor());
            case "SOLICITAR NOMBRE" -> mandarMensaje(mensaje.getPuertoDestino(), "SOLICITAR NOMBRE", "");
            case "NOMBRE" -> procesarNombre(mensaje);
            case "LISTA USUARIOS" -> notifyObservadores("LISTA USUARIOS",mensaje.getMensaje(),""); //TODO agregar notifyObservadores con la lista de usuarios
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
        System.out.printf("\nEl nombre de usuario es" + nombreDeUsuario);
        this.mandarMensaje(puertoServer, "REGISTRAR", nombreDeUsuario);
    }

    public void aceptarConexion(int puertoDestino) {
        System.out.print("se acepto la conexion con puerto destino:" + puertoDestino);
        this.mandarMensaje(puertoDestino,"ACEPTAR","");
    }

    public void listaUsuarios(){
        System.out.printf("Se mando mensaje");
        this.mandarMensaje(puertoServer,"LISTA USUARIOS","");
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