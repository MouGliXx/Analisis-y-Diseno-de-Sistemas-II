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

public class ControladorInicio implements ActionListener, WindowListener, IObserver {
    private final IVistaInicio vista;
    private IVistaNotificacion notificacion;
    private int puertoInvitoASesion;

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

    public int getPuertoInvitoASesion() {
        return puertoInvitoASesion;
    }

    public void setPuertoInvitoASesion(int puertoInvitoASesion) {
        this.puertoInvitoASesion = puertoInvitoASesion;
    }

    private void setNotificacion(int tipo) {
        String nombreUsuarioEmisor = null; //TODO poner el nombre de cliente del emisor que recibo del modelo

        this.notificacion = vista.lanzarNotificacion();
        this.notificacion.setActionListener(this);
        this.notificacion.setWindowListener(this);
        this.notificacion.setTipoNotificacion(tipo, nombreUsuarioEmisor);
        this.notificacion.ejecutar();
    }

    private void notificacionAceptada() {
        if (notificacion.getTipo() == 3) { //Si es de tipo solicitud -> creo ventanaMensajes
            try {
                Sistema.getInstance().getCliente().aceptarConexion(getPuertoInvitoASesion());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            vista.creaVentanaMensajes("nombre usuario emisor"); //TODO poner el nombre de usuario del emisor que recibo del modelo
            this.vista.ocultarVentana();
        }
        //Si es de tipo error -> no hago nada
        this.notificacion.cerrarDialogo();
    }

    private void notificacionRechazada() {
        //TODO revisar esto con lauta
        if (notificacion.getTipo() == 3) { //Si es de tipo solicitud -> informo al emisor
            System.out.print("Se rechazo la solicitud: " + getPuertoInvitoASesion() + "\n");
            Sistema.getInstance().getCliente().rechazarConexion(getPuertoInvitoASesion());
        }
        this.notificacion.cerrarDialogo();
    }

    private void registrarUsuario() {
        try {
            Sistema.getInstance().getCliente().registrarServidor();
            this.vista.setModoConectar();
            this.vista.lanzarVentanaEmergente("El usuario se ha registrado en el servidor con exito!");
        }
        catch (Exception e){
            this.vista.lanzarVentanaEmergente("ALERTA: No existe servidor.");
        }
    }

    private void conectar() {
        try {
            //NOTIFICACION ESPERA
            int puertoDestino = vista.getPuerto();

            Sistema.getInstance().getCliente().setNombreDeUsuario(vista.getNombreDeUsuario());
            Sistema.getInstance().getCliente().crearConexion(puertoDestino);

            setNotificacion(2);
        } catch (IOException e) {
            //NOTIFICAION ERROR
            //TODO contemplar el caso en donde no exista el usuario que quiero contactar --> Notificacion error
            setNotificacion(1);
        }
    }

    private void cambiarModoEscucha() {
        Sistema.getInstance().getCliente().setModoEscucha(vista.getModoEscucha());
        Sistema.getInstance().getCliente().setNombreDeUsuario(vista.getNombreDeUsuario());
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
    public void notificarCambio(String estado, String mensaje) {
        //A esta funcion solo llego si soy el RECEPTOR y el EMISOR quiere conectarse conmigo
        System.out.print("RECIBIO NOTIFICACION DE CAMBIO" + estado);

        switch (estado) {
            case "Rechazo invitacion sesion" -> this.notificacion.cerrarDialogo();
            case "Abro ventana notificacion" -> setNotificacion(1);
            case "Acepto conexion" -> setNotificacion(3);
            case "Abro ventana sesion" -> {
                // TODO recibir nombre de usuario emisor , recien no se me cerro la notificacion rari.
                this.vista.creaVentanaMensajes("nombre usuario emisor");
                this.notificacion.cerrarDialogo();
                this.vista.ocultarVentana();
            }
            case "Cierro ventana sesion" -> {
                System.out.print("\n[VENTANA INICIO] ME ENTERO QUE SE CERRO LA SESION\n");
                this.vista.mostrarVentana();
            }
            // TODO caso que se rechace excepcion
        }

    }

    @Override
    public void notificarCambio(String estado, int puerto) {
        //A esta funcion solo llego si soy el RECEPTOR y el EMISOR quiere conectarse conmigo
        System.out.print("ENTRO A NOTIFICAR CAMBIO [CONTROLADOR INICIO]");

        setPuertoInvitoASesion(puerto);
        if ("Abro ventana notificacion".equals(estado)) {
            setNotificacion(3);
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {
        //TODO se cierra la app --> deberia eliminar al cliente registrado del servidor
    }

    @Override
    public void windowClosed(WindowEvent e) {
        notificacionRechazada();
    }

    //METODOS NO USADOS
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
