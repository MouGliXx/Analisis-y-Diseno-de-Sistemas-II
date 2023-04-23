package app;

import controlador.ControladorInicio;
import controlador.ControladorNotificacion;
import modelo.Sistema;
import vista.ventanas.VentanaInicio;
import vista.ventanas.VentanaNotificacion;

public class App {
    public static void main(String[] args) {
//        VentanaInicio ventanaInicio = new VentanaInicio();
//        Sistema sistema = new Sistema();
//        ControladorInicio controladorInicio = new ControladorInicio(ventanaInicio,sistema);
//        ventanaInicio.ejecutar();

        int tipo = 3;
        String nombreUsuarioEmisor = "Lautaro";

        VentanaNotificacion ventanaNotificacion = new VentanaNotificacion();
        ControladorNotificacion controladorNotificacion = new ControladorNotificacion(ventanaNotificacion);
        switch (tipo) {
            case 1 -> ventanaNotificacion.setTipoVentana(1, null); //tipo 1 -> Notificacion Error
            case 2 -> ventanaNotificacion.setTipoVentana(2, null); //tipo 2 -> Notificacion Espera
            case 3 -> ventanaNotificacion.setTipoVentana(3, nombreUsuarioEmisor); //tipo 3 -> Notificacion Solicitud
        }
        ventanaNotificacion.ejecutar();
    }
}
