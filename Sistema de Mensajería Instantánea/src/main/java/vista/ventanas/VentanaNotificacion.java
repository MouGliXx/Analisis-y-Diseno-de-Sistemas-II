package vista.ventanas;

import controlador.ControladorInicio;
import controlador.ControladorMensajes;
import modelo.Sistema;
import modelo.Usuario;
import modelo.interfaces.IObserver;
import vista.interfaces.IVistaNotificacion;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class VentanaNotificacion extends JFrame implements IVistaNotificacion {
    private int tipo; // 1=error | 2=espera | 3=solicitud
    private JPanel PanelPrincipal;
    private JPanel PanelCentral;
    private JButton aceptarButton;
    private JButton cancelarButton;
    private JLabel TituloLabel;
    private JLabel Contenido1Label;
    private JLabel Contenido2Label;
    private JPanel ButtonPanel;

    @Override
    public void setActionListener(ActionListener controlador) {
        this.aceptarButton.addActionListener(controlador);
        this.cancelarButton.addActionListener(controlador);
    }

    @Override
    public void ejecutar() {
        setTitle("Sistema de Mensajeria Instantaneo");
        pack(); //Coloca los componentes
        setContentPane(PanelPrincipal);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        setSize(600,300); //Dimensiones del JFrame
        setResizable(false); //No redimensionable
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon(getClass().getResource("/Icono.png")).getImage());
        toFront();
    }

    @Override
    public void cerrarVentana() {
        setVisible(false); //Oculto la ventana
        dispose(); //Cierro la ventana
    }

    @Override
    public void creaOtraVentana(Sistema sistema, int tipo, String nombreUsuarioEmisor) {
        Usuario usuario = sistema.getUsuario();
        switch (tipo) {
            case 0 -> { //CASO VENTANA MENSAJES
                VentanaMensajes ventanaMensajes = new VentanaMensajes();
                ControladorMensajes controladorMensajes = new ControladorMensajes(ventanaMensajes, sistema);
                ArrayList<IObserver> observadores = new ArrayList<>(usuario.getObservadores());
                observadores.add(controladorMensajes);
                usuario.setObservadores(observadores);
                usuario.setConnected(true);
                ventanaMensajes.setUsuarios(usuario.getNombreDeUsuario(), "nombre del receptor"); //TODO poner nombre de usuario receptor y hacer que se configure el nombre de usuario en la notificacion
                ventanaMensajes.ejecutar();
            }
            case 1 -> { //CASO VENTANA INICIO
                VentanaInicio ventanaInicio = new VentanaInicio();
                ControladorInicio controladorInicio = new ControladorInicio(ventanaInicio, sistema);
                usuario.agregarObservador(controladorInicio);
                usuario.setListenerServidor();
                ventanaInicio.ejecutar();
            }
        }
    }

    @Override
    public void lanzarVentanaEmergente(String mensaje) {
        JFrame jFrameVacio = new JFrame();
        JOptionPane.showMessageDialog(jFrameVacio, mensaje);
    }

    public int getTipo() {
        return tipo;
    }

    @Override
    public void setTipoVentana(int tipo, String nombreUsuarioEmisor) {
        this.tipo = tipo;
        switch (tipo) {
            case 1 -> { //tipo 1 -> Notificacion Error
                this.TituloLabel.setText("Error");
                this.Contenido1Label.setText("No ha sido posible conectarse con el usuario ingresado.");
                this.Contenido2Label.setText("Es posible que este no se encuentra disponible.");
                this.aceptarButton.setVisible(true);
                this.cancelarButton.setVisible(false);
            }
            case 2 -> { //tipo 2 -> Notificacion Espera
                this.TituloLabel.setText("Espere...");
                this.Contenido1Label.setText("La solicitud ha sido enviada correctamente. Aguarde a ");
                this.Contenido2Label.setText("que el usuario ingresado responda la misma.");
                this.aceptarButton.setVisible(false);
                this.cancelarButton.setVisible(true);
            }
            case 3 -> { //tipo 3 -> Notificacion Solicitud
                this.TituloLabel.setText("Atencion!");
                this.Contenido1Label.setText("El usuario '" + nombreUsuarioEmisor + "' quiere unirse a una ");
                this.Contenido2Label.setText("sesión con usted.");
                this.aceptarButton.setVisible(true);
                this.cancelarButton.setVisible(true);
            }
        }
    }
}
