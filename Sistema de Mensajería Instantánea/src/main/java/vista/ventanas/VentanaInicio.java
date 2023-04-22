package vista.ventanas;

import vista.interfaces.IVistaInicio;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowListener;

public class VentanaInicio extends JFrame implements IVistaInicio, KeyListener {
    private JPanel PanelPrincipal;
    private JPanel PanelCentral;
    private JButton conectarButton;

    private JLabel IconoMensaje;
    private JLabel MiDireccionLabel;
    private JTextField NombreDeUsuarioJTextField;
    private JTextField IpJTextField;
    private JTextField PuertoJTextField;
    private JLabel NombreDeUsuarioLabel;
    private JLabel IPLabel;
    private JLabel PuertoLabel;
    private JRadioButton modoEscuchaRadioButton;
    private JLabel Info1Label;
    private JLabel Info2Label;

    @Override
    public void setActionListener(ActionListener controlador) {
        this.modoEscuchaRadioButton.addActionListener(controlador);
        this.conectarButton.addActionListener(controlador);
    }

    @Override
    public void setKeyListener() {
        NombreDeUsuarioJTextField.addKeyListener(this);
        IpJTextField.addKeyListener(this);
        PuertoJTextField.addKeyListener(this);
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
        conectarButton.setActionCommand("CONECTAR");
        modoEscuchaRadioButton.setActionCommand("MODO_ESCUCHA");
    }


    @Override
    public void cerrarVentana() {
        setVisible(false); //Oculto la ventana
        dispose(); //Cierro la ventana
    }

    @Override
    public void creaOtraVentana(String ventana) {

    }

    @Override
    public void lanzarVentanaEmergente(String mensaje) {
        JFrame jFrame = new JFrame();
        JOptionPane.showMessageDialog(jFrame, mensaje);
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
    public boolean getModoEscucha() { return this.modoEscuchaRadioButton.isSelected();}

    @Override
    public void setMiDireccionIP(String IP) {
        this.MiDireccionLabel.setText("Mi direccion IP: " + IP);
    }

    @Override
    public String getPuerto() {
        return this.PuertoJTextField.getText();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        boolean conexion = !NombreDeUsuarioJTextField.getText().isEmpty() && !IpJTextField.getText().isEmpty() && !PuertoJTextField.getText().isEmpty();
        conectarButton.setEnabled(conexion);
    }

    //METODOS NO USADOS
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }
}