package controlador;

import modelo.Sistema;
import modelo.interfaces.IObserver;
import vista.interfaces.IVistaInicio;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;

public class ControladorInicio implements ActionListener, IObserver {
    private final IVistaInicio vista;

    public ControladorInicio(IVistaInicio vistaInicio) {
        this.vista = vistaInicio;
        this.vista.setActionListener(this);
        this.vista.setKeyListener();
        this.vista.setChangeListener();
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
        Sistema.getInstance().getUsuario().setNombreDeUsuario(vista.getNombreDeUsuario());
        String usuario = vista.getNombreDeUsuario();
        Sistema.getInstance().getUsuario().setUsuario(usuario);
        System.out.println("Intentando conectarse con el puerto" + puertoDestino);
        Sistema.getInstance().getUsuario().crearConexionCliente(puertoDestino);
//        vista.cerrarVentana();
    }

    private void cambiarModoEscucha() {
        System.out.println("Cambio el modo escucha");
        System.out.println("El modo escucha es " + vista.getModoEscucha());
        Sistema.getInstance().getUsuario().setModoEscucha(vista.getModoEscucha());
    }

    private void establecerIP() {
        try {
            vista.setMiDireccionIP(Sistema.getInstance().obtenerIP());
        } catch (UnknownHostException e) {
            vista.setMiDireccionIP("XXX.XXX.X.X");
        }
    }

    @Override
    public void notificarCambio(String estado, String mensaje) {
        switch (estado) {
            case "Abro ventana sesion" -> {
                vista.creaOtraVentana(0, vista.getNombreDeUsuario());
                vista.cerrarVentana();
            }
            case "Abro ventana notificacion" -> {
                vista.creaOtraVentana(3, "Usuario emisor"); //TODO poner el nombre de usuario del emisor
                vista.cerrarVentana();
            }
        }
    }
}
