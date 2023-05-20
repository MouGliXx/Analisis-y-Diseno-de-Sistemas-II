package modelo;

import java.io.Serializable;

public class Mensaje implements Serializable {
    private int puertoOrigen;
    private int puertoDestino;
    String mensajeControl;
    String mensaje;
    private String nombreUsuarioEmisor;

    public Mensaje(int puertoOrigen, int puertoDestino, String mensajeControl, String mensaje,String nombreUsuarioEmisor) {
        this.puertoOrigen = puertoOrigen;
        this.puertoDestino = puertoDestino;
        this.mensajeControl = mensajeControl;
        this.mensaje = mensaje;
        this.nombreUsuarioEmisor=nombreUsuarioEmisor;
    }

    public String getNombreUsuarioEmisor() {
        return nombreUsuarioEmisor;
    }

    public void setNombreUsuarioEmisor(String nombreUsuarioEmisor) {
        this.nombreUsuarioEmisor = nombreUsuarioEmisor;
    }

    public int getPuertoOrigen() {
        return puertoOrigen;
    }

    public void setPuertoOrigen(int puertoOrigen) {
        this.puertoOrigen = puertoOrigen;
    }

    public int getPuertoDestino() {
        return puertoDestino;
    }

    public void setPuertoDestino(int puertoDestino) {
        this.puertoDestino = puertoDestino;
    }

    public String getMensajeControl() {
        return mensajeControl;
    }

    public void setMensajeControl(String mensajeControl) {
        this.mensajeControl = mensajeControl;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
