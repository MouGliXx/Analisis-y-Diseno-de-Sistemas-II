package controlador;

import modelo.Sistema;
import modelo.interfaces.IObserver;
import vista.interfaces.IVistaNotificacion;
import vista.ventanas.VentanaMensajes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

public class ControladorNotificacion implements ActionListener, WindowListener {
    private IVistaNotificacion vista;
    private Sistema sistema;

    public ControladorNotificacion(IVistaNotificacion vista, Sistema sistema) {
        this.vista = vista;
        this.sistema = sistema;
        this.vista.setActionListener(this);
        this.vista.setWindowListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Aceptar":
                //Creo sesion y creo ventana de sesion
                VentanaMensajes ventanaMensajes = new VentanaMensajes();
                ControladorMensajes controladorMensajes = new ControladorMensajes(ventanaMensajes,this.sistema);
                //TODO setear usuarios
                ArrayList<IObserver> observadores = new ArrayList<>(this.sistema.getCliente().getObservadores());
                observadores.add(controladorMensajes);
                this.sistema.getCliente().setObservadores(observadores);
                ventanaMensajes.ejecutar();
                this.vista.cerrarVentana();
                this.sistema.getCliente().setConnected(true);
                break;
            case "Cancelar":
                this.vista.cerrarVentana();
                this.sistema.getCliente().setRejected(true);
                break;
        }
    }

    //METODOS NO USADOS
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
}
