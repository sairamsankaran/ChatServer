import java.io.*;
import java.net.Socket;

/**
 * Created by sairamsankaran on 3/31/14.
 */
public class ServerThread extends  Thread {
    private Server server;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public ServerThread(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        start();
    }

    public void run(){
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println("Welcome to chat room!");
            while (true) {
                String message = reader.readLine();
                System.out.println("Sending message: " + message);
                server.sendToAll(message);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            server.removeConnection(socket);
        }
    }
}
