package controlador;

import modelo.Sistema;
import modelo.interfaces.IObserver;
import vista.interfaces.IVistaInicio;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;

public class ControladorInicio implements ActionListener, IObserver {
    private final IVistaInicio vista;
    private final Sistema sistema;

    public ControladorInicio(IVistaInicio vistaInicio, Sistema sistema) {
        this.vista = vistaInicio;
        this.sistema = sistema;
        vista.setActionListener(this);
        vista.setKeyListener();
        vista.setChangeListener();
        establecerIP();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Conectar" -> conectar();
            case "Modo Escucha" -> cambiarModoEscucha();
        }
    }

    private void conectar() {
        int puertoDestino = vista.getPuerto();
        sistema.getUsuario().setNombreDeUsuario(vista.getNombreDeUsuario());
        String usuario = vista.getNombreDeUsuario();
        sistema.getUsuario().setUsuario(usuario);
        System.out.println("\nIntentando conectarse con el puerto" + puertoDestino);
        sistema.getUsuario().crearConexionCliente(puertoDestino);
//        vista.cerrarVentana();
    }

    private void cambiarModoEscucha() {
        System.out.println("\nCambio el modo escucha");
        System.out.println("\nEl modo escucha es " + vista.getModoEscucha());
        if (vista.getNombreDeUsuario().isEmpty()) {
            vista.lanzarVentanaEmergente("Para activar el modo escucha, es necesario que establezca su nombre de usuario primero.");
            vista.setModoEscucha(false);
        } else {
            sistema.getUsuario().setModoEscucha(vista.getModoEscucha());
        }

    }

    private void establecerIP() {
        try {
            vista.setMiDireccionIP(sistema.obtenerIP());
        } catch (UnknownHostException e) {
            vista.setMiDireccionIP("XXX.XXX.X.X");
        }
    }

    @Override
    public void notificarCambio(String estado, String mensaje) {
        switch (estado) {
            case "Abro ventana sesion" -> {
                vista.creaOtraVentana(sistema, 0, vista.getNombreDeUsuario());
                vista.cerrarVentana();
            }
            case "Abro ventana notificacion" -> {
                vista.creaOtraVentana(sistema, 3, "Usuario emisor"); //TODO poner el nombre de usuario del emisor
//                vista.cerrarVentana();
            }
        }
    }
}
