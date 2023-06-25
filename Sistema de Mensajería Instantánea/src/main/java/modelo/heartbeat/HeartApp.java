package modelo.heartbeat;

import modelo.Conexion;
import modelo.Mensaje;
import modelo.heartbeat.vista.TextField;
import modelo.heartbeat.vista.VentanaHeartBeat;
import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

public class HeartApp {
    public static void main(String[] args) throws InterruptedException, ClassNotFoundException, IOException {
        long startTime, startTime2;
        long endTime, endTime2;

        VentanaHeartBeat ventana = new VentanaHeartBeat("HeartBeat");

        TextField.txtPane = ventana.txtPane;
        Conexion conexion = new Conexion();
        Conexion conexion2=new Conexion();

        while(true) {
            Socket socket = null;
            Socket socket2 = null;
            try {
                Thread.sleep(1000);
                startTime = System.nanoTime();
                socket = new Socket("localhost",1235);

                conexion.setSocket(socket);

                conexion.setOutput(new ObjectOutputStream(socket.getOutputStream()));

                conexion.setInput(new ObjectInputStream(socket.getInputStream()));

                conexion.mandarMensaje(new Mensaje(-1,-1,"HOLA","Hola como andas","Tomas"));

                endTime = System.nanoTime();
                String ping = "\nPuerto 1235: "+ (float) (endTime-startTime)/1000000 + " ms";
                TextField.txtPane.setText(TextField.txtPane.getText() + ping);

            } catch (IOException e) {
                //ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "servidor.jar");
                System.out.printf("Se cayo servidor el 1235");
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
                    JOptionPane.showMessageDialog(null,"Se cayo el servidor 1235");
                }

            }

            try{
                Thread.sleep(1000);

                startTime2 = System.nanoTime();
                socket2 = new Socket("localhost", 1234);

                conexion2.setSocket(socket2);

                conexion2.setOutput(new ObjectOutputStream(socket2.getOutputStream()));

                conexion2.setOutput(new ObjectOutputStream(socket2.getOutputStream()));

                conexion2.mandarMensaje(new Mensaje(-1,-1,"HOLA","Como va","Ignacio"));


                endTime2 = System.nanoTime();
                String ping2  = "\nPuerto 1234: "+ (float) (endTime2-startTime2)/1000000 + " ms";
                TextField.txtPane.setText(TextField.txtPane.getText() + ping2);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                //ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "servidor.jar");
                System.out.printf("Se cayo servidor el 1234");
                //Process process = processBuilder.start();
                String ubicacionActual = System.getProperty("user.dir");
                System.out.println("La ubicación actual es: " + ubicacionActual);
                Thread.sleep(5000);
                try {
                    startTime = System.nanoTime();
                    socket = new Socket("localhost",1234);
                    conexion2.setSocket(socket2);
                    conexion2.setOutput(new ObjectOutputStream(socket2.getOutputStream()));
                    conexion2.setInput(new ObjectInputStream(socket2.getInputStream()));

                    conexion2.mandarMensaje(new Mensaje(-1,-1,"HOLA","Hola como andas","Tomas"));

                    //System.out.println(response.toString());
                    endTime = System.nanoTime();
                    String ping = "\nPing: " + (float) (endTime - startTime) / 1000000 + " ms";
                    TextField.txtPane.setText(TextField.txtPane.getText() + ping);
                } catch (IOException ex){
                    JOptionPane.showMessageDialog(null,"Se cayo el servidor 1234");
                }
            }
        }
    }
}
