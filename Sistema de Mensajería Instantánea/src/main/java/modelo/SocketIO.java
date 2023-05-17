package modelo;

import java.io.*;
import java.net.Socket;

public class SocketIO{
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;

    public SocketIO() {
    }

    public void mandarMensaje(Object o){
//        try{
//            output.writeObject(o);
//        } catch(IOException e) {
//            System.err.println("No se ha inicializado la salida del socket.");
//        }
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

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public PrintWriter getOutput() {
        return output;
    }

    public void setOutput(PrintWriter output) {
        this.output = output;
    }

    public BufferedReader getInput() {
        return input;
    }

    public void setInput(BufferedReader input) {
        this.input = input;
    }
}