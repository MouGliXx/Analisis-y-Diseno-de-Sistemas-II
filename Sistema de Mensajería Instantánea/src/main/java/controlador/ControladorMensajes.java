package controlador;

import modelo.Cliente;
import modelo.Sistema;
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
        Cliente cliente = sistema.getCliente();
//        // Esto lo podemos cambiar mas adelante, esta medio pelo
//        if (cliente.isServer()) {
//            cliente.mandarMensajeComoServidor(mensaje);
//            this.vista.agregarNuevoEnviado(mensaje);
//        } else {
//            cliente.mandarMensajeComoCliente(mensaje);
//            this.vista.agregarNuevoEnviado(mensaje);
//        }
    }

    private void cerrarSesion() {
//        cliente cliente = sistema.getCliente();
//        if (cliente.isServer()) {
//            cliente.mandarMensajeComoServidor("Se cierra conexion y ventana");
//        } else {
//            cliente.mandarMensajeComoCliente("Se cierra conexion y ventana");
//        }
//        cliente.desconectar();
//        vista.creaOtraVentana(sistema);
//        this.sistema.getCliente().getObservadores().remove(this);
//        vista.cerrarVentana();
    }

    @Override
    public void notificarCambio(String estado, String mensaje) {
        switch (estado) {
            case STATE_RECIBIR_MENSAJE -> vista.agregarNuevoRecibido(mensaje);
            case STATE_CERRAR_SESION -> {
                this.sistema.getCliente().getObservadores().remove(this);
                vista.creaOtraVentana(sistema);
                vista.cerrarVentana();
            }
        }
    }
}