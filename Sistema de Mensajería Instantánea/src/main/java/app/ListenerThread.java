package app;

import java.io.BufferedReader;
import java.io.IOException;

public class ListenerThread implements Runnable{

    public BufferedReader input;
    private String mensaje = "";
    private String usuario = "";

    public ListenerThread(BufferedReader input, String usuario) {
        this.input = input;
        this.usuario = usuario;
    }

    public void run(){
        while (true){
            try{
                mensaje = this.input.readLine();
                if (mensaje != null) {
                    System.out.println("["+usuario + "]:" + mensaje);
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
