package app;

import controlador.ControladorInicio;
import vista.ventanas.VentanaInicio;

public class App {
    public static void main(String[] args) {
        VentanaInicio ventanaInicio = new VentanaInicio();
        ControladorInicio controladorInicio = new ControladorInicio(ventanaInicio);
        ventanaInicio.ejecutar();
    }
}
