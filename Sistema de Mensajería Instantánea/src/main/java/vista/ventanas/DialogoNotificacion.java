package vista.ventanas;

import vista.interfaces.IVistaNotificacion;
import javax.swing.*;
import java.awt.event.*;
import java.util.Objects;

public class DialogoNotificacion extends JDialog implements IVistaNotificacion {
    private int tipo; // 1=error | 2=espera | 3=solicitud
    private JPanel panelPrincipal;
    private JPanel PanelCentral;
    private JPanel ButtonPanel;
    private JLabel TituloLabel;
    private JLabel Contenido1Label;
    private JLabel Contenido2Label;
    private JButton aceptarButton;
    private JButton cancelarButton;

    @Override
    public void ejecutar() {
        setTitle("Sistema de Mensajeria Instantaneo");
        setContentPane(panelPrincipal);
        setSize(600,300);
//        setModal(true);
        setModal(false);
        setVisible(true);
        setResizable(false); //No redimensionable
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        getRootPane().setDefaultButton(aceptarButton);
        setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getResource("/Icono.png"))).getImage());
        pack();
        toFront();
    }

    @Override
    public void setActionListener(ActionListener controlador) {
        aceptarButton.addActionListener(controlador);
        cancelarButton.addActionListener(controlador);
    }

    @Override
    public void setWindowListener(WindowListener controlador) {
        this.addWindowListener(controlador);
    }

    @Override
    public void cerrarDialogo() {
        dispose(); // Cierra el JDialog
    }

    public int getTipo() {
        return tipo;
    }

    @Override
    public void setTipoNotificacion(int tipo, String nombreUsuarioEmisor) {
        this.tipo = tipo;
        switch (tipo) {
            case 1 -> { //tipo 1 -> Notificacion Error
                this.TituloLabel.setText("Error");
                this.Contenido1Label.setText("No ha sido posible conectarse con el cliente ingresado.");
                this.Contenido2Label.setText("Es posible que este no se encuentra disponible.");
                this.aceptarButton.setVisible(true);
                this.cancelarButton.setVisible(false);
            }
            case 2 -> { //tipo 2 -> Notificacion Espera
                this.TituloLabel.setText("Espere...");
                this.Contenido1Label.setText("La solicitud ha sido enviada correctamente. Aguarde a ");
                this.Contenido2Label.setText("que el cliente ingresado responda la misma.");
                this.aceptarButton.setVisible(false);
                this.cancelarButton.setVisible(true);
            }
            case 3 -> { //tipo 3 -> Notificacion Solicitud
                this.TituloLabel.setText("Atencion!");
                this.Contenido1Label.setText("El cliente '" + nombreUsuarioEmisor + "' quiere unirse a una ");
                this.Contenido2Label.setText("sesión con usted.");
                this.aceptarButton.setVisible(true);
                this.cancelarButton.setVisible(true);
            }
        }
    }
}
