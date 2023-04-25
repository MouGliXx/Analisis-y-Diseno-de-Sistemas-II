package controlador;

import modelo.Sistema;
import vista.interfaces.IVistaNotificacion;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorNotificacion implements ActionListener {
    private IVistaNotificacion vista;

    public ControladorNotificacion(IVistaNotificacion vista) {
        this.vista = vista;
        this.vista.setActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Aceptar" -> {
                vista.creaOtraVentana(0);
                vista.cerrarVentana();
            }
            case "Cancelar" -> {
                Sistema.getInstance().getUsuario().setRejected(true);
                vista.creaOtraVentana(1);
                vista.cerrarVentana();
            }
        }
    }
}
