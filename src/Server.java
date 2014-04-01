import javax.xml.crypto.Data;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by sairamsankaran on 3/31/14.
 */
public class Server {
    Map<Socket, DataOutputStream> outputStreams = new HashMap<Socket, DataOutputStream>();

    public Server(int port) throws IOException {
        listen(port);
    }

    private void listen(int port) throws  IOException {
        ServerSocket ss = new ServerSocket(port);
        System.out.println("Listening on server socket: " + ss);
        while (true) {
            Socket s = ss.accept();
            System.out.println("Connection for client socket: " + s);
            DataOutputStream dataOutputStream = new DataOutputStream(s.getOutputStream());
            outputStreams.put(s, dataOutputStream);
            new ServerThread(this, s);
        }
    }

    public void sendToAll(String message) {
        // Broadcast message to all clients
        System.out.println("Message from chat room: " + message);
        Iterator it = outputStreams.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            try {
                ((DataOutputStream)pairs.getValue()).writeUTF(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    public void removeConnection(Socket socket) {
        // remove sockets to disconnected clients
        try {
            if (null != socket) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws Exception {
        int port = Integer.parseInt(args[0]);
        new Server(port);
    }
}