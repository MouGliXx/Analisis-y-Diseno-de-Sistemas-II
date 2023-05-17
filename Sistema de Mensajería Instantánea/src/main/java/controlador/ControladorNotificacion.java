package controlador;

import modelo.Sistema;
import modelo.interfaces.IObserver;
import vista.interfaces.IVistaNotificacion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorNotificacion implements ActionListener, IObserver {
    private final IVistaNotificacion vista;
    private final Sistema sistema;
    private int ipEmisor;

    public ControladorNotificacion(IVistaNotificacion vista, Sistema sistema,int ipEmisor) {
        this.vista = vista;
        this.sistema = sistema;
        this.vista.setActionListener(this);
        this.ipEmisor = ipEmisor;
    }

    public ControladorNotificacion(IVistaNotificacion vista, Sistema sistema) {
        this.vista = vista;
        this.sistema = sistema;
        this.vista.setActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Aceptar" -> {
                if (vista.getTipo() == 3) { //Si es de tipo solicitud -> creo ventanaMensajes
                    vista.creaOtraVentana(sistema, 0, "nombre usuario emisor"); //TODO poner el nombre de usuario del emisor que recibo del modelo
                    System.out.printf("la ip el emisor es:" + ipEmisor);
                    try {
                        this.sistema.getCliente().aceptarConexion(ipEmisor);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else { // -> creo ventanaInicio
                    vista.creaOtraVentana(sistema, 1, null);
                }
                this.sistema.getCliente().getObservadores().remove(this);
                vista.cerrarVentana();
            }
            case "Cancelar" -> {
                // TODO avisarle al usuario que no se acepto la sesion ES LO QUE QUEDA
                vista.creaOtraVentana(sistema, 1, null); //TODO poner el nombre de usuario del emisor que recibo del modelo
                vista.cerrarVentana();
            }
        }
    }

    @Override
    public void notificarCambio(String estado, String mensaje) {
        //A esta funcion solo llego si soy el EMISOR y el RECEPTOR acepto mi solicitud
        if ("Abro ventana sesion".equals(estado)) {
            vista.creaOtraVentana(sistema, 0, "nombre usuario emisor"); //TODO poner el nombre de usuario del emisor que recibo del modelo
            this.sistema.getCliente().getObservadores().remove(this);
            vista.cerrarVentana();
        }
    }

    public void notificarCambio(String estado, int puerto) {
        //A esta funcion solo llego si soy el EMISOR y el RECEPTOR acepto mi solicitud
        System.out.printf("ENTRO ACA:  ",puerto);

    }
}
