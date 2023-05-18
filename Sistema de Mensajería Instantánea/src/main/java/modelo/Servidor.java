package modelo;

import java.io.*;
import java.net.*;
import java.util.HashMap;

public class Servidor implements Runnable, Serializable {
    private HashMap<Integer, Conexion> clientes = new HashMap<>();
    private HashMap<Integer,Integer> sesiones = new HashMap<>();

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
                procesarMensaje(clientSocket, conexion, mensaje);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void procesarMensaje(Socket clientSocket, Conexion conexion, Mensaje mensaje) {
        String mensajeControl = mensaje.getMensajeControl();
        switch (mensajeControl) {
            case "REGISTRAR":
                System.out.printf("\n ------------------------ \n MENSAJE CONTROL: REGISTRAR");
                procesarRegistro(conexion,mensaje);
                break;
            case "CONECTAR":
                System.out.printf("\n ------------------------ \n MENSAJE CONTROL: CONECTAR\n");
                procesarConexion(mensaje);
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
            default:
                break;
        }
    }

    // Agrego la conexion al servidor
    private void procesarRegistro(Conexion conexion,Mensaje mensaje) {
        clientes.put(mensaje.getPuertoOrigen(), conexion);
        System.out.printf("Los clientes son" + clientes.toString());
    }

    //Aviso al puerto destino que me quiero conectar con el
    private void procesarConexion(Mensaje mensaje) {
        System.out.println("LLegueeasfd");
        if (existeCliente(mensaje.getPuertoDestino(),mensaje.getPuertoOrigen())) {
            mandarMensaje(mensaje.getPuertoOrigen(), mensaje.getPuertoDestino(), "NUEVA_CONEXION", "");
        }
    }

    //Aviso al puerto que me aceptaron la sesion, creo sesiones y abro ventana sesion.
    private void procesarAceptacion(Mensaje mensaje) {
        this.sesiones.put(mensaje.getPuertoOrigen(), mensaje.getPuertoDestino());
        this.sesiones.put(mensaje.getPuertoDestino(), mensaje.getPuertoOrigen());
        System.out.printf("\n Puerto al que se quiere mandar mensaje" + mensaje.getPuertoDestino());
        mandarMensaje(mensaje.getPuertoOrigen(), mensaje.getPuertoDestino(), "Abro ventana sesion", "");
    }

    //Aviso al puerto que me rechazaron la sesion
    //TODO hay que terminarlo
    private void procesarRechazo(Conexion conexion, Mensaje mensaje) {
        System.out.printf("\n ------------------------ \n MENSAJE CONTROL: RECHAZAR");
        mandarMensaje(mensaje.getPuertoOrigen(), mensaje.getPuertoDestino(),"Rechazo conexion","");
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
            Mensaje mensaje1 = new Mensaje(0,0,"Cerrar sesion","");
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

//    private void cambiarModoEscucha(int puerto){
//        Conexion conex=this.clientes.get(puerto);
//        if(conex.isEstaModoEscucha())
//            conex.setEstaModoEscucha(false);
//        else
//            conex.setEstaModoEscucha(true);
//    }


    public void mandarMensaje(int puertoOrigen,int puertoDestino,String mensajeControl, String text){
        Mensaje mensaje = new Mensaje(puertoOrigen,puertoDestino,mensajeControl,text);
        this.clientes.get(puertoDestino).mandarMensaje(mensaje);
    }
}