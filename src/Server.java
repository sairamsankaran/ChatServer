import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Created by sairamsankaran on 3/31/14.
 */
public class Server {
    private List<Socket> sockets = new ArrayList<Socket>();

    public Server(int port) throws IOException {
        listen(port);
    }

    private void listen(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server listening on socket: " + serverSocket);
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Connecting to client socket: " + socket);
            sockets.add(socket);
            System.out.println("Number of clients in chat room: " + sockets.size());
            new ServerThread(this, socket);
        }
    }

    public void sendToAll(String sender, String message) {
        // Broadcast message to all clients
        PrintWriter writer;
        Iterator it = sockets.iterator();
        while (it.hasNext()) {
            Socket socket = (Socket) it.next();
            try {
                writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println(sender + " : " + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeConnection(Socket socket) {
        // remove sockets to disconnected clients
        try {
            if (null != socket) {
                System.out.println("Closing  connection to client: " + socket);
                sockets.remove(socket);
                socket.close();
                System.out.println("Number of clients in chat room: " + sockets.size());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected int getSize() {
        return sockets.size();
    }

    public static void main(String args[]) throws Exception {
        int port = Integer.parseInt(args[0]);
        new Server(port);
    }
}