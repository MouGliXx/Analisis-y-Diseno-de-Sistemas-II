package app;

import controlador.ControladorCliente;
import modelo.Sistema;
import servidor.Servidor;
import vista.ventanas.VentanaInicio;
import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        VentanaInicio ventanaInicio = new VentanaInicio();
        ControladorCliente controladorInicio = new ControladorCliente(ventanaInicio);
        Sistema.getInstance().getCliente().agregarObservador(controladorInicio);
        ventanaInicio.ejecutar();


//        Thread servidor = new Thread(new Servidor());
//        servidor.start();
    }
}