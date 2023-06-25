package gestordeMensajes;

import conexion.Conexion;
import modelo.Cifrado;
import modelo.Mensaje;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class GestordeMensajes implements IGestordeMensajes {
    private HashMap<Integer, Conexion> clientes = new HashMap<>();
    private HashMap<Integer,Integer> sesiones = new HashMap<>();
    private HashMap<Integer, String> clientesConectados = new HashMap<>();
    private int puerto;
    private int puertoRedundancia;
    private Conexion redundancia;
    private boolean hayRedundancia = false;
    private int servidores[] = {1235,1234};
    private Cifrado cifrado;

    public GestordeMensajes(HashMap<Integer, Conexion> clientes, HashMap<Integer, Integer> sesiones, HashMap<Integer, String> clientesConectados, int puerto, int puertoRedundancia, Conexion redundancia, boolean hayRedundancia, int[] servidores, Cifrado cifrado) {
        this.clientes = clientes;
        this.sesiones = sesiones;
        this.clientesConectados = clientesConectados;
        this.puerto = puerto;
        this.puertoRedundancia = puertoRedundancia;
        this.redundancia = redundancia;
        this.hayRedundancia = hayRedundancia;
        this.servidores = servidores;
        this.cifrado = cifrado;
    }

    @Override
    public void crearConexion(Mensaje mensaje) {
    }

    @Override
    public void procesarMensaje(Conexion conexion, Mensaje mensaje) {
        String mensajeControl = mensaje.getMensajeControl();
        System.out.printf(clientes.toString());
        System.out.printf("SE RECIBIO MENSAJE");
        switch (mensajeControl) {
            case "REGISTRAR" -> {
                System.out.printf("\n ------------------------ \n MENSAJE CONTROL: REGISTRAR");
                procesarRegistro(conexion, mensaje);
                //crearConexionRedundancia();
            }
            case "NUEVA CONEXION" -> {
                System.out.printf("\n ------------------------ \n MENSAJE CONTROL: CONECTAR\n");
                procesarConexion(mensaje);
            }
            case "CONEXION CORRECTA" -> procesarConexionAceptada(mensaje);
            case "ACEPTAR" -> {
                System.out.printf("\n ------------------------ \n MENSAJE CONTROL: ACEPTAR");
                crearConexionRedundancia();
                procesarAceptacion(mensaje);
            }
            case "RECHAZAR" -> {
                System.out.printf("\n ------------------------ \n MENSAJE CONTROL: RECHAZAR");
                procesarRechazo(conexion, mensaje);
            }
            case "TEXTO" -> {
                System.out.printf("\n ------------------------ \n MENSAJE CONTROL: TEXTO");
                procesarTexto(conexion, mensaje);
            }
            case "DESCONECTAR" -> procesarDesconexion(mensaje);
            case "CIERRO VENTANA SESION" -> {
                System.out.printf("\n ------------------------ \n MENSAJE CONTROL: CIERRO VENTANA SESION");
                procesarCierroVentana(mensaje);
            }
            case "CIERRO VENTANA SESION LOCAL" -> {
                System.out.printf("\n ------------------------ \n MENSAJE CONTROL: CIERRO VENTANA SESION");
                procesarCierroVentanaLocal(mensaje);
            }
            case "ERROR CONEXION" -> {
                System.out.printf("\n ------------------------ \n MENSAJE CONTROL: ERRO CONEXION");
                mandarMensaje(puerto, mensaje.getPuertoDestino(), "ERROR CONEXION", "", mensaje.getNombreUsuarioEmisor());
            }
            case "SOLICITAR NOMBRE" -> {
                System.out.printf("\n ------------------------ \n MENSAJE CONTROL: SOLICITAR NOMBRE");
                if (this.clientes.containsKey(mensaje.getPuertoDestino()))
                    mandarMensaje(puerto, mensaje.getPuertoOrigen(), "NOMBRE", this.clientes.get(mensaje.getPuertoDestino()).getNombreUsuario(), mensaje.getNombreUsuarioEmisor());
                else
                    mandarMensaje(puerto, mensaje.getPuertoOrigen(), "NOMBRE", "", mensaje.getNombreUsuarioEmisor());
            }
            case "LISTA USUARIOS" -> {
                System.out.printf("LOS CLIENTES CONECTADOS SON " + clientesConectados.toString());
                mandarMensaje(puerto, mensaje.getPuertoOrigen(), "LISTA USUARIOS", clientesConectados.toString(), "");
            }
            case "CERRAR CONEXION" -> {
                System.out.printf("\nLOS CLIENTES CONECTADOS SON " + clientesConectados.toString());
                this.clientesConectados.remove(mensaje.getPuertoOrigen());
                System.out.printf("LOS CLIENTES CONECTADOS SON " + clientesConectados.toString());
            }
            case "RESINCRONIZACION" ->{
                System.out.printf("Se hizo la resincronizacion ----------------");
                this.sesiones.put(mensaje.getPuertoOrigen(), mensaje.getPuertoDestino());
                this.sesiones.put(mensaje.getPuertoDestino(), mensaje.getPuertoOrigen());
            }
        }
    }

    public void cerrarSesion(){}

    public void setModoEscucha(){}

    public void mandarMensajeEmisor(){}

    private void procesarRegistro(Conexion conexion,Mensaje mensaje) {
        conexion.setNombreUsuario(mensaje.getMensaje());
        clientesConectados.put(mensaje.getPuertoOrigen(), mensaje.getMensaje());
        clientes.put(mensaje.getPuertoOrigen(), conexion);
//        if (redundancia != null)
//            this.redundancia.mandarMensaje(mensaje);
        System.out.printf("Se realizo el registro");
        //mandarMensaje(mensaje.getPuertoOrigen(), mensaje.getPuertoDestino(), "ACEPTAR", "", mensaje.getNombreUsuarioEmisor());
    }

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
            Mensaje mensajeRed = new Mensaje(mensaje.getPuertoOrigen(), mensaje.getPuertoDestino(), "RESINCRONIZACION","", mensaje.getNombreUsuarioEmisor());
            this.redundancia.mandarMensaje(mensajeRed);
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

    private void crearConexionRedundancia() {
        if (!hayRedundancia) {
            try {
                //TODO verificar como funciona con el puerto redundancia.
                Socket socket = new Socket("localhost", puertoRedundancia);
                System.out.printf("Se creo conexion con el servidor secundario");
                Conexion conexionLocal = new Conexion();
                conexionLocal.setSocket(socket);
                conexionLocal.setOutput(new ObjectOutputStream(socket.getOutputStream()));
                conexionLocal.setInput(new ObjectInputStream(socket.getInputStream()));
                this.redundancia = conexionLocal;
                this.hayRedundancia = true;
            }catch (IOException e){
                System.out.printf("Error");
            }

        }
    }





}
