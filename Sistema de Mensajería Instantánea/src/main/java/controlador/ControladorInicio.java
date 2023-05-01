package controlador;

import modelo.Sistema;
import modelo.interfaces.IObserver;
import vista.interfaces.IVistaInicio;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
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
        establecerPuerto();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Conectar" -> conectar();
            case "Modo Escucha" -> cambiarModoEscucha();
            case "Ventana Emergente"-> ventanaEmergente();
        }
    }

    private void conectar() {
        try {
            int puertoDestino = vista.getPuerto();
            System.out.printf("El puerto destino es" + puertoDestino);
            sistema.getUsuario().setNombreDeUsuario(vista.getNombreDeUsuario());
            String usuario = vista.getNombreDeUsuario();
            sistema.getUsuario().setUsuario(usuario);
            System.out.println("\nIntentando conectarse con el puerto" + puertoDestino);
            sistema.getUsuario().crearConexionCliente(puertoDestino);
        } catch (IOException e) {
            vista.creaOtraVentana(sistema, 1, null);
            this.sistema.getUsuario().getObservadores().remove(this);
            vista.cerrarVentana();
        }
    }

    private void conexionCorrecta(){

    }

    private void conexionFallida(){

    }

    private void cambiarModoEscucha() {
        System.out.println("\nEl modo escucha es " + vista.getModoEscucha());
        if (vista.getNombreDeUsuario().isEmpty()) {
            vista.lanzarVentanaEmergente("Para activar el modo escucha, es necesario que establezca su nombre de usuario primero.");
            vista.setModoEscucha(false);
        } else {
            sistema.getUsuario().setModoEscucha(vista.getModoEscucha());
            sistema.getUsuario().setNombreDeUsuario(vista.getNombreDeUsuario());
        }
    }

    private void establecerIP() {
        try {
            vista.setMiDireccionIP(sistema.obtenerIP());
        } catch (UnknownHostException e) {
            vista.setMiDireccionIP("XXX.XXX.X.X");
        }
    }

    private void establecerPuerto() {
        String puerto = String.valueOf(sistema.getUsuario().getPuertoPropio());
        if (puerto != null) {
            vista.setMiPuerto(puerto);
        } else {
            vista.setMiPuerto("XXXXX");
        }
    }

    public void ventanaEmergente(){
        vista.lanzarVentanaEmergente("a");
    }

    @Override
    public void notificarCambio(String estado, String mensaje) {
        //A esta funcion solo llego si soy el RECEPTOR y el EMISOR quiere conectarse conmigo
        if ("Ventana Emergente".equals(estado)){
//            vista.lanzarVentanaEmergente("El usuario con el que se intenta conectar no se encuentra en modo escucha");
            vista.creaOtraVentana(sistema,1,null);
            vista.cerrarVentana();
        }
        if ("Abro ventana notificacion".equals(estado)) {
            System.out.printf("a ver cuantas veces se imprime esta shit");
            vista.creaOtraVentana(sistema, 3, "Usuario emisor"); //TODO poner el nombre de usuario del emisor que recibo del modelo
            this.sistema.getUsuario().getObservadores().remove(this);
            vista.cerrarVentana();
        }
        if ("Acepto conexion".equals(estado)){
            System.out.printf("se acepto la conexion");
            vista.creaOtraVentana(sistema, 2, null);
            this.sistema.getUsuario().getObservadores().remove(this);
            vista.cerrarVentana();
        }
    }
}
