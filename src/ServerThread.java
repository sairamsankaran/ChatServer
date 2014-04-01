import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by sairamsankaran on 3/31/14.
 */
public class ServerThread extends  Thread {
    Server server;
    Socket socket;

    public ServerThread(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        start();
    }

    public void run(){
        try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF("Welcome to chat room");
            while (true) {
                String message = dataInputStream.readUTF();
                System.out.println("Sending " + message);
                server.sendToAll(message);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            server.removeConnection(socket);
        }
    }
}
