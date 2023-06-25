package modelo.heartbeat.vista;

import javax.swing.*;
import java.util.Objects;

public class VentanaHeartBeat extends JFrame{
    private JPanel panelPrincipal;
    private JPanel panelSuperior;
    private JPanel panelCentral;
    private JLabel monitorLabel;
    private JScrollPane scrollPane1;
    private JScrollPane scrollPane2;
    private JList list1;
    private JList list2;
    //ATRIBUTOS LISTA
    DefaultListModel<String> modeloLista1 = new DefaultListModel<>();
    DefaultListModel<String> modeloLista2 = new DefaultListModel<>();

    public void ejecutar() {
        setTitle("Sistema de Mensajeria Instantaneo");
        pack(); //Coloca los componentes
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setSize(800,800); //Dimensiones del JFrame
        setResizable(false); //No redimensionable
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getResource("/Icono.png"))).getImage());
        setModelos();
    }

    public void setModelos() {
        this.list1.setModel(modeloLista1);
        this.list2.setModel(modeloLista2);
    }

    public void agregarEnLista1(String mensaje) {
        modeloLista1.addElement(mensaje);
    }

    public void agregarEnLista2(String mensaje) {
        modeloLista2.addElement(mensaje);
    }
}
