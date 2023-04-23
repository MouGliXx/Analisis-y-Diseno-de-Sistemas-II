package controlador;

import vista.interfaces.IVistaNotificacion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ControladorNotificacion implements ActionListener, WindowListener {
    private IVistaNotificacion vista;

    public ControladorNotificacion(IVistaNotificacion vista) {
        this.vista = vista;
        this.vista.setActionListener(this);
        this.vista.setWindowListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Aceptar":
                break;
            case "Cancelar":
                this.vista.cerrarVentana();
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
