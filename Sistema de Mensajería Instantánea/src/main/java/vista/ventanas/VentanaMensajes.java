package vista.ventanas;

import vista.interfaces.IVistaMensajes;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.Objects;

public class VentanaMensajes extends JFrame implements IVistaMensajes, KeyListener {
    private JPanel PanelPrincipal;
    private JPanel PanelInferior;
    private JPanel PanelSuperior;
    private JButton EnviarButton;
    private JLabel NombreUsuarioEmisorLabel;
    private JTextField EscribirMensajeJTextField;
    private JButton CerrarSesionButton;
    private JLabel NombreUsuarioReceptorLabel;
    private JLabel PersonaLabel;
    private JScrollPane ScrollPanelCentral;
    private JTable TablaMensajes;
    //ATRIBUTOS TABLA
    private final String[] columnNames = {"Mensajes Recibidos", "Mensajes Enviados"};
    private final DefaultTableModel modeloTabla = new DefaultTableModel(columnNames, 0);

    @Override
    public void setActionListener(ActionListener controlador) {
        this.EnviarButton.addActionListener(controlador);
        this.CerrarSesionButton.addActionListener(controlador);
    }

    @Override
    public void setKeyListener() {
        this.EscribirMensajeJTextField.addKeyListener(this);
    }

    @Override
    public void ejecutar() {
        setTitle("Sistema de Mensajeria Instantaneo");
        pack(); //Coloca los componentes
        setContentPane(PanelPrincipal);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setSize(1280,720); //Dimensiones del JFrame
        setResizable(false); //No redimensionable
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getResource("/Icono.png"))).getImage());
        setModelos();
    }

    @Override
    public void cerrarVentana() {
        dispose(); //Cierro la ventana
    }

    @Override
    public void lanzarVentanaEmergente(String mensaje) {
        JFrame jFrameVacio = new JFrame();
        JOptionPane.showMessageDialog(jFrameVacio, mensaje);
    }

    @Override
    public void setModelos() {
        this.TablaMensajes.setModel(modeloTabla);
    }

    @Override
    public void setUsuarios(String UsuarioEmisor, String UsuarioReceptor) {
        NombreUsuarioEmisorLabel.setText(UsuarioEmisor);
        NombreUsuarioReceptorLabel.setText(UsuarioReceptor);
    }

    @Override
    public void agregarNuevoRecibido(String mensaje) {
        Object[] newRow = {mensaje, ""};
        modeloTabla.addRow(newRow);
    }

    @Override
    public void agregarNuevoEnviado(String mensaje) {
        Object[] newRow = {"", mensaje};
        modeloTabla.addRow(newRow);
    }

    @Override
    public String getMensajeEnviado() {
        String mensaje = EscribirMensajeJTextField.getText();
        EscribirMensajeJTextField.setText("");
        return mensaje;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        boolean existeMensaje = !EscribirMensajeJTextField.getText().isEmpty();
        EnviarButton.setEnabled(existeMensaje);
    }

    //METODOS NO USADOOS
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }
}
