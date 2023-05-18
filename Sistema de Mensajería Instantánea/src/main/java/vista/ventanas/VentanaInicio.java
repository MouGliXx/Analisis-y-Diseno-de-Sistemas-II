package vista.ventanas;

import controlador.ControladorMensajes;
import modelo.Cliente;
import modelo.Sistema;
import modelo.interfaces.IObserver;
import vista.interfaces.IVistaInicio;
import vista.interfaces.IVistaNotificacion;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.util.ArrayList;

public class VentanaInicio extends JFrame implements IVistaInicio, ActionListener, KeyListener, ChangeListener {
    private JPanel PanelPrincipal;
    private JPanel PanelCentral;
    private JButton registrarseConectarButton;
    private JLabel IconoMensaje;
    private JLabel MiDireccionLabel;
    private JTextField NombreDeUsuarioJTextField;
    private JTextField IpJTextField;
    private JLabel NombreDeUsuarioLabel;
    private JLabel IPLabel;
    private JLabel PuertoLabel;
    private JCheckBox modoEscuchaCheckBox;
    private JLabel Info1Label;
    private JLabel Info2Label;
    private JSpinner PuertoSpinner;
    private JLabel MiPuertoLabel;

    @Override
    public void setActionListener(ActionListener controlador) {
        this.modoEscuchaCheckBox.addActionListener(controlador);
        this.modoEscuchaCheckBox.addActionListener(this);
        this.registrarseConectarButton.addActionListener(controlador);
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
    public void ejecutar() {
        setTitle("Sistema de Mensajeria Instantaneo");
        pack(); //Coloca los componentes
        setContentPane(PanelPrincipal);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setSize(600,800); //Dimensiones del JFrame
        setResizable(false); //No redimensionable
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon(getClass().getResource("/Icono.png")).getImage());
        registrarseConectarButton.setEnabled(false);
        IpJTextField.setText("localhost");
    }

    @Override
    public void cerrarVentana() {
        setVisible(false); //Oculto la ventana
        dispose(); //Cierro la ventana
    }

    @Override
    public void lanzarVentanaEmergente(String mensaje) {
        JFrame jFrameVacio = new JFrame();
        JOptionPane.showMessageDialog(jFrameVacio, mensaje);
    }

    @Override
    public IVistaNotificacion lanzarNotificacion() { //tipo 1 -> Notificacion Error
        DialogoNotificacion dialogoNotificacion = new DialogoNotificacion(this);
        return dialogoNotificacion;
    }

    @Override
    public void creaVentanaMensajes(String nombreUsuarioEmisor) { //TODO crear ventana mensajes
        Cliente cliente = Sistema.getInstance().getCliente();
        VentanaMensajes ventanaMensajes = new VentanaMensajes();
        ControladorMensajes controladorMensajes = new ControladorMensajes(ventanaMensajes);
        ArrayList<IObserver> observadores = new ArrayList<>(cliente.getObservadores());
        observadores.add(controladorMensajes);
        cliente.setObservadores(observadores);
        cliente.setConnected(true);
        ventanaMensajes.setUsuarios(cliente.getNombreDeUsuario(), "nombre del receptor"); //TODO poner nombre de cliente receptor y hacer que se configure el nombre de cliente en la notificacion
        ventanaMensajes.ejecutar();

//        ControladorNotificacion controladorNotificacion;
//        VentanaNotificacion ventanaNotificacion = new VentanaNotificacion();
//        if (nombreUsuarioEmisor == null){
//           controladorNotificacion = new ControladorNotificacion(ventanaNotificacion);
//        } else {
//            controladorNotificacion = new ControladorNotificacion(ventanaNotificacion, Integer.parseInt(nombreUsuarioEmisor));
//        }
//        ArrayList<IObserver> observadores = new ArrayList<>(Sistema.getInstance().getCliente().getObservadores());
//        observadores.add(controladorNotificacion);
//        Sistema.getInstance().getCliente().setObservadores(observadores);
//        switch (tipo) {
//            case 1 -> ventanaNotificacion.setTipoVentana(1, null); //tipo 1 -> Notificacion Error
//            case 2 -> ventanaNotificacion.setTipoVentana(2, null); //tipo 2 -> Notificacion Espera
//            case 3 -> ventanaNotificacion.setTipoVentana(3, nombreUsuarioEmisor); //tipo 3 -> Notificacion Solicitud
//        }
//        ventanaNotificacion.setVisible(true);
//        ventanaNotificacion.ejecutar();
    }

    @Override
    public void setMiDireccionIP(String IP) {
        this.MiDireccionLabel.setText("Mi direccion IP: " + IP);
    }

    @Override
    public String getMiDireccionIP() {
        return this.MiDireccionLabel.getText();
    }

    @Override
    public void setMiPuerto(String puerto) {
        this.MiPuertoLabel.setText("Mi puerto: " + puerto);
    }

    @Override
    public void setModoConectar() {
        IpJTextField.setEnabled(true);
        PuertoSpinner.setEnabled(true);
        modoEscuchaCheckBox.setEnabled(true);

        NombreDeUsuarioJTextField.setFocusable(false);
        registrarseConectarButton.setEnabled(false);
        registrarseConectarButton.setActionCommand("Conectar");
        registrarseConectarButton.setText("Conectar");

        Info1Label.setText("Para chatear con otro usuario ingrese");
        Info2Label.setText("su IP y puerto para continuar:");
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
    public void setModoEscucha(Boolean activado) {
        this.modoEscuchaCheckBox.setSelected(activado);
        if (activado)
            modoEscuchaCheckBox.setText("Modo Escucha: ON");
        else
            modoEscuchaCheckBox.setText("Modo Escucha: OFF");
    }

    @Override
    public boolean getModoEscucha() {
        return this.modoEscuchaCheckBox.isSelected();
    }

    @Override
    public int getPuerto() {
        return Integer.parseInt(this.PuertoSpinner.getValue().toString());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Modo Escucha")) {
            if (modoEscuchaCheckBox.isSelected())
                modoEscuchaCheckBox.setText("Modo Escucha: ON");
            else
                modoEscuchaCheckBox.setText("Modo Escucha: OFF");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int currentValue = (int) PuertoSpinner.getValue();
        boolean conexion;

        if (currentValue == 0) {
            conexion = registrarseConectarButton.getText().equals("Registrarse") && !NombreDeUsuarioJTextField.getText().isEmpty();
        } else {
            conexion = !NombreDeUsuarioJTextField.getText().isEmpty() && !IpJTextField.getText().isEmpty();
        }

        registrarseConectarButton.setEnabled(conexion);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        int currentValue = (int) PuertoSpinner.getValue();

        if (currentValue == 0) {
            registrarseConectarButton.setEnabled(false);
        } else {
            registrarseConectarButton.setEnabled(true);
            if (currentValue <= 1) {
                PuertoSpinner.setValue(1);
            }
            if (currentValue > 65535) {
                PuertoSpinner.setValue(65535);
            }
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