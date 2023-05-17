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
<<<<<<< Updated upstream
=======
            System.out.println("Servidor iniciado. Esperando clientes...");


>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
            while ((mensaje = (Mensaje) reader.readObject()) != null) {
                procesarMensaje(clientSocket, conexion, mensaje);
=======
            while ((mensaje = (Mensaje)reader.readObject()) != null) {

                if (mensaje.getMensajeControl().equals("REGISTRAR")){
                    System.out.printf("\n ------------------------ \n MENSAJE CONTROL: REGISTRAR");
                    conexion.setPuertoServidor(1234);
                    conexion.setEstaModoEscucha(false);
                    clientes.put(mensaje.getPuertoOrigen(),conexion);
                    System.out.printf(clientes.toString());
                }
                if (mensaje.getMensajeControl().equals("CONECTAR")){
                    System.out.printf("\n ------------------------ \n MENSAJE CONTROL: CONECTAR\n");
                    // aviso al puerto destino que se quieren conectar con el
                    System.out.printf("\nPUERTO DESTINO" + mensaje.getPuertoDestino());
                    if (existeCliente(mensaje.getPuertoDestino(),mensaje.getPuertoOrigen())){
                        System.out.printf("\nABRIR PESTANA DE INVITACION A SESION");
                    }
                    else{
                        //TODO excepcion
                        System.out.printf("");
                    }
                }

                if (mensaje.getMensajeControl().equals("ACEPTAR")){
                    System.out.printf("\n ------------------------ \n MENSAJE CONTROL: CONECTAR");
                    // AVISAR AL EMISOR QUE SE ACEPTO
                    this.sesiones.put(mensaje.getPuertoOrigen(), mensaje.getPuertoDestino());
                    this.sesiones.put(mensaje.getPuertoDestino(), mensaje.getPuertoOrigen());
                    System.out.printf(sesiones.toString());
                    System.out.printf("\nse creo sesion entre clientes");
                }

                if (mensaje.getMensajeControl().equals("RECHAZAR")){
                    System.out.printf("\n ------------------------ \n MENSAJE CONTROL: RECHAZAR");
                    // si se recibe un mensaje de rechaza
                    // mando a la conexion que rechaza que se rechazo el chat
                }
                if (mensaje.getMensajeControl().equals("TEXTO")){
                    System.out.printf("\n ------------------------ \n MENSAJE CONTROL: TEXTO");
                    if (sesiones.containsKey(mensaje.getPuertoDestino())){
                        System.out.printf("MANDANDO MENSAJE");
                        System.out.printf(mensaje.getPuertoDestino() + "\n");
                        System.out.printf(clientes.toString() + "\n");
                        clientes.get(mensaje.getPuertoDestino()).mandarMensaje(mensaje);
                    }
                    else{

                    }
                }
                if (mensaje.getMensaje().equals("DESCONECTAR")){
                    System.out.printf("\n ------------------------ \n MENSAJE CONTROL: DESCONECTAR");
                    // se deshace la conexion
                }
                // manejo mensaje
                // Envía una respuesta al cliente
                // tipo mensaje
                //this.conexion.mandarMensaje(new Mensaje());
>>>>>>> Stashed changes
            }
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
    }

    //Aviso al puerto destino que me quiero conectar con el

    private void procesarConexion(Mensaje mensaje) {
        if (existeCliente(mensaje.getPuertoDestino())) {
            mandarMensaje(mensaje.getPuertoOrigen(), mensaje.getPuertoDestino(), "NUEVA_CONEXION", "");
        } else {
            System.out.printf("");
        }
    }

    //Aviso al puerto que me aceptaron la sesion, creo sesiones y abro ventana sesion.

    private void procesarAceptacion(Mensaje mensaje) {
        this.sesiones.put(mensaje.getPuertoOrigen(), mensaje.getPuertoDestino());
        this.sesiones.put(mensaje.getPuertoDestino(), mensaje.getPuertoOrigen());
        mandarMensaje(mensaje.getPuertoOrigen(), mensaje.getPuertoDestino(), "Abro ventana sesion", "");
    }

    //Aviso al puerto que me rechazaron la sesion
    //TODO hay que terminarlo
    private void procesarRechazo(Conexion conexion, Mensaje mensaje) {
        System.out.printf("\n ------------------------ \n MENSAJE CONTROL: RECHAZAR");

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

<<<<<<< Updated upstream
    private boolean existeCliente(int puerto){
        return this.clientes.containsKey(puerto);
=======
    private boolean existeCliente(int puertoDestino, int puertoOrigen){
        System.out.println("\n ESTOY ACA");
        System.out.printf(this.clientes.get(puertoDestino).toString());
        if(this.clientes.containsKey(puertoDestino) && this.conexion.isEstaModoEscucha())
            return true;
        else
            return false;
>>>>>>> Stashed changes
    }


    public void mandarMensaje(int puertoOrigen,int puertoDestino,String mensajeControl, String text){
        Mensaje mensaje = new Mensaje(puertoOrigen,puertoDestino,mensajeControl,text);
        this.clientes.get(puertoDestino).mandarMensaje(mensaje);
    }

}