package controlador;

import modelo.Sistema;
import modelo.interfaces.IObserver;
import vista.interfaces.IVistaMensajes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorMensajes implements ActionListener, IObserver {
    private static final String ACTION_MANDAR_MENSAJE = "Enviar Mensaje";
    private static final String ACTION_CERRAR_SESION = "Cerrar Sesion";
    private static final String STATE_RECIBIR_MENSAJE = "Recibo mensaje";
    private static final String STATE_CERRAR_SESION = "Cierro ventana sesion";

    private final IVistaMensajes vista;

    public ControladorMensajes(IVistaMensajes vista) {
        this.vista = vista;
        this.vista.setActionListener(this);
        this.vista.setKeyListener();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case ACTION_MANDAR_MENSAJE -> enviarMensaje();
            case ACTION_CERRAR_SESION -> cerrarSesion();
        }
    }

    private void enviarMensaje() {
        String mensaje = vista.getMensajeEnviado();
        System.out.printf("\nEL MENSAJE ENVIADO ES" + mensaje);
        // Esto lo podemos cambiar mas adelante , esta medio pelo
        if (Sistema.getInstance().getUsuario().isServer()) {
            System.out.printf("\nse mando como servidor");
            Sistema.getInstance().getUsuario().mandarMensajeComoServidor(mensaje);
            this.vista.agregarNuevoEnviado(mensaje);
        } else {
            Sistema.getInstance().getUsuario().mandarMensajeComoCliente(mensaje);
            this.vista.agregarNuevoEnviado(mensaje);
        }
    }

    private void cerrarSesion() {
        vista.creaOtraVentana();
        vista.cerrarVentana();
        if (Sistema.getInstance().getUsuario().isServer()) {
            System.out.printf("\nPor cerrar conexion papi");
            Sistema.getInstance().getUsuario().mandarMensajeComoServidor("Se cierra conexion y ventana");
        } else {
            System.out.printf("\nIntentando cerrar conexion again");
            Sistema.getInstance().getUsuario().mandarMensajeComoCliente("Se cierra conexion y ventana");
        }
        Sistema.getInstance().getUsuario().desconectar();
    }

    @Override
    public void notificarCambio(String estado, String mensaje) {
        System.out.printf("\nSe notifico de un cambio en ventana mensajes");
        switch (estado) {
            case STATE_RECIBIR_MENSAJE -> vista.agregarNuevoRecibido(mensaje);
            case STATE_CERRAR_SESION -> {
                vista.creaOtraVentana();
                vista.cerrarVentana();
            }
        }
    }
}