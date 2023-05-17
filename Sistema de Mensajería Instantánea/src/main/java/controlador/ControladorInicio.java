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
            case "Ventana Emergente"-> ventanaEmergente();
        }
    }

    private void registrarUsuario() {
        //TODO registrar usuario dentro del servidor
        System.out.printf("me registro");
        this.vista.setModoConectar();
        this.vista.lanzarVentanaEmergente("El usuario se ha registrado en el servidor con exito!");
    }

    private void conectar() {
        try {
            //TODO contemplar el caso en donde no exista el usuario que quiero contactar --> Notificacion error
            int puertoDestino = vista.getPuerto();
            Sistema.getInstance().getCliente().setNombreDeUsuario(vista.getNombreDeUsuario());
            String cliente = vista.getNombreDeUsuario();
            Sistema.getInstance().getCliente().setNombreDeUsuario(cliente);
            Sistema.getInstance().getCliente().crearConexion(puertoDestino);
            vista.creaOtraVentana(Sistema.getInstance(), 2, null);
            Sistema.getInstance().getInstance().getCliente().getObservadores().remove(this);
            vista.cerrarVentana();
            // Si la conexion falla que tire una excepcion
        } catch (IOException e) {
            vista.creaOtraVentana(Sistema.getInstance(), 1, null);
            Sistema.getInstance().getInstance().getCliente().getObservadores().remove(this);
            vista.cerrarVentana();
        }
    }

    private void cambiarModoEscucha() {
        if (vista.getNombreDeUsuario().isEmpty()) {
            vista.lanzarVentanaEmergente("Para activar el modo escucha, es necesario que establezca su nombre de cliente primero.");
            vista.setModoEscucha(false);
        } else {
            Sistema.getInstance().getCliente().setModoEscucha(vista.getModoEscucha());
            if(Sistema.getInstance().getCliente().modoEscucha)
                Sistema.getInstance().getCliente().setModoEscucha(false);
            else
                Sistema.getInstance().getCliente().setModoEscucha(true);
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
//            vista.lanzarVentanaEmergente("El cliente con el que se intenta conectar no se encuentra en modo escucha");
            vista.creaOtraVentana(Sistema.getInstance(),1,null);
            vista.cerrarVentana();
        }
        if ("Abro ventana notificacion".equals(estado)) {
            vista.creaOtraVentana(Sistema.getInstance(), 3, "cliente emisor"); //TODO poner el nombre de cliente del emisor que recibo del modelo
            Sistema.getInstance().getCliente().getObservadores().remove(this);
            vista.cerrarVentana();
        }
        if ("Acepto conexion".equals(estado)){
            vista.creaOtraVentana(Sistema.getInstance(), 2, null);
            Sistema.getInstance().getCliente().getObservadores().remove(this);
            vista.cerrarVentana();
        }
    }
    @Override
    public void notificarCambio(String estado, int puerto) {
        System.out.printf("ENTRO ACAAAAAA2");
        //A esta funcion solo llego si soy el RECEPTOR y el EMISOR quiere conectarse conmigo
        if ("Abro ventana notificacion".equals(estado)) {
            vista.creaOtraVentana(Sistema.getInstance(), 3,  String.valueOf(puerto)); //TODO poner el nombre de cliente del emisor que recibo del modelo
            Sistema.getInstance().getCliente().getObservadores().remove(this);
            vista.cerrarVentana();
        }
    }
}
