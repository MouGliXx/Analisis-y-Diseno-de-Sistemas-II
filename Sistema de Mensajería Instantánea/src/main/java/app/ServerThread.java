import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;

public class ServerThread implements Runnable{
    private SocketIO socket;
    private ServerSocket server;
    private Cliente cliente;

    ServerThread(ServerSocket serverSocket, Cliente cliente) {
        this.server = serverSocket;
        this.cliente = cliente;
    }

    public void run(){
        try{
            System.out.println("Servidor escuchando en el puerto " + server.getLocalPort());
            this.cliente.getSocketServer().setSocket(server.accept());
            // Se setea a este cliente como el servidor
            this.setAsServer();
            this.setListenerMensajes();
        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor");
            e.printStackTrace();
        }
    }

    private void setAsServer() {
        try {
            SocketIO socketServer = this.cliente.getSocketServer();
            socketServer.setOutput(new PrintWriter(socketServer.getSocket().getOutputStream(), true));
            socketServer.setInput(new BufferedReader(new InputStreamReader(socketServer.getSocket().getInputStream())));
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setListenerMensajes() {
        SocketIO socketServer = this.cliente.getSocketServer();
        if (socketServer.getInput() != null) {
            Thread receiverThread = new Thread(new ListenerThread(socketServer.getInput(),"Cliente"));
            receiverThread.start();
        } else {
            System.out.println("No se ha establecido una conexión previa.");
        }
    }
}