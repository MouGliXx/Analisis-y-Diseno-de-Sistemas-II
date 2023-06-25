package modelo.heartbeat;

import modelo.Conexion;
import modelo.Mensaje;
import modelo.heartbeat.vista.VentanaHeartBeat;
import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

public class HeartApp {
    public static void main(String[] args) throws InterruptedException {
        long startTime, startTime2;
        long endTime, endTime2;

        VentanaHeartBeat ventanaH = new VentanaHeartBeat();
        ventanaH.ejecutar();

        Conexion conexion = new Conexion();
        Conexion conexion2 = new Conexion();

        while(true) {
            Socket socket;
            Socket socket2;
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
                ventanaH.agregarEnLista1(ping);
            } catch (IOException e) {
                Thread.sleep(1000);
                try {
                    startTime = System.nanoTime();
                    socket = new Socket("localhost",1235);
                    conexion.setSocket(socket);
                    conexion.setOutput(new ObjectOutputStream(socket.getOutputStream()));
                    conexion.setInput(new ObjectInputStream(socket.getInputStream()));

                    conexion.mandarMensaje(new Mensaje(-1,-1,"HOLA","Hola como andas","Tomas"));

                    endTime = System.nanoTime();
                    String ping = "\nPing: " + (float) (endTime - startTime) / 1000000 + " ms";
                    ventanaH.agregarEnLista1(ping);
                } catch (IOException ex){
//                    JOptionPane.showMessageDialog(null,"El servidor 1235 esta caido");
                    ventanaH.agregarEnLista1("El servidor 1235 esta caido");
                }
            }

            try{
                Thread.sleep(1000);

                startTime2 = System.nanoTime();
                socket2 = new Socket("localhost", 1234);

                conexion2.setSocket(socket2);

                conexion2.setOutput(new ObjectOutputStream(socket2.getOutputStream()));

                conexion2.setInput(new ObjectInputStream(socket2.getInputStream()));

                conexion2.mandarMensaje(new Mensaje(-1,-1,"HOLA","Como va","Ignacio"));

                endTime2 = System.nanoTime();
                String ping2  = "\nPuerto 1234: "+ (float) (endTime2-startTime2)/1000000 + " ms";
                ventanaH.agregarEnLista2(ping2);
            } catch (IOException e) {
                Thread.sleep(1000);
                try {
                    startTime = System.nanoTime();
                    socket2 = new Socket("localhost", 1234);
                    conexion2.setSocket(socket2);
                    conexion2.setOutput(new ObjectOutputStream(socket2.getOutputStream()));
                    conexion2.setInput(new ObjectInputStream(socket2.getInputStream()));

                    conexion2.mandarMensaje(new Mensaje(-1,-1,"HOLA","Hola como andas","Tomas"));

                    endTime = System.nanoTime();
                    String ping2 = "\nPing: " + (float) (endTime - startTime) / 1000000 + " ms";
                    ventanaH.agregarEnLista2(ping2);
                } catch (IOException ex){
//                    JOptionPane.showMessageDialog(null,"El servidor 1234 esta caido");
                    ventanaH.agregarEnLista2("El servidor 1234 esta caido");
                }
            }
        }
    }
}
