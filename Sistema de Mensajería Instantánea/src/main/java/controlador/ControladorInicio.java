package controlador;

import modelo.Sistema;
import modelo.interfaces.IObserver;
import vista.interfaces.IVistaInicio;
import vista.ventanas.VentanaMensajes;
import vista.ventanas.VentanaNotificacion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

public class ControladorInicio implements ActionListener, WindowListener, IObserver {
    private final IVistaInicio vistaInicio;
    private final Sistema sistema;
    private static final String CONECTAR = "Conectar";

    public ControladorInicio(IVistaInicio vistaInicio, Sistema sistema) {
        this.vistaInicio = vistaInicio;
        this.sistema = sistema;
        configurarVista();
    }

    private void configurarVista() {
        vistaInicio.setActionListener(this);
        vistaInicio.setKeyListener();
        vistaInicio.setChangeListener();
        vistaInicio.setWindowListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case CONECTAR:
                conectar();
                break;
            case "Modo Escucha":
                cambiarModoEscucha();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + e.getActionCommand());
        }
    }

    private void conectar() {
        int puertoDestino = vistaInicio.getPuerto();
        String ipDestino = "localhost";
        String usuario = vistaInicio.getNombreDeUsuario();
        sistema.getCliente().setUsuario(usuario);
        System.out.println("Intentando conectarse con el puerto" + puertoDestino);
        sistema.getCliente().crearConexionCliente(puertoDestino);
    }

    private void cambiarModoEscucha() {
        System.out.println("Cambio el modo escucha");
        System.out.println("El modo escucha es " + vistaInicio.getModoEscucha());
        sistema.getCliente().setModoEscucha(vistaInicio.getModoEscucha());
    }

    @Override
    public void notificarCambio(String estado, String mensaje) {
        switch (estado) {
            case "Abro ventana sesion":{
                VentanaMensajes ventanaMensajes = new VentanaMensajes();
                ControladorMensajes controladorMensajes = new ControladorMensajes(ventanaMensajes,this.sistema);
                ArrayList<IObserver> observadores = new ArrayList<>(this.sistema.getCliente().getObservadores());
                observadores.add(controladorMensajes);
                this.sistema.getCliente().setObservadores(observadores);
                ventanaMensajes.ejecutar();
                break;
            }
            case "Abro ventana notificacion":{
                this.creaOtraVentana(3, "Usuario emisor");
                break;
            }
        }
    }

    public void creaOtraVentana(int tipo, String nombreUsuarioEmisor) {
        VentanaNotificacion ventanaNotificacion = new VentanaNotificacion();
        ControladorNotificacion controladorNotificacion = new ControladorNotificacion(ventanaNotificacion,this.sistema);
        switch (tipo) {
            case 1 -> ventanaNotificacion.setTipoVentana(1, null); //tipo 1 -> Notificacion Error
            case 2 -> ventanaNotificacion.setTipoVentana(2, null); //tipo 2 -> Notificacion Espera
            case 3 -> ventanaNotificacion.setTipoVentana(3, nombreUsuarioEmisor); //tipo 3 -> Notificacion Solicitud
        }
        ventanaNotificacion.ejecutar();
    }

    //METODOS NO USADOS
    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {

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
