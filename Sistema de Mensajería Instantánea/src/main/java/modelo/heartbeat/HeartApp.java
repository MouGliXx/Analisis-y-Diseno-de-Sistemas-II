package modelo.heartbeat;

import modelo.Conexion;
import modelo.Mensaje;
import modelo.heartbeat.vista.TextField;
import modelo.heartbeat.vista.VentanaHeartBeat;
import Servidor;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

public class HeartApp {


    public static void main(String[] args) throws InterruptedException, ClassNotFoundException, IOException {
        long startTime;
        long endTime;
        VentanaHeartBeat ventana = new VentanaHeartBeat("HeartBeat");
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
                //ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "servidor.jar");
                System.out.printf("Se cayo servidor");
                //Process process = processBuilder.start();
                String ubicacionActual = System.getProperty("user.dir");
                System.out.println("La ubicación actual es: " + ubicacionActual);
                Thread.sleep(5000);
                try {
                    startTime = System.nanoTime();
                    socket = new Socket("localhost",1235);
                    conexion.setSocket(socket);
                    conexion.setOutput(new ObjectOutputStream(socket.getOutputStream()));
                    conexion.setInput(new ObjectInputStream(socket.getInputStream()));

                    conexion.mandarMensaje(new Mensaje(-1,-1,"HOLA","Hola como andas","Tomas"));


                    //System.out.println(response.toString());
                    endTime = System.nanoTime();
                    String ping = "\nPing: " + (float) (endTime - startTime) / 1000000 + " ms";
                    TextField.txtPane.setText(TextField.txtPane.getText() + ping);
                } catch (IOException ex){
                    JOptionPane.showMessageDialog(null,"Se cayo el servidor");
                }

            }

        }
    }
}
