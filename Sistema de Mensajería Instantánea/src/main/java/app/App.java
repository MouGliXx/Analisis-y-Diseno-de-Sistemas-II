package app;

import controlador.ControladorInicio;
import controlador.ControladorMensajes;
import controlador.ControladorNotificacion;
import modelo.Sistema;
import vista.ventanas.VentanaInicio;
import vista.ventanas.VentanaMensajes;
import vista.ventanas.VentanaNotificacion;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        /**
         * PRUEBA VENTANA INICIO
         * **/
        VentanaInicio ventanaInicio = new VentanaInicio();
        Sistema sistema = new Sistema();
        ControladorInicio controladorInicio = new ControladorInicio(ventanaInicio,sistema);
        sistema.getCliente().agregarObservador(controladorInicio);
        sistema.getCliente().setListenerServidor();
        ventanaInicio.ejecutar();

        /**
         * PRUEBA VENTANA NOTIFICACION
         * **/
//        int tipo = 3;
//        String nombreUsuarioEmisor = "Lautaro";
//
//        VentanaNotificacion ventanaNotificacion = new VentanaNotificacion();
//        ControladorNotificacion controladorNotificacion = new ControladorNotificacion(ventanaNotificacion);
//        switch (tipo) {
//            case 1 -> ventanaNotificacion.setTipoVentana(1, null); //tipo 1 -> Notificacion Error
//            case 2 -> ventanaNotificacion.setTipoVentana(2, null); //tipo 2 -> Notificacion Espera
//            case 3 -> ventanaNotificacion.setTipoVentana(3, nombreUsuarioEmisor); //tipo 3 -> Notificacion Solicitud
//        }
        //TODO setear tipo de notificacion
//        ventanaNotificacion.ejecutar();

        /**
         * PRUEBA VENTANA MENSAJES
         * **/
//        VentanaMensajes ventanaMensajes = new VentanaMensajes();
//        ControladorMensajes controladorMensajes = new ControladorMensajes(ventanaMensajes);
//        //TODO setear usuarios
//        ventanaMensajes.ejecutar();
//        ventanaMensajes.agregarNuevoRecibido("Holaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//        ventanaMensajes.agregarNuevoRecibido("Como estas?");
//        ventanaMensajes.agregarNuevoEnviado("Hola, todo bien!");
//        ventanaMensajes.agregarNuevoEnviado("Vos?");
    }
}
