package controlador;

import modelo.Cliente;
import modelo.Sistema;
import modelo.interfaces.IObserver;
import vista.interfaces.IVistaMensajes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorMensajes implements ActionListener, IObserver {
    private final IVistaMensajes vista;

    public ControladorMensajes(IVistaMensajes vista) {
        this.vista = vista;
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
        Cliente cliente = Sistema.getInstance().getCliente();
        String mensaje = vista.getMensajeEnviado();
        cliente.mandarTexto(mensaje);
        vista.agregarNuevoEnviado(mensaje);
    }

    private void cerrarSesion() {
        Sistema.getInstance().getCliente().cerrarConexion("");
        Sistema.getInstance().getCliente().getObservadores().remove(this);
//        vista.creaVentanaInicio(); //TODO no deberia crearla, sino volver a mostrarla
        vista.cerrarVentana();
    }

    @Override
    public void notificarCambio(String estado, String mensaje) {
        switch (estado) {
            case "Recibo mensaje" -> vista.agregarNuevoRecibido(mensaje);
            case "Cierro ventana sesion" -> {
                Sistema.getInstance().getCliente().getObservadores().remove(this);
//                vista.creaVentanaInicio(); //TODO no deberia crearla, sino volver a mostrarla
                vista.cerrarVentana();
            }
        }
    }

    @Override
    public void notificarCambio(String estado, int puerto, String nombreEmisor) {}
}