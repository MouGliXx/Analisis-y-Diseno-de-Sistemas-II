package modelo.heartbeat.vista;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

public class VentanaHeartBeat extends JFrame {
    private JPanel contentPane;
    private JPanel panelCentral;
    private JScrollPane scrollPane;
    public JTextPane txtPane;
    private JLabel lblTitulo;

    public VentanaHeartBeat(String nombre) {
        setTitle(nombre);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 741, 423);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        panelCentral = new JPanel();
        panelCentral.setBounds(10, 41, 705, 332);
        contentPane.add(panelCentral);
        panelCentral.setLayout(new BorderLayout(0, 0));

        scrollPane = new JScrollPane();
        panelCentral.add(scrollPane);

        txtPane = new JTextPane();
        txtPane.setEditable(false);
        scrollPane.setViewportView(txtPane);

        lblTitulo = new JLabel(nombre);
        lblTitulo.setFont(new Font("Tahoma", Font.PLAIN, 28));
        lblTitulo.setBounds(295, 11, 248, 34);
        contentPane.add(lblTitulo);
        this.setVisible(true);
    }

}
