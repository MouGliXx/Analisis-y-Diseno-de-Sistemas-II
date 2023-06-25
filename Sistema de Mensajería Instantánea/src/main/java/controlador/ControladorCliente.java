package controlador;

import modelo.Sistema;
import modelo.interfaces.IObserver;
import vista.interfaces.IVistaInicio;
import vista.interfaces.IVistaNotificacion;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.UnknownHostException;

public class ControladorCliente implements ActionListener, WindowListener, IObserver {
    private final IVistaInicio vista;
    private IVistaNotificacion notificacion;
    private int puertoInvitoASesion;
    private String nombreUsuarioEmisor;

    public ControladorCliente(IVistaInicio vistaInicio) {
        this.vista = vistaInicio;

        vista.setActionListener(this);
        vista.setKeyListener();
        vista.setChangeListener();
        vista.setWindowListener(this);

        this.establecerIP();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Registrarse" -> registrarUsuario();
            case "Conectar" -> conectar();
            case "Modo Escucha" -> cambiarModoEscucha();
            case "Aceptar Notificacion" -> notificacionAceptada();
            case "Cancelar Notificacion" -> notificacionRechazada();
            case "Refrescar" -> actualizarUsuariosConectados();
        }
    }

    private void actualizarUsuariosConectados() {
        Sistema.getInstance().getCliente().listaUsuarios();
    }

    private void setNotificacion(int tipo, String nombreEmisor) {
        this.notificacion = this.vista.lanzarNotificacion();
        this.notificacion.setActionListener(this);
        this.notificacion.setWindowListener(this);
        this.notificacion.setTipoNotificacion(tipo, nombreEmisor);
        this.notificacion.ejecutar();
    }

    private void notificacionAceptada() {
        System.out.print("\nnotificacionAceptada\n");
        if (notificacion.getTipo() == 3) { //Si es de tipo solicitud -> creo ventanaMensajes
            try {
                Sistema.getInstance().getCliente().aceptarConexion(getPuertoInvitoASesion());
                Sistema.getInstance().getCliente().setEnSesion(true);
                vista.creaVentanaMensajes(nombreUsuarioEmisor);
            } catch (Exception ex) {
                vista.lanzarVentanaEmergente("ERROR: El servidor no se encuentra disponible en este momento");
            }
            this.notificacion.cerrarDialogo();
        } else {
            //Si es de tipo error -> no hago nada
            this.notificacion.cerrarDialogo();
            this.vista.mostrarVentana();
        }
    }

    private void notificacionRechazada() {
        System.out.print("\nnotificacionRechazada\n");
        if (notificacion.getTipo() == 3) { //Si es de tipo solicitud -> informo al emisor
            System.out.print("Se rechazo la solicitud: " + getPuertoInvitoASesion() + "\n");
            try {
                Sistema.getInstance().getCliente().rechazarConexion(getPuertoInvitoASesion());
            } catch (IOException e) {
                vista.lanzarVentanaEmergente("ERROR: El servidor no se encuentra disponible en este momento");
            }
        }
        this.notificacion.cerrarDialogo();
        this.vista.mostrarVentana();
    }

    private void registrarUsuario() {
        try {
            String nombreUsuario= vista.getNombreDeUsuario();
            Sistema.getInstance().getCliente().registrarServidor(nombreUsuario);
            this.vista.setModoConectar();
            this.vista.lanzarVentanaEmergente("El usuario se ha registrado en el servidor con exito!");
        } catch (Exception e){
            e.printStackTrace();
            this.vista.lanzarVentanaEmergente("ALERTA! No existe el servidor.");
        }
    }

    private void conectar() {
        System.out.print("\n[apreto CONECTAR] El nombre es: " + Sistema.getInstance().getCliente().getNombreDeUsuarioReceptor());
        Sistema.getInstance().getCliente().setNombreDeUsuario(vista.getNombreDeUsuario());
        try {
            Sistema.getInstance().getCliente().crearConexion(vista.getPuerto());
            Sistema.getInstance().getCliente().setearNombreReceptor(vista.getPuerto());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cambiarModoEscucha() {
        if (vista.getNombreDeUsuario().isEmpty()) {
            vista.lanzarVentanaEmergente("Para activar el modo escucha, es necesario que establezca su nombre de cliente primero.");
            vista.setModoEscucha(false);
        } else {
            Sistema.getInstance().getCliente().setModoEscucha(vista.getModoEscucha());
            Sistema.getInstance().getCliente().setNombreDeUsuario(vista.getNombreDeUsuario());
        }
    }

    private void establecerIP() {
        try {
            vista.setMiDireccionIP(Sistema.getInstance().obtenerIP());
            establecerPuerto();
        } catch (UnknownHostException e) {
            vista.setMiDireccionIP("XXX.XXX.X.X");
        }
    }

    private void establecerPuerto() {
        String puerto = String.valueOf(Sistema.getInstance().getCliente().getPuertoPropio());
        vista.setMiPuerto(puerto);
    }

    @Override
    public void notificarCambio(String estado, String mensaje, String nombreUsuarioEmisor) {
        //A esta funcion solo llego si soy el RECEPTOR y el EMISOR quiere conectarse conmigo
        System.out.print("\nRECIBIO NOTIFICACION DE CAMBIO: " + estado);
        switch (estado) {
            case "Rechazo invitacion sesion" -> {
                this.notificacion.cerrarDialogo();
                this.vista.mostrarVentana();
            }
            case "Abro ventana notificacion", "ERROR CONEXION" -> {
                setNotificacion(1,nombreUsuarioEmisor);
                this.vista.ocultarVentana();
            }
            case "CONEXION CORRECTA" -> {
                setNotificacion(2,nombreUsuarioEmisor);
                this.vista.ocultarVentana();
            }
            case "Abro ventana sesion" -> {
                Sistema.getInstance().getCliente().setEnSesion(true);
                System.out.print("" + "\nACEPTAR --- se cambio el enSesion");
                System.out.print("\n En sesion " + Sistema.getInstance().getCliente());
                this.vista.creaVentanaMensajes(nombreUsuarioEmisor);
                this.notificacion.cerrarDialogo();
            }
            case "CIERRO VENTANA SESION" -> {
                Sistema.getInstance().getCliente().setEnSesion(false);
                this.vista.mostrarVentana();
            }
            case "LISTA USUARIOS"-> this.vista.actualizarTablaUsuarios(mensaje);
            case "SERVIDOR OUT"-> this.vista.lanzarVentanaEmergente("SE CAYO EL SERVIDOR");
        }
    }

    @Override
    public void notificarCambio(String estado, int puerto, String nombreEmisor) {
        //A esta funcion solo llego si soy el RECEPTOR y el EMISOR quiere conectarse conmigo
        this.nombreUsuarioEmisor = nombreEmisor;
        setPuertoInvitoASesion(puerto);
        if ("Abro ventana notificacion".equals(estado)) {
            setNotificacion(3,nombreEmisor);
            this.vista.ocultarVentana();
        }
    }

    public int getPuertoInvitoASesion() {
        return puertoInvitoASesion;
    }

    public void setPuertoInvitoASesion(int puertoInvitoASesion) {
        this.puertoInvitoASesion = puertoInvitoASesion;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        try {
            Sistema.getInstance().getCliente().cerrarConexion();
        } catch (NullPointerException exception) {
            this.vista.cerrarVentana();
        }
    }

    //METODOS NO USADOS
    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
