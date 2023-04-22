package vista.ventanas;

import vista.interfaces.IVistaMensajes;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;

public class VentanaMensajes implements IVistaMensajes {
    private JPanel PanelPrincipal;
    private JPanel PanelInferior;
    private JPanel PanelSuperior;
    private JButton EnviarButton;
    private JLabel NombreUsuarioEmisorLabel;
    private JTextField EscribirMensajeJTextField;
    private JButton button1;
    private JLabel NombreUsuarioReceptorLabel;
    private JLabel PersonaLabel;
    private JScrollPane ScrollPanelCentral;
    private JTable table1;

    @Override
    public void setActionListener(ActionListener controlador) {

    }

    @Override
    public void setWindowListener(WindowListener controlador) {

    }

    @Override
    public void ejecutar() {

    }

    @Override
    public void cerrarVentana() {

    }

    @Override
    public void creaOtraVentana(String ventana) {

    }

    @Override
    public void lanzarVentanaEmergente(String mensaje) {

    }

    @Override
    public void setKeyListener() {

    }
}
