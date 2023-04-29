package controlador;

import modelo.Sistema;
import modelo.Usuario;
import modelo.interfaces.IObserver;
import vista.interfaces.IVistaMensajes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorMensajes implements ActionListener, IObserver {
    private static final String STATE_RECIBIR_MENSAJE = "Recibo mensaje";
    private static final String STATE_CERRAR_SESION = "Cierro ventana sesion";
    private final IVistaMensajes vista;
    private final Sistema sistema;

    public ControladorMensajes(IVistaMensajes vista, Sistema sistema) {
        this.vista = vista;
        this.sistema = sistema;
        this.vista.setActionListener(this);
        this.vista.setKeyListener();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Enviar Mensaje" -> enviarMensaje();
            case "Cerrar Sesion" -> cerrarSesion();
        }
    }

    private void enviarMensaje() {
        String mensaje = vista.getMensajeEnviado();
        Usuario usuario = sistema.getUsuario();
        System.out.printf("\nEL MENSAJE ENVIADO ES" + mensaje);
        // Esto lo podemos cambiar mas adelante, esta medio pelo
        if (usuario.isServer()) {
            System.out.printf("\nse mando como servidor");
            usuario.mandarMensajeComoServidor(mensaje);
            this.vista.agregarNuevoEnviado(mensaje);
        } else {
            usuario.mandarMensajeComoCliente(mensaje);
            this.vista.agregarNuevoEnviado(mensaje);
        }
    }

    private void cerrarSesion() {
        Usuario usuario = sistema.getUsuario();
        if (usuario.isServer()) {
            System.out.printf("\nPor cerrar conexion papi");
            usuario.mandarMensajeComoServidor("Se cierra conexion y ventana");
        } else {
            System.out.printf("\nIntentando cerrar conexion again");
            usuario.mandarMensajeComoCliente("Se cierra conexion y ventana");
        }
        usuario.desconectar();
        vista.creaOtraVentana(sistema);
        vista.cerrarVentana();
    }

    @Override
    public void notificarCambio(String estado, String mensaje) {
        System.out.printf("\nSe notifico de un cambio en ventana mensajes");
        switch (estado) {
            case STATE_RECIBIR_MENSAJE -> vista.agregarNuevoRecibido(mensaje);
            case STATE_CERRAR_SESION -> {
                vista.creaOtraVentana(sistema);
                vista.cerrarVentana();
            }
        }
    }
}