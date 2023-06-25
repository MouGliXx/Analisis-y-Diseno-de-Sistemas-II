package modelo.heartbeat;

import modelo.Conexion;
import modelo.Mensaje;
import modelo.heartbeat.vista.TextField;
import modelo.heartbeat.vista.VentanaHeartBeat;
import servidor.Servidor;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Reintento {
    public static void main(String[] args) throws InterruptedException, ClassNotFoundException, IOException {
        long startTime;
        long endTime;
        boolean bandera = false;
        Servidor response = new Servidor(1235);
        VentanaHeartBeat ventana = new VentanaHeartBeat("Reintento");
        TextField.txtPane = ventana.txtPane;
        Conexion conexion = new Conexion();
        while(true) {
            Socket socket = null;
            try {
                Thread.sleep(1000);
                startTime = System.nanoTime();
                socket = new Socket("localhost",1235);
                conexion.setSocket(socket);
                conexion.setOutput(new ObjectOutputStream(socket.getOutputStream()));
                conexion.setInput(new ObjectInputStream(socket.getInputStream()));

                conexion.mandarMensaje(new Mensaje(-1,-1,"HOLA","Hola como andas","Tomas"));

                endTime = System.nanoTime();
                String ping = "\nPing: "+ (float) (endTime-startTime)/1000000 + " ms";
                TextField.txtPane.setText(TextField.txtPane.getText() + ping);

            } catch (IOException e) {
                ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "servidor.jar");
                Process process = processBuilder.start();
                String ubicacionActual = System.getProperty("user.dir");
                System.out.println("La ubicación actual es: " + ubicacionActual);
                Thread.sleep(1000);
                if (!bandera) {
                    for (int i = 10; i > 0; i--) {
                        Thread.sleep(1000);
                        TextField.txtPane.setText("Reintentando conexion-Tiempo restante: " + i + "segundos");
                    }
                    bandera = true;
                }
                try {
                    startTime = System.nanoTime();
                    socket = new Socket("localhost",1234);
                    conexion.setSocket(socket);
                    conexion.setOutput(new ObjectOutputStream(socket.getOutputStream()));
                    conexion.setInput(new ObjectInputStream(socket.getInputStream()));

                    conexion.mandarMensaje(new Mensaje(-1,-1,"HOLA","Hola como andas","Tomas"));


                    //System.out.println(response.toString());
                    endTime = System.nanoTime();
                    String ping = "\nPing: " + (float) (endTime - startTime) / 1000000 + " ms";
                    TextField.txtPane.setText(TextField.txtPane.getText() + ping);
                } catch (IOException ex){
                    JOptionPane.showMessageDialog(null,ex.getMessage());
                }

            }

        }
    }
}

