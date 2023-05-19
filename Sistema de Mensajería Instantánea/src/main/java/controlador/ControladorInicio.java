package controlador;

import modelo.Sistema;
import modelo.interfaces.IObserver;
import vista.interfaces.IVistaInicio;
import vista.interfaces.IVistaNotificacion;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.UnknownHostException;

public class ControladorInicio implements ActionListener, WindowListener, IObserver {
    private final IVistaInicio vista;
    private IVistaNotificacion notificacion;
    private int puertoInvitoASesion;
    private String nombreEmisor;

    public ControladorInicio(IVistaInicio vistaInicio) {
        this.vista = vistaInicio;

        vista.setActionListener(this);
        vista.setKeyListener();
        vista.setChangeListener();

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
        }
    }

    private void setNotificacion(int tipo, String nombreEmisor) {
        this.notificacion = this.vista.lanzarNotificacion();
        this.notificacion.setActionListener(this);
        this.notificacion.setWindowListener(this);
        this.notificacion.setTipoNotificacion(tipo, nombreEmisor);
        this.notificacion.ejecutar();
    }

    private void notificacionAceptada() {
        if (notificacion.getTipo() == 3) { //Si es de tipo solicitud -> creo ventanaMensajes
            try {
                Sistema.getInstance().getCliente().aceptarConexion(getPuertoInvitoASesion());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            System.out.printf("\nEl nombre es: " +Sistema.getInstance().getCliente().getNombreDeUsuarioReceptor());
//            vista.creaVentanaMensajes(Sistema.getInstance().getCliente().getNombreDeUsuarioReceptor()); //TODO poner el nombre de usuario del emisor que recibo del modelo
            vista.creaVentanaMensajes(nombreEmisor);
            this.notificacion.cerrarDialogo();
        } else {
            //Si es de tipo error -> no hago nada
            this.notificacion.cerrarDialogo();
            this.vista.mostrarVentana();
        }
    }

    private void notificacionRechazada() {
        if (notificacion.getTipo() == 3) { //Si es de tipo solicitud -> informo al emisor
            System.out.print("Se rechazo la solicitud: "+ getPuertoInvitoASesion() + "\n");
            Sistema.getInstance().getCliente().rechazarConexion(getPuertoInvitoASesion());
        }
        this.vista.mostrarVentana();
        this.notificacion.cerrarDialogo();
    }

    private void registrarUsuario() {
        try {
            String nombreUsuario= vista.getNombreDeUsuario();
            Sistema.getInstance().getCliente().registrarServidor(nombreUsuario);
            this.vista.setModoConectar();
            this.vista.lanzarVentanaEmergente("El usuario se ha registrado en el servidor con exito!");
        }
        catch (Exception e){
            this.vista.lanzarVentanaEmergente("ALERTA: No existe servidor.");
        }
    }

    private void conectar() {
            //NOTIFICACION ESPERA
            int puertoDestino = vista.getPuerto();

            Sistema.getInstance().getCliente().setNombreDeUsuario(vista.getNombreDeUsuario());
            Sistema.getInstance().getCliente().crearConexion(puertoDestino);
            System.out.printf("\nINTENTAMOS CONECTARNOS");
            Sistema.getInstance().getCliente().setearNombreReceptor(puertoDestino);
            System.out.printf("\nEl nombre es: " + Sistema.getInstance().getCliente().getNombreDeUsuarioReceptor());
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
        System.out.printf("\nRECIBIO NOTIFICACION DE CAMBIO: " + estado);

        switch (estado) {
            case "Rechazo invitacion sesion" -> {
                System.out.printf("\nRechazo invitacion sesion\n");
                this.notificacion.cerrarDialogo();
                this.vista.mostrarVentana();
            }
            case "Abro ventana notificacion" -> {
                System.out.printf("\n\nHOLAAAAAAAAAAAAAAAAAAAAAAAA Abro ventana notificacion? \n\n");
                this.vista.ocultarVentana();
                setNotificacion(1,nombreUsuarioEmisor);
            }
            case "CONEXION CORRECTA" -> {
                System.out.printf("\n\nHOLAAAAAAAAAAAAAAAAAAAAAAAA CONEXION CORRECTA? \n\n");
                setNotificacion(2,nombreUsuarioEmisor);
                this.vista.ocultarVentana();
            }
            case "Abro ventana sesion" -> {
                this.vista.creaVentanaMensajes(nombreUsuarioEmisor);
                this.notificacion.cerrarDialogo();
            }
            case "CIERRO VENTANA SESION" -> {
                this.vista.mostrarVentana();
            }
        }
    }

    @Override
    public void notificarCambio(String estado, int puerto, String nombreEmisor) {
        //A esta funcion solo llego si soy el RECEPTOR y el EMISOR quiere conectarse conmigo
        setPuertoInvitoASesion(puerto);
        System.out.println("ENTRE Y EL nombre emisor es: " + nombreEmisor);
        this.nombreEmisor = nombreEmisor;
        if ("Abro ventana notificacion".equals(estado)) {
            this.vista.ocultarVentana();
            setNotificacion(3, nombreEmisor);
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
        notificacionRechazada();
    }

    //METODOS NO USADOS
    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {

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
