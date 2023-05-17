package modelo;

import java.io.*;
import java.net.*;
import java.util.HashMap;

public class Servidor implements Runnable, Serializable {
    private SocketIO2 conexion = new SocketIO2();
    private HashMap<Integer,SocketIO2> clientes = new HashMap<>();
    private HashMap<Integer,Integer> sesiones = new HashMap<>();

    public Servidor() {
    }

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("Servidor iniciado. Esperando clientes...");

            while (true) {

                Socket clientSocket = serverSocket.accept();

                // Registro del usuario
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress() + "Port:" +clientSocket.getPort() + "\n");
                this.conexion.setSocket(clientSocket);
                this.conexion.setOutput(new ObjectOutputStream(conexion.getSocket().getOutputStream()));
                // Ejecuto el metodo Client Thread en un hilo aparte para que reciba mensajes
                Thread listenerMensajes = new Thread(()-> listenerMensajes(clientSocket));
                listenerMensajes.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void listenerMensajes(Socket clientSocket) {
        try {
            ObjectInputStream reader = new ObjectInputStream(clientSocket.getInputStream());
            Mensaje mensaje;
            while ((mensaje = (Mensaje)reader.readObject()) != null) {

                if (mensaje.getMensajeControl().equals("REGISTRAR")){
                    System.out.printf("\nREGISTRAR");
                    clientes.put(mensaje.getPuertoOrigen(),conexion);
                    System.out.printf(clientes.toString());
                }
                if (mensaje.getMensajeControl().equals("CONECTAR")){
                    System.out.printf("\nCONECTAR");
                    // aviso al puerto destino que se quieren conectar con el
                    System.out.printf("\nIntentando conectar con otro cliente");
                    System.out.printf("\nPUERTO DESTINO" + mensaje.getPuertoDestino());
                    if (existeCliente(mensaje.getPuertoDestino())){
                        System.out.printf("\nABRIR PESTANA DE INVITACION A SESION");
                    }
                    else{
                        //TODO excepcion
                        System.out.printf("\nHACER EXCEPCION NO EXISTE CLIENTE O NO ESTA EN MODO ESCUCHA VENTANA");
                    }
                }

                if (mensaje.getMensajeControl().equals("ACEPTAR")){
                    System.out.printf("\nACEPTAR");
                    // AVISAR AL EMISOR QUE SE ACEPTO
                    this.sesiones.put(mensaje.getPuertoOrigen(), mensaje.getPuertoDestino());
                    this.sesiones.put(mensaje.getPuertoDestino(), mensaje.getPuertoOrigen());
                    System.out.printf(sesiones.toString());
                    System.out.printf("\nse creo sesion entre clientes");
                }

                if (mensaje.getMensajeControl().equals("RECHAZA")){
                    // si se recibe un mensaje de rechaza
                    // mando a la conexion que rechaza que se rechazo el chat
                }
                if (mensaje.getMensajeControl().equals("TEXTO")){
                    if (sesiones.containsKey(mensaje.getPuertoDestino())){
                        System.out.printf("MANDANDO MENSAJE");
                        System.out.printf(mensaje.getPuertoDestino() + "\n");
                        System.out.printf(clientes.toString() + "\n");
                        clientes.get(mensaje.getPuertoDestino()).mandarMensaje(mensaje);
                    }
                }
                if (mensaje.getMensaje().equals("DESCONECTAR")){
                    // se deshace la conexion
                }
                // manejo mensaje
                // Envía una respuesta al cliente
                // tipo mensaje
                //this.conexion.mandarMensaje(new Mensaje());
            }

            // Cierra la conexión del cliente
            //conexion.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean existeCliente(int puerto){
        System.out.printf(this.clientes.get(puerto).toString());
        return this.clientes.containsKey(puerto);
    }

    private void tipoMensaje(Mensaje mensaje){

    }
}