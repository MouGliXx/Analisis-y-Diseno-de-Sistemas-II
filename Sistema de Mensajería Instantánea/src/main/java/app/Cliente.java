package app;

import controlador.ControladorCliente;
import modelo.Sistema;
import vista.ventanas.VentanaInicio;

public class Cliente {
    public static void main(String[] args) {
        VentanaInicio ventanaInicio = new VentanaInicio();
        ControladorCliente controladorInicio = new ControladorCliente(ventanaInicio);
        Sistema.getInstance().getCliente().agregarObservador(controladorInicio);
        ventanaInicio.ejecutar();
    }
}