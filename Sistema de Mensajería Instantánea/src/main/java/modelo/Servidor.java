package modelo;

import java.io.*;
import java.net.*;
import java.util.HashMap;

public class Servidor implements Runnable, Serializable {
    private final HashMap<Integer, Conexion> clientes = new HashMap<>();
    private final HashMap<Integer,Integer> sesiones = new HashMap<>();

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
            case "REGISTRAR" -> {
                System.out.print("\n ------------------------ \n MENSAJE CONTROL: REGISTRAR");
                procesarRegistro(conexion, mensaje);
            }
            case "CONECTAR" -> {
                System.out.print("\n ------------------------ \n MENSAJE CONTROL: CONECTAR\n");
                procesarConexion(mensaje);
            }
            case "CONEXION CORRECTA" -> procesarConexionAceptada(mensaje);
            case "ACEPTAR" -> {
                System.out.print("\n ------------------------ \n MENSAJE CONTROL: ACEPTAR");
                procesarAceptacion(mensaje);
            }
            case "RECHAZAR" -> {
                System.out.print("\n ------------------------ \n MENSAJE CONTROL: RECHAZAR");
                procesarRechazo(mensaje);
            }
            case "TEXTO" -> {
                System.out.print("\n ------------------------ \n MENSAJE CONTROL: TEXTO");
                procesarTexto(mensaje);
            }
            case "DESCONECTAR" -> procesarDesconexion(mensaje);
            case "CIERRO VENTANA SESION" -> {
                System.out.print("\n ------------------------ \n MENSAJE CONTROL: CIERRO VENTANA SESION");
                procesarCierroVentana(mensaje);
            }
            case "CIERRO VENTANA SESION LOCAL" -> {
                System.out.print("\n ------------------------ \n MENSAJE CONTROL: CIERRO VENTANA SESION");
                procesarCierroVentanaLocal(mensaje);
            }
            case "ERROR CONEXION" -> {
                System.out.print("\n ------------------------ \n MENSAJE CONTROL: ERRO CONEXION");
                mandarMensaje(1234, mensaje.getPuertoDestino(), "ERROR CONEXION", "", mensaje.getNombreUsuarioEmisor());
            }
            case "SOLICITAR NOMBRE" -> {
                System.out.print("\n ------------------------ \n MENSAJE CONTROL: SOLICITAR NOMBRE");
                mandarMensaje(1234, mensaje.getPuertoOrigen(), "NOMBRE", this.clientes.get(mensaje.getPuertoDestino()).getNombreUsuario(), mensaje.getNombreUsuarioEmisor());
            }
        }
    }

    // Agrego la conexion al servidor
    private void procesarRegistro(Conexion conexion,Mensaje mensaje) {
        conexion.setNombreUsuario(mensaje.getMensaje());
        clientes.put(mensaje.getPuertoOrigen(), conexion);
        System.out.print("Los clientes son" + clientes.toString());
    }

    //Aviso al puerto destino que me quiero conectar con el
    private void procesarConexion(Mensaje mensaje) {
        if (existeCliente(mensaje.getPuertoDestino())) {
            System.out.print("EL PUERTO ORIGEN ES" + mensaje.getPuertoOrigen());
            mandarMensaje(mensaje.getPuertoOrigen(), mensaje.getPuertoDestino(), "NUEVA_CONEXION", "", mensaje.getNombreUsuarioEmisor());
        }
        else{
            //TODO definir como variable el puerto del server
            // Aviso al origen que no existe el usuaario
            mandarMensaje(1234,mensaje.getPuertoOrigen(), "ERROR CONEXION","", mensaje.getNombreUsuarioEmisor());
        }
    }

    private void procesarConexionAceptada(Mensaje mensaje) {
        System.out.print("\n CONEXION ACEPTADA \nse mando conexion aceptadaa");
        mandarMensaje(1234,mensaje.getPuertoDestino(), "CONEXION CORRECTA","", mensaje.getNombreUsuarioEmisor());
    }

    //Aviso al puerto que me aceptaron la sesion, creo sesiones y abro ventana sesion.
    private void procesarAceptacion(Mensaje mensaje) {
        this.sesiones.put(mensaje.getPuertoOrigen(), mensaje.getPuertoDestino());
        this.sesiones.put(mensaje.getPuertoDestino(), mensaje.getPuertoOrigen());
        System.out.print("\n Puerto al que se quiere mandar mensaje" + mensaje.getPuertoDestino());
        mandarMensaje(mensaje.getPuertoOrigen(), mensaje.getPuertoDestino(), "Abro ventana sesion", "", mensaje.getNombreUsuarioEmisor());
    }

    //Aviso al puerto que me rechazaron la sesion
    //TODO hay que terminarlo
    private void procesarRechazo(Mensaje mensaje) {
        System.out.print("\n ------------------------ \n MENSAJE CONTROL: RECHAZAR");
        mandarMensaje(mensaje.getPuertoOrigen(), mensaje.getPuertoDestino(),"Rechazo conexion","", mensaje.getNombreUsuarioEmisor());
    }

    // Mando mensaje de texto entre sesiones, por las dudas verifico que la sesion exista
    private void procesarTexto(Mensaje mensaje) {
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
            Mensaje mensaje1 = new Mensaje(0,0,"Cerrar sesion","",null);
            clientes.get(puertoDestino).mandarMensaje(mensaje1);
        }
    }

    //chequeo si existe la conexion entre el puerto destino y el server, ademas
    private boolean existeCliente(int puertoDestino){
        return this.clientes.containsKey(puertoDestino);
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
//            int puertoDestino = sesiones.get(mensaje.getPuertoOrigen());
            Mensaje mensaje1 = new Mensaje(0,0,"CIERRO VENTANA SESION","",null);
            clientes.get(mensaje.getPuertoOrigen()).mandarMensaje(mensaje1);
        }
    }

//    private void cambiarModoEscucha(int puerto){
//        Conexion conex=this.clientes.get(puerto);
//        if(conex.isEstaModoEscucha())
//            conex.setEstaModoEscucha(false);
//        else
//            conex.setEstaModoEscucha(true);
//    }

    public void mandarMensaje(int puertoOrigen,int puertoDestino,String mensajeControl, String text, String nombreUsuarioEmisor){
        Mensaje mensaje = new Mensaje(puertoOrigen,puertoDestino,mensajeControl,text,nombreUsuarioEmisor);
        System.out.print("puerto destino" + puertoDestino);
        this.clientes.get(puertoDestino).mandarMensaje(mensaje);
    }
}