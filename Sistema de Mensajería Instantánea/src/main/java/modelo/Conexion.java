package modelo;

import java.io.*;
import java.net.Socket;

public class Conexion {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String nombreUsuario;

    public Conexion() {
    }

    public void mandarMensaje(Object o){
        try{
            output.writeObject(o);
        } catch(IOException e) {
            System.err.println("No se ha inicializado la salida del socket.");
        }
    }

    public void close() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
        } catch (IOException e) {
            System.err.println("\nError al cerrar el socket: " + e.getMessage());
        }
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ObjectOutputStream getOutput() {
        return output;
    }

    public void setOutput(ObjectOutputStream output) {
        this.output = output;
    }

    public ObjectInputStream getInput() {
        return input;
    }

    public void setInput(ObjectInputStream input) {
        this.input = input;
    }

    @Override
    public String toString() {
        return "Conexion{" +
                ", socket=" + socket +
                ", output=" + output +
                ", input=" + input +
                '}';
    }
}