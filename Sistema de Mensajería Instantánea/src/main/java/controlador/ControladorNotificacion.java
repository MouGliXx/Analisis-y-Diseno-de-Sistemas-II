package controlador;

import modelo.Sistema;
import vista.interfaces.IVistaNotificacion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorNotificacion implements ActionListener {
    private final IVistaNotificacion vista;
    private final Sistema sistema;

    public ControladorNotificacion(IVistaNotificacion vista, Sistema sistema) {
        this.vista = vista;
        this.sistema = sistema;
        this.vista.setActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Aceptar" -> {
                vista.creaOtraVentana(sistema, 0);
                vista.cerrarVentana();
            }
            case "Cancelar" -> {
                sistema.getUsuario().setRejected(true);
                vista.creaOtraVentana(sistema, 1);
                vista.cerrarVentana();
            }
        }
    }

}
