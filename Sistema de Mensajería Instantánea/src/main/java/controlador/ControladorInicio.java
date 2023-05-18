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

    private void setNotificacion(int tipo) {
        String nombreUsuarioEmisor = null; //TODO poner el nombre de cliente del emisor que recibo del modelo

        this.notificacion = vista.lanzarNotificacion();
        this.notificacion.setActionListener(this);
        this.notificacion.setWindowListener(this);
        this.notificacion.setTipoVentana(tipo, nombreUsuarioEmisor);
        this.notificacion.ejecutar();
    }

    private void notificacionAceptada() {
        if (notificacion.getTipo() == 3) { //Si es de tipo solicitud -> creo ventanaMensajes
            try {
                System.out.printf("puerto que me invito a sesion" + getPuertoInvitoASesion());
                Sistema.getInstance().getCliente().aceptarConexion(getPuertoInvitoASesion());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            vista.creaVentanaMensajes("nombre usuario emisor"); //TODO poner el nombre de usuario del emisor que recibo del modelo
        }
        //Si es de tipo error -> no hago nada
        this.notificacion.cerrarVentana();
    }

    private void notificacionRechazada() {
        if (notificacion.getTipo() == 3) { //Si es de tipo solicitud -> informo al emisor
            System.out.print("Se rechazo la solicitud: "+ getPuertoInvitoASesion() + "\n");
            Sistema.getInstance().getCliente().rechazarConexion(getPuertoInvitoASesion());
        }
        this.notificacion.cerrarVentana();
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
            //NOTIFICACION ESPERA
            int puertoDestino = vista.getPuerto();

            Sistema.getInstance().getCliente().setNombreDeUsuario(vista.getNombreDeUsuario());
            Sistema.getInstance().getCliente().crearConexion(puertoDestino);

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
    public void notificarCambio(String estado, String mensaje) {
        //A esta funcion solo llego si soy el RECEPTOR y el EMISOR quiere conectarse conmigo
        System.out.printf("RECIBIO NOTIFICACION DE CAMBIO " + estado);
        if ("Rechazo invitacion sesion".equals(estado)){
            this.notificacion.cerrarVentana();
        }
        if ("Abro ventana notificacion".equals(estado)) {
            setNotificacion(1);
        }
        if ("Acepto conexion".equals(estado)){
            setNotificacion(3);
        }
        if ("Abro ventana sesion".equals(estado)){
            // TODO recibir nombre de usuario emisor , recien no se me cerro la notificacion rari.
            vista.creaVentanaMensajes("nombre usuario emisor");
            this.notificacion.cerrarVentana();
        }
        if ("ERROR CONEXION".equals(estado)){
            // TODO se puede poner notificacion uno
            vista.lanzarVentanaEmergente("El usuario no existe o no se encuentra en modo escucha");
        }
        if ("CONEXION CORRECTA".equals(estado)){
            // TODO la podemos cambiar por una ventana mas chota diciendo esperando
            setNotificacion(2);
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
