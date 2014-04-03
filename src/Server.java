import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by sairamsankaran on 3/31/14.
 */
public class Server {
    private HashMap<Socket, PrintWriter> writers;

    public Server(int port) throws IOException {
        writers = new HashMap<Socket, PrintWriter>();
        listen(port);
    }

    private void listen(int port) throws  IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server listening on socket: " + serverSocket);
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Connecting to client socket: " + socket);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writers.put(socket, writer);
            new ServerThread(this, socket);
        }
    }

    public void sendToAll(String message) {
        // Broadcast message to all clients
        System.out.println("Message from chat room: " + message);
        Iterator it = writers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            ((PrintWriter)(pairs.getValue())).println(message);
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    public void removeConnection(Socket socket) {
        // remove sockets to disconnected clients
        try {
            if (null != socket) {
                System.out.println("Connection lost to client socket: " + socket);
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