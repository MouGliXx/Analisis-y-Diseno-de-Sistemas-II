package vista.ventanas;

import controlador.ControladorNotificacion;
import vista.interfaces.IVistaInicio;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;

public class VentanaInicio extends JFrame implements IVistaInicio, ActionListener, KeyListener, ChangeListener {
    private JPanel PanelPrincipal;
    private JPanel PanelCentral;
    private JButton conectarButton;
    private JLabel IconoMensaje;
    private JLabel MiDireccionLabel;
    private JTextField NombreDeUsuarioJTextField;
    private JTextField IpJTextField;
    private JLabel NombreDeUsuarioLabel;
    private JLabel IPLabel;
    private JLabel PuertoLabel;
    private JRadioButton modoEscuchaRadioButton;
    private JLabel Info1Label;
    private JLabel Info2Label;
    private JSpinner PuertoSpinner;

    @Override
    public void setActionListener(ActionListener controlador) {
        this.modoEscuchaRadioButton.addActionListener(controlador);
        this.modoEscuchaRadioButton.addActionListener(this);
        this.conectarButton.addActionListener(controlador);
    }

    @Override
    public void setKeyListener() {
        this.NombreDeUsuarioJTextField.addKeyListener(this);
        this.IpJTextField.addKeyListener(this);
    }

    @Override
    public void setChangeListener() {
        this.PuertoSpinner.addChangeListener(this);
    }

    @Override
    public void setWindowListener(WindowListener controlador) {

    }

    @Override
    public void ejecutar() {
        setTitle("Sistema de Mensajeria Instantaneo");
        pack(); //Coloca los componentes
        setContentPane(PanelPrincipal);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setSize(600,800); //Dimensiones del JFrame
        setResizable(false); //No redimensionable
        setLocationRelativeTo(null);
        conectarButton.setEnabled(false);
        PuertoSpinner.setValue(1);
    }

    @Override
    public void cerrarVentana() {
        setVisible(false); //Oculto la ventana
        dispose(); //Cierro la ventana
    }

    @Override
    public void creaOtraVentana(int tipo, String nombreUsuarioEmisor) {
        VentanaNotificacion ventanaNotificacion = new VentanaNotificacion();
        ControladorNotificacion controladorNotificacion = new ControladorNotificacion(ventanaNotificacion);
        switch (tipo) {
            case 1 -> ventanaNotificacion.setTipoVentana(1, null); //tipo 1 -> Notificacion Error
            case 2 -> ventanaNotificacion.setTipoVentana(2, null); //tipo 2 -> Notificacion Espera
            case 3 -> ventanaNotificacion.setTipoVentana(3, nombreUsuarioEmisor); //tipo 3 -> Notificacion Solicitud
        }
        ventanaNotificacion.ejecutar();
    }

    @Override
    public void lanzarVentanaEmergente(String mensaje) {
        JFrame jFrame = new JFrame();
        JOptionPane.showMessageDialog(jFrame, mensaje);
    }

    @Override
    public void setMiDireccionIP(String IP) {
        this.MiDireccionLabel.setText("Mi direccion IP: " + IP);
    }

    @Override
    public String getNombreDeUsuario() {
        return this.NombreDeUsuarioJTextField.getText();
    }

    @Override
    public String getDireccionIP() {
        return this.IpJTextField.getText();
    }

    @Override
    public boolean getModoEscucha() {
        return this.modoEscuchaRadioButton.isSelected();
    }

    @Override
    public int getPuerto() {
        return Integer.parseInt(this.PuertoSpinner.getValue().toString());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Modo Escucha")) {
            if (modoEscuchaRadioButton.isSelected())
                modoEscuchaRadioButton.setText("Modo Escucha: ON");
            else
                modoEscuchaRadioButton.setText("Modo Escucha: OFF");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        boolean conexion = !NombreDeUsuarioJTextField.getText().isEmpty() && !IpJTextField.getText().isEmpty();
        conectarButton.setEnabled(conexion);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        int currentValue = (int) PuertoSpinner.getValue();
        if (currentValue <= 1) {
            PuertoSpinner.setValue(1);
        }
        if (currentValue > 65535) {
            PuertoSpinner.setValue(65535);
        }
    }

    //METODOS NO USADOS
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }
}