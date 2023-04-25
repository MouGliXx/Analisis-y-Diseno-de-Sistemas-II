package vista.ventanas;

import controlador.ControladorInicio;
import controlador.ControladorMensajes;
import modelo.Sistema;
import modelo.Usuario;
import modelo.interfaces.IObserver;
import vista.interfaces.IVistaNotificacion;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.util.ArrayList;

public class VentanaNotificacion extends JFrame implements IVistaNotificacion {
    private int tipo;
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
    public void setWindowListener(WindowListener controlador) {
        this.addWindowListener(controlador);
    }

    @Override
    public void ejecutar() {
        setTitle("Sistema de Mensajeria Instantaneo");
        pack(); //Coloca los componentes
        setContentPane(PanelPrincipal);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setVisible(true);
        setSize(600,300); //Dimensiones del JFrame
        setResizable(false); //No redimensionable
        setLocationRelativeTo(null);
        toFront();
    }

    @Override
    public void cerrarVentana() {
        setVisible(false); //Oculto la ventana
        dispose(); //Cierro la ventana
    }

    @Override
    public void creaOtraVentana(int tipo) {
        switch (tipo) {
            case 0: //CASO VENTANA MENSAJES
                VentanaMensajes ventanaMensajes = new VentanaMensajes();
                ControladorMensajes controladorMensajes = new ControladorMensajes(ventanaMensajes);

                ArrayList<IObserver> observadores = new ArrayList<>(Sistema.getInstance().getUsuario().getObservadores());
                observadores.add(controladorMensajes);
                Sistema.getInstance().getUsuario().setObservadores(observadores);
                Sistema.getInstance().getUsuario().setConnected(true);

                ventanaMensajes.setUsuarios(Sistema.getInstance().getUsuario().getNombreDeUsuario(), "nose como ponerlo"); //TODO poner nombre de usuario receptor y hacer que se configure el nombre de usuario en la notificacion
                ventanaMensajes.ejecutar();
                break;
            case 1: //CASO VENTANA INICIO
                VentanaInicio ventanaInicio = new VentanaInicio();
                ControladorInicio controladorInicio = new ControladorInicio(ventanaInicio);

                Sistema.getInstance().getUsuario().agregarObservador(controladorInicio);
                Sistema.getInstance().getUsuario().setListenerServidor();

                ventanaInicio.ejecutar();
                break;
        }
    }

    @Override
    public void lanzarVentanaEmergente(String mensaje) {
        JFrame jFrame = new JFrame();
        JOptionPane.showMessageDialog(jFrame, mensaje);
    }

    @Override
    public void setTipoVentana(int tipo, String nombreUsuarioEmisor) {
        this.tipo = tipo;
        switch (tipo) {
            case 1: //tipo 1 -> Notificacion Error
                this.TituloLabel.setText("Error");
                this.Contenido1Label.setText("No ha sido posible conectarse con el usuario ingresado.");
                this.Contenido2Label.setText("Es posible que este no se encuentra disponible.");
                this.aceptarButton.setVisible(true);
                this.cancelarButton.setVisible(false);
                break;
            case 2: //tipo 2 -> Notificacion Espera
                this.TituloLabel.setText("Espere...");
                this.Contenido1Label.setText("La solicitud ha sido enviada correctamente. Aguarde a ");
                this.Contenido2Label.setText("que el usuario ingresado responda la misma.");
                this.aceptarButton.setVisible(false);
                this.cancelarButton.setVisible(true);
                break;
            case 3: //tipo 3 -> Notificacion Solicitud
                this.TituloLabel.setText("Atencion!");
                this.Contenido1Label.setText("El usuario '" + nombreUsuarioEmisor + "' quiere unirse a una ");
                this.Contenido2Label.setText("sesión con usted.");
                this.aceptarButton.setVisible(true);
                this.cancelarButton.setVisible(true);
                break;
        }
    }
}
