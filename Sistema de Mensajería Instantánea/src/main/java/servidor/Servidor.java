package servidor;

import modelo.Conexion;
import modelo.Mensaje;
import java.io.*;
import java.net.*;
import java.util.HashMap;

public class Servidor implements Runnable, Serializable {
    private HashMap<Integer, Conexion> clientes = new HashMap<>();
    private HashMap<Integer,Integer> sesiones = new HashMap<>();

    public static void main(String[] args) {
        Thread servidor = new Thread(new Servidor());
        servidor.start();
    }

    public Servidor() {
    }

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Conexion conexion = new Conexion();
                conexion.setSocket(clientSocket);
                conexion.setOutput(new ObjectOutputStream(conexion.getSocket().getOutputStream()));
                Thread listenerMensajes = new Thread(() -> listenerMensajes(clientSocket, conexion));
                listenerMensajes.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenerMensajes(Socket clientSocket, Conexion conexion) {
        try {
            ObjectInputStream reader = new ObjectInputStream(clientSocket.getInputStream());
            Mensaje mensaje;
            while ((mensaje = (Mensaje) reader.readObject()) != null)
                procesarMensaje(conexion, mensaje);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void procesarMensaje(Conexion conexion, Mensaje mensaje) {
        String mensajeControl = mensaje.getMensajeControl();
        switch (mensajeControl) {
            case "REGISTRAR":
                System.out.printf("\n ------------------------ \n MENSAJE CONTROL: REGISTRAR");
                procesarRegistro(conexion,mensaje);
                break;
            case "NUEVA CONEXION":
                System.out.printf("\n ------------------------ \n MENSAJE CONTROL: CONECTAR\n");
                procesarConexion(mensaje);
                break;
            case "CONEXION CORRECTA":
                procesarConexionAceptada(mensaje);
                break;
            case "ACEPTAR":
                System.out.printf("\n ------------------------ \n MENSAJE CONTROL: ACEPTAR");
                procesarAceptacion(mensaje);
                break;
            case "RECHAZAR":
                System.out.printf("\n ------------------------ \n MENSAJE CONTROL: RECHAZAR");
                procesarRechazo(conexion, mensaje);
                break;
            case "TEXTO":
                System.out.printf("\n ------------------------ \n MENSAJE CONTROL: TEXTO");
                procesarTexto(conexion, mensaje);
                break;
            case "DESCONECTAR":
                procesarDesconexion(mensaje);
                break;
            case "CIERRO VENTANA SESION":
                System.out.printf("\n ------------------------ \n MENSAJE CONTROL: CIERRO VENTANA SESION");
                procesarCierroVentana(mensaje);
                break;
            case "CIERRO VENTANA SESION LOCAL":
                System.out.printf("\n ------------------------ \n MENSAJE CONTROL: CIERRO VENTANA SESION");
                procesarCierroVentanaLocal(mensaje);
                break;
            case "ERROR CONEXION":
                System.out.printf("\n ------------------------ \n MENSAJE CONTROL: ERRO CONEXION");
                mandarMensaje(1234,mensaje.getPuertoDestino(), "ERROR CONEXION","",mensaje.getNombreUsuarioEmisor());
                break;
            case "SOLICITAR NOMBRE":
                System.out.printf("\n ------------------------ \n MENSAJE CONTROL: SOLICITAR NOMBRE");
                if (this.clientes.containsKey(mensaje.getPuertoDestino()))
                    mandarMensaje(1234,mensaje.getPuertoOrigen(), "NOMBRE",this.clientes.get(mensaje.getPuertoDestino()).getNombreUsuario(),mensaje.getNombreUsuarioEmisor());
                else
                    mandarMensaje(1234,mensaje.getPuertoOrigen(), "NOMBRE","",mensaje.getNombreUsuarioEmisor());
                break;
        }
    }

    // Agrego la conexion al servidor
    private void procesarRegistro(Conexion conexion,Mensaje mensaje) {
        conexion.setNombreUsuario(mensaje.getMensaje());
        clientes.put(mensaje.getPuertoOrigen(), conexion);
        System.out.printf("Los clientes son" + clientes.toString());
    }

    //Aviso al puerto destino que me quiero conectar con el
    private void procesarConexion(Mensaje mensaje) {
        if (existeCliente(mensaje.getPuertoDestino(),mensaje.getPuertoOrigen())) {
            System.out.printf("EL PUERTO ORIGEN ES" + mensaje.getPuertoOrigen());
            mandarMensaje(mensaje.getPuertoOrigen(), mensaje.getPuertoDestino(), "NUEVA CONEXION", "", mensaje.getNombreUsuarioEmisor());
        }
        else{
            //TODO definir como variable el puerto del server
            // Aviso al origen que no existe el usuaario
            mandarMensaje(1234,mensaje.getPuertoOrigen(), "ERROR CONEXION","", mensaje.getNombreUsuarioEmisor());
        }
    }

    private void procesarConexionAceptada(Mensaje mensaje) {
        System.out.printf("\n CONEXION ACEPTADA \nse mando conexion aceptadaa");
        mandarMensaje(1234,mensaje.getPuertoDestino(), "CONEXION CORRECTA","", mensaje.getNombreUsuarioEmisor());
    }

    //Aviso al puerto que me aceptaron la sesion, creo sesiones y abro ventana sesion.
    private void procesarAceptacion(Mensaje mensaje) {
        this.sesiones.put(mensaje.getPuertoOrigen(), mensaje.getPuertoDestino());
        this.sesiones.put(mensaje.getPuertoDestino(), mensaje.getPuertoOrigen());
        System.out.printf("\n Puerto al que se quiere mandar mensaje" + mensaje.getPuertoDestino());
        mandarMensaje(mensaje.getPuertoOrigen(), mensaje.getPuertoDestino(), "ACEPTAR", "", mensaje.getNombreUsuarioEmisor());
    }

    //Aviso al puerto que me rechazaron la sesion
    //TODO hay que terminarlo
    private void procesarRechazo(Conexion conexion, Mensaje mensaje) {
        System.out.printf("\n ------------------------ \n MENSAJE CONTROL: RECHAZAR");
        mandarMensaje(mensaje.getPuertoOrigen(), mensaje.getPuertoDestino(),"RECHAZAR","", mensaje.getNombreUsuarioEmisor());
    }

    // Mando mensaje de texto entre sesiones, por las dudas verifico que la sesion exista
    private void procesarTexto(Conexion conexion, Mensaje mensaje) {
        if (sesiones.containsKey(mensaje.getPuertoOrigen())) {
            int puertoDestino = sesiones.get(mensaje.getPuertoOrigen());
            clientes.get(puertoDestino).mandarMensaje(mensaje);
        }
    }

    private void procesarDesconexion(Mensaje mensaje){
        if (sesiones.containsKey(mensaje.getPuertoOrigen())){
            int puertoDestino = sesiones.get(mensaje.getPuertoOrigen());
            sesiones.remove(puertoDestino);
            sesiones.remove(mensaje.getPuertoOrigen());
            Mensaje mensaje1 = new Mensaje(0,0,"CERRAR SESION","",null);
            clientes.get(puertoDestino).mandarMensaje(mensaje1);
        }
    }

    //chequeo si existe la conexion entre el puerto destino y el server, ademas
    private boolean existeCliente(int puertoDestino, int puertoOrigen){
        if(this.clientes.containsKey(puertoDestino))
            return true;
        else
            return false;
    }

    private void procesarCierroVentana(Mensaje mensaje){
        if (sesiones.containsKey(mensaje.getPuertoOrigen())) {
            int puertoDestino = sesiones.get(mensaje.getPuertoOrigen());
            Mensaje mensaje1 = new Mensaje(0,0,"CIERRO VENTANA SESION","",null);
            clientes.get(puertoDestino).mandarMensaje(mensaje1);
        }
    }

    private void procesarCierroVentanaLocal(Mensaje mensaje){
        if (sesiones.containsKey(mensaje.getPuertoOrigen())) {
            int puertoDestino = sesiones.get(mensaje.getPuertoOrigen());
            Mensaje mensaje1 = new Mensaje(0,0,"CIERRO VENTANA SESION","",null);
            clientes.get(mensaje.getPuertoOrigen()).mandarMensaje(mensaje1);
        }
    }

    public void mandarMensaje(int puertoOrigen,int puertoDestino,String mensajeControl, String text, String nombreUsuarioEmisor){
        Mensaje mensaje = new Mensaje(puertoOrigen,puertoDestino,mensajeControl,text,nombreUsuarioEmisor);
        System.out.printf("puerto destino" + puertoDestino);
        this.clientes.get(puertoDestino).mandarMensaje(mensaje);
    }
}