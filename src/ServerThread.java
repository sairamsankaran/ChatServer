import java.io.*;
import java.net.Socket;
import java.util.Iterator;

/**
 * Created by sairamsankaran on 3/31/14.
 */
public class ServerThread extends Thread {
    private Server server;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String username;

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
            writer.println("Please enter a username: ");
            this.username = reader.readLine();
            while (!validNickname(this.username)) { // prompt for valid username
                writer.println("Please enter a valid username: ");
                this.username = reader.readLine();
            }
            writer.println("Hello " + this.username + " welcome to chat room!");
            writer.println("There are " + this.server.getSize() + " other parties in the room including you.");
            Iterator<String> userIterator = server.getUsers().iterator();
            while (userIterator.hasNext()) {
                writer.println(" * " + userIterator.next());
            }
            writer.println("Type 'help' for a list of commands.");
            while (true) {
                String message = reader.readLine();
                if (!validMessage(message)) {
                    continue; // ignore the message
                }
                if (message.equalsIgnoreCase("bye")) {
                    writer.println("Goodbye " + this.username);
                    break;
                }
                if (message.equalsIgnoreCase("help")) {
                    printHelp();
                    continue;
                }
                server.sendToAll(this.username, message);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            server.removeConnection(username, socket);
        }
    }

    private boolean validNickname(String username) {
        if (this.username != null && this.username != "" && this.username.length() >= 1 && server.addNewUser(username)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean validMessage(String message) {
        if (message != "" && message.length() >= 1) {
            return true;
        } else {
            return false;
        }
    }

    private void printHelp() {
        this.writer.println("'bye'  - exit chat");
        this.writer.println("'help' - print this message");
    }
}
