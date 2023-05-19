package app;

import controlador.ControladorInicio;
import modelo.Sistema;
import vista.ventanas.VentanaInicio;
import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        VentanaInicio ventanaInicio = new VentanaInicio();
        ControladorInicio controladorInicio = new ControladorInicio(ventanaInicio);
        Sistema.getInstance().getCliente().agregarObservador(controladorInicio);
        ventanaInicio.ejecutar();
    }
}
