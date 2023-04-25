package controlador;

import modelo.Sistema;
import modelo.interfaces.IObserver;
import vista.interfaces.IVistaMensajes;
import vista.ventanas.VentanaMensajes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ControladorMensajes implements ActionListener, WindowListener, IObserver {
    private static final String ACTION_MANDAR_MENSAJE = "Enviar Mensaje";
    private static final String ACTION_CERRAR_SESION = "Cerrar Sesion";
    private static final String STATE_RECIBIR_MENSAJE = "Recibo mensaje";
    private static final String STATE_ABRO_VENTANA = "Abro ventana";
    private static final String STATE_CERRAR_SESION = "Cierro ventana sesion";

    private IVistaMensajes vista;
    private Sistema sistema;

    public ControladorMensajes(IVistaMensajes vista, Sistema sistema) {
        this.vista = vista;
        this.sistema = sistema;
        this.vista.setActionListener(this);
        this.vista.setKeyListener();
        this.vista.setWindowListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evento) {
        String actionCommand = evento.getActionCommand();
        switch (actionCommand) {
            case ACTION_MANDAR_MENSAJE:
                enviarMensaje();
                break;
            case ACTION_CERRAR_SESION:
                cerrarSesion();
                break;
        }
    }

    private void enviarMensaje() {
        String mensaje = vista.getMensajeEnviado();
        System.out.printf("EL MENSAJE ENVIADO ES" + mensaje);
        // Esto lo podemos cambiar mas adelante , esta medio pelo
        if (this.sistema.getCliente().isServer()) {
            System.out.printf("se mando como servidor");
            this.sistema.getCliente().mandarMensajeComoServidor(mensaje);
            this.vista.agregarNuevoEnviado(mensaje);
        }
        else {
            this.sistema.getCliente().mandarMensajeComoCliente(mensaje);
            this.vista.agregarNuevoEnviado(mensaje);
        }
    }

    private void cerrarSesion() {
        vista.creaOtraVentana();
        vista.cerrarVentana();
        if (this.sistema.getCliente().isServer()) {
            System.out.printf("Por cerrar conexion papi");
            this.sistema.getCliente().mandarMensajeComoServidor("Se cierra conexion y ventana");
        } else {
            System.out.printf("Intentando cerrar conexion again");
            this.sistema.getCliente().mandarMensajeComoCliente("Se cierra conexion y ventana");
        }
        this.sistema.getCliente().desconectar();
    }

    @Override
    public void notificarCambio(String estado, String mensaje) {
        switch (estado) {
            case STATE_RECIBIR_MENSAJE:
                vista.agregarNuevoRecibido(mensaje);
                break;
            case STATE_CERRAR_SESION:
                vista.cerrarVentana();
                break;
        }
    }

    public void reciboMensaje(String mensaje){

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