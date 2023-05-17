package app;

import controlador.ControladorInicio;
import modelo.Sistema;
import vista.ventanas.VentanaInicio;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        VentanaInicio ventanaInicio = new VentanaInicio();
        Sistema sistema = null;
        try {
            sistema = new Sistema();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ControladorInicio controladorInicio = new ControladorInicio(ventanaInicio,sistema);
        sistema.getCliente().agregarObservador(controladorInicio);
        ventanaInicio.ejecutar();
//        Thread.sleep(1000);
//        Cliente cliente = new Cliente(1111);
//        Cliente cliente2 = new Cliente(2222);
//        Thread.sleep(1000);
//        cliente.registroServidor();
//        cliente2.registroServidor();
//        Thread.sleep(1000);
//        cliente.registrar();
//        cliente2.registrar();
//        Thread.sleep(1);
//        cliente.aceptarConexion(2222);
//        Thread.sleep(1000);
//        cliente.mandarTexto(2222,"hola como andas");
//        Thread.sleep(1000);
//        cliente2.mandarTexto(1111,"todo bien amigo y vos?");
//        cliente2.mandarTexto(1111,"todo bien amigo y vos?");
    }
}
