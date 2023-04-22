package controlador;

import app.Cliente;
import app.Sistema;
import vista.interfaces.IVistaMensajes;
import vista.ventanas.VentanaMensajes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ControladorMensajes implements ActionListener, WindowListener {
    private IVistaMensajes vista;
    private Sistema sistema;

    public ControladorMensajes(IVistaMensajes vista) {
        this.vista = vista;
        this.vista.setActionListener(this);
        this.vista.setKeyListener();
        this.vista.setWindowListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evento) {
        switch (evento.getActionCommand()) {
            case "NUEVO_MENSAJE":
                break;
            case "CERRAR_SESION":
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
}