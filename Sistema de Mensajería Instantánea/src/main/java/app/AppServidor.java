package app;

import modelo.Servidor;

public class AppServidor {
    public static void main(String[] args) {
        Thread servidor = new Thread(new Servidor());
        servidor.start();
    }
}
