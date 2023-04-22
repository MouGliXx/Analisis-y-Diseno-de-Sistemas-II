package app;

import controlador.ControladorInicio;
import vista.ventanas.VentanaInicio;

public class App {
    public static void main(String[] args) {
        VentanaInicio ventanaInicio = new VentanaInicio();
        Sistema sistema = new Sistema();
        ControladorInicio controladorInicio = new ControladorInicio(ventanaInicio,sistema);
        ventanaInicio.ejecutar();
    }
}
