package controlador;

import app.Sistema;
import interfaces.IObserver;
import vista.interfaces.IVistaInicio;
import vista.interfaces.IVistaMensajes;
import vista.ventanas.VentanaInicio;
import vista.ventanas.VentanaMensajes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ControladorInicio implements ActionListener, WindowListener, IObserver {
    private IVistaInicio vista;
    private Sistema sistema;

    public ControladorInicio(IVistaInicio vista, Sistema sistema) {
        this.vista = vista;
        this.sistema = sistema;
        this.vista.setActionListener(this);
        this.vista.setKeyListener();
        this.vista.setWindowListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "CONECTAR":
                Integer puertoDestino = Integer.parseInt(this.vista.getPuerto());
                String ipDestino = "localhost";
                String usuario = vista.getNombreDeUsuario();
                this.sistema.getCliente().setUsuario(usuario);
                System.out.printf("\nIntentando conectarse con el puerto" + puertoDestino);
                sistema.getCliente().crearConexionCliente(puertoDestino);
                System.out.printf("ventaaa");
                this.notificarCambio();
                break;
            case "MODO_ESCUCHA":
                System.out.printf("\nCambio el modo escucha");
                System.out.printf("\nEl modo escucha es " + vista.getModoEscucha());
                sistema.getCliente().setModoEscucha(vista.getModoEscucha());
                break;

        }
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    @Override
    public void notificarCambio() {
        System.out.printf("intentando abrir ventana");
        VentanaMensajes ventanaMensajes =  new VentanaMensajes();
        ventanaMensajes.ejecutar();
    }
}
