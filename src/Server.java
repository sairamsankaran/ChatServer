import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Created by sairamsankaran on 3/31/14.
 */
public class Server {
    private List<Socket> sockets = new ArrayList<Socket>();
    private List<String> users = new ArrayList<String>();
    private List<String> commands = new ArrayList<String>();

    public Server(int port) throws IOException {
        addDefaultCommands();
        listen(port);
    }

    public List<String> getUsers() {
        return users;
    }

    private void listen(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server listening on socket: " + serverSocket);
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Connecting to client socket: " + socket);
            sockets.add(socket);
            System.out.println("Number of clients in chat room: " + sockets.size());
            Runnable socketRunnable = new SocketRunnable(this, socket); // prepare job for the thread
            Thread socketThread = new Thread(socketRunnable); // create thread and give it a job
            socketThread.start(); // ask the thread to start its job
        }
    }

    protected void addCommands(List<String> commands) {
        this.commands.addAll(commands);
    }

    protected void addCommand(String command) {
        this.commands.add(command);
    }

    protected void removeCommand(String command) {
        this.commands.remove(command);
    }

    protected void addDefaultCommands() {
        String[] commands = {"bye", "cmds", "help", "users"};
        addCommands(new ArrayList<String>(Arrays.asList(commands)));
    }

    protected List<String> getCommands() {
        return this.commands;
    }

    protected boolean addNewUser(String user) {
        Iterator<String> iterator = this.users.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().equalsIgnoreCase(user.trim())) {
                return false;
            }
        }
        this.users.add(user.toLowerCase().trim());
        return true;
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

    public void removeConnection(String user, Socket socket) {
        // remove sockets of disconnected clients
        try {
            if (null != socket) {
                System.out.println("Closing  connection to client: " + socket);
                sockets.remove(socket);
                socket.close();
                System.out.println("Number of clients in chat room: " + sockets.size());
                users.remove(user);
                sendToAll(user, "Has left the room.");
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