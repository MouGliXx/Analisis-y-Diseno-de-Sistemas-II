package servidor;

import modelo.Conexion;
import modelo.Mensaje;
import java.io.*;
import java.net.*;
import java.util.HashMap;

public class Servidor implements Runnable, Serializable {
    private HashMap<Integer, Conexion> clientes = new HashMap<>();
    private HashMap<Integer,Integer> sesiones = new HashMap<>();
    private HashMap<Integer, String> clientesConectados = new HashMap<>();
    private int puerto;
    private int puertoRedundancia;
    private Conexion redundancia = null;
    private boolean hayRedundancia = false;
    private int servidores[] = {1235,1234};

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1235);
            serverSocket.close();
            Thread servidor = new Thread(new Servidor(1235));
            servidor.start();
        }
        catch( Exception e){
            try {
                ServerSocket serverSocket = new ServerSocket(1234);
                serverSocket.close();
                Thread servidor = new Thread(new Servidor(1234));
                servidor.start();
            }
            catch(Exception e1){
                System.out.printf("------ ERROR!! NO SE PUDO ESTABLECER EL SERVIDOR -------");
            }
        }
    }

    public Servidor(int puerto) {
        this.puerto = puerto;
        if (puerto == 1235) puertoRedundancia = 1234;
        else puertoRedundancia = 1235;
    }

    public void run() {
        try {
            System.out.printf("Servidor corriendo en el PUERTO: " + puerto);
            ServerSocket serverSocket = new ServerSocket(puerto);
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
            System.out.printf("\n Se esta escuchando mensajes");
            while ((mensaje = (Mensaje) reader.readObject()) != null)
                procesarMensaje(conexion, mensaje);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.printf("Se cerro conexion");
        }
    }




    private void crearConexionRedundancia() {
        if (!hayRedundancia) {
            System.out.printf("ENTRO ACAtomas");
            try {
                Socket socket = new Socket("localhost", puertoRedundancia);
                Conexion conexionLocal = new Conexion();
                conexionLocal.setSocket(socket);
                conexionLocal.setOutput(new ObjectOutputStream(socket.getOutputStream()));
                conexionLocal.setInput(new ObjectInputStream(socket.getInputStream()));
                this.redundancia = conexionLocal;
                this.hayRedundancia = true;
                System.out.printf("Se creo conexion con el servidor secundario");
            }catch (IOException e){

                this.hayRedundancia = false;
                this.redundancia = null;
                e.printStackTrace();
                System.out.printf("\nERROR SE CAYO LA CONEXION DE LA REDUNDANCIA\n");
            }

        }
    }

    private void procesarMensaje(Conexion conexion, Mensaje mensaje) {
        String mensajeControl = mensaje.getMensajeControl();
        System.out.printf(clientes.toString());
        System.out.printf("SE RECIBIO MENSAJE");
        switch (mensajeControl) {
            case "REGISTRAR":
                System.out.printf("\n ------------------------ \n MENSAJE CONTROL: REGISTRAR");
                crearConexionRedundancia();
                procesarRegistro(conexion,mensaje);
                crearConexionRedundancia();
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
                mandarMensaje(puerto,mensaje.getPuertoDestino(), "ERROR CONEXION","",mensaje.getNombreUsuarioEmisor());
                break;
            case "SOLICITAR NOMBRE":
                System.out.printf("\n ------------------------ \n MENSAJE CONTROL: SOLICITAR NOMBRE");
                if (this.clientes.containsKey(mensaje.getPuertoDestino()))
                    mandarMensaje(puerto,mensaje.getPuertoOrigen(), "NOMBRE",this.clientes.get(mensaje.getPuertoDestino()).getNombreUsuario(),mensaje.getNombreUsuarioEmisor());
                else
                    mandarMensaje(puerto,mensaje.getPuertoOrigen(), "NOMBRE","",mensaje.getNombreUsuarioEmisor());
                break;
            case "LISTA USUARIOS":
                System.out.printf("LOS CLIENTES CONECTADOS SON "+ clientesConectados.toString());
                mandarMensaje(puerto, mensaje.getPuertoOrigen(), "LISTA USUARIOS",clientesConectados.toString(),"");
                break;
            case "CERRAR CONEXION":
                System.out.printf("\nLOS CLIENTES CONECTADOS SON "+ clientesConectados.toString());
                this.clientesConectados.remove(mensaje.getPuertoOrigen());
                System.out.printf("LOS CLIENTES CONECTADOS SON "+ clientesConectados.toString());
                break;
            case "RESINCRONIZACION":
                System.out.printf("\n ------------------------ \nRESINCRONIZACION");
                this.sesiones.put(mensaje.getPuertoOrigen(), mensaje.getPuertoDestino());
                this.sesiones.put(mensaje.getPuertoDestino(), mensaje.getPuertoOrigen());
                break;
        }
    }

    // Agrego la conexion al servidor
    private void procesarRegistro(Conexion conexion,Mensaje mensaje) {
        conexion.setNombreUsuario(mensaje.getMensaje());
        clientesConectados.put(mensaje.getPuertoOrigen(), mensaje.getMensaje());
        clientes.put(mensaje.getPuertoOrigen(), conexion);
        System.out.printf("Se realizo el registro");
        //mandarMensaje(mensaje.getPuertoOrigen(), mensaje.getPuertoDestino(), "ACEPTAR", "", mensaje.getNombreUsuarioEmisor());
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
            mandarMensaje(puerto,mensaje.getPuertoOrigen(), "ERROR CONEXION","", mensaje.getNombreUsuarioEmisor());
        }
    }

    private void procesarConexionAceptada(Mensaje mensaje) {
        System.out.printf("\n CONEXION ACEPTADA \nse mando conexion aceptadaa");
        mandarMensaje(puerto,mensaje.getPuertoDestino(), "CONEXION CORRECTA","", mensaje.getNombreUsuarioEmisor());
    }

    //Aviso al puerto que me aceptaron la sesion, creo sesiones y abro ventana sesion.
    private void procesarAceptacion(Mensaje mensaje) {
        this.sesiones.put(mensaje.getPuertoOrigen(), mensaje.getPuertoDestino());
        this.sesiones.put(mensaje.getPuertoDestino(), mensaje.getPuertoOrigen());
        System.out.printf("\n Puerto al que se quiere mandar mensaje" + mensaje.getPuertoDestino());
        if (hayRedundancia){
            Mensaje mensajeRed = new Mensaje(mensaje.getPuertoOrigen(), mensaje.getPuertoDestino(), "RESINCRONIZACION","","");
            this.redundancia.mandarMensaje(mensajeRed);
        }
        else{
            crearConexionRedundancia();
            Mensaje mensajeRed = new Mensaje(mensaje.getPuertoOrigen(), mensaje.getPuertoDestino(), "RESINCRONIZACION","","");
            if (this.redundancia != null) {
                this.redundancia.mandarMensaje(mensajeRed);
            }
        }
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
//        if (sesiones.containsKey(mensaje.getPuertoOrigen())) {
            int puertoDestino = sesiones.get(mensaje.getPuertoOrigen());
            System.out.printf("INTENTAMOS MANDAR MENSAJE A ");
            System.out.printf("PUERTO ORI" + mensaje.getPuertoOrigen() + "PUERTO DEST" + puertoDestino );
            clientes.get(puertoDestino).mandarMensaje(mensaje);
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
    private boolean existeCliente(int puertoDestino, int puertoOrigen) {
        if(this.clientes.containsKey(puertoDestino))
            return true;
        else
            return false;
    }

    private void procesarCierroVentana(Mensaje mensaje) {
        if (sesiones.containsKey(mensaje.getPuertoOrigen())) {
            int puertoDestino = sesiones.get(mensaje.getPuertoOrigen());
            Mensaje mensaje1 = new Mensaje(0,0,"CIERRO VENTANA SESION","",null);
            clientes.get(puertoDestino).mandarMensaje(mensaje1);
        }
    }

    private void procesarCierroVentanaLocal(Mensaje mensaje) {
        if (sesiones.containsKey(mensaje.getPuertoOrigen())) {
            int puertoDestino = sesiones.get(mensaje.getPuertoOrigen());
            Mensaje mensaje1 = new Mensaje(0,0,"CIERRO VENTANA SESION","",null);
            clientes.get(mensaje.getPuertoOrigen()).mandarMensaje(mensaje1);
        }
    }

    public void mandarMensaje(int puertoOrigen,int puertoDestino,String mensajeControl, String text, String nombreUsuarioEmisor) {
        Mensaje mensaje = new Mensaje(puertoOrigen,puertoDestino,mensajeControl,text,nombreUsuarioEmisor);
        System.out.printf("puerto destino" + puertoDestino);
        this.clientes.get(puertoDestino).mandarMensaje(mensaje);
    }
}