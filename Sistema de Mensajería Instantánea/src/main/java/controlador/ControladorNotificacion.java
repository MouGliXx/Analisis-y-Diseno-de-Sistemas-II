package controlador;

import modelo.Sistema;
import modelo.interfaces.IObserver;
import vista.interfaces.IVistaNotificacion;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorNotificacion implements ActionListener, IObserver {
    private final IVistaNotificacion vista;
    private int ipEmisor; //TODO solucionar esto

    public ControladorNotificacion(IVistaNotificacion vista, int ipEmisor) {
        this.vista = vista;
        this.vista.setActionListener(this);
        this.ipEmisor = ipEmisor;
    }

    public ControladorNotificacion(IVistaNotificacion vista) {
        this.vista = vista;
        this.vista.setActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Aceptar" -> {
                if (vista.getTipo() == 3) { //Si es de tipo solicitud -> creo ventanaMensajes
                    vista.creaOtraVentana(0, "nombre usuario emisor"); //TODO poner el nombre de usuario del emisor que recibo del modelo
                    System.out.printf("la ip el emisor es:" + ipEmisor);
                    try {
                        Sistema.getInstance().getCliente().aceptarConexion(ipEmisor);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else { //Si es de tipo error -> creo ventanaInicio
                    vista.creaOtraVentana(1, null);
                }
                Sistema.getInstance().getCliente().getObservadores().remove(this);
                vista.cerrarVentana();
            }
            case "Cancelar" -> {
                if (vista.getTipo() == 3) { //Si es de tipo solicitud -> informo al emisor
                    //TODO avisarle al emisor que la solicitud ha sido RECHAZADA
                }
                vista.creaOtraVentana(1, null); //TODO poner el nombre de usuario del emisor que recibo del modelo
                vista.cerrarVentana();
            }
        }
    }

    @Override
    public void notificarCambio(String estado, String mensaje) {
        //A esta funcion solo llego si soy el EMISOR y el RECEPTOR acepto mi solicitud
        if ("Abro ventana sesion".equals(estado)) {
            vista.creaOtraVentana(0, "nombre usuario emisor"); //TODO poner el nombre de usuario del emisor que recibo del modelo
            Sistema.getInstance().getCliente().getObservadores().remove(this);
            vista.cerrarVentana();
        }
    }

    public void notificarCambio(String estado, int puerto) {
        //A esta funcion solo llego si soy el EMISOR y el RECEPTOR acepto mi solicitud
        System.out.printf("ENTRO ACA:  ",puerto);
    }
}
