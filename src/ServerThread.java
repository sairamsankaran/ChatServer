import java.io.*;
import java.net.Socket;
import java.util.HashSet;

/**
 * Created by sairamsankaran on 3/31/14.
 */
public class ServerThread extends Thread {
    private Server server;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String nickname;

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
            writer.println("Please enter a nickname: ");
            this.nickname = reader.readLine();
            while (!validNickname(this.nickname)) { // prompt for valid nickname
                writer.println("Please enter a valid nickname: ");
                this.nickname = reader.readLine();
            }
            writer.println("Hello " + this.nickname + " welcome to chat room!");
            writer.println("There are " + this.server.getSize() + " other parties in the room including you.");
            while (true) {
                String message = reader.readLine();
                if (!validMessage(message)) {
                    continue; // ignore the message
                }
                server.sendToAll(this.nickname, message);
                if (message.equalsIgnoreCase("bye")) {
                    break;
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            server.removeConnection(socket);
        }
    }

    private boolean validNickname(String nickname) {
        return (this.nickname == "" || this.nickname.length() < 1);
    }

    private boolean validMessage(String message) {
        return (message == "" || message.length() < 1);
    }

    public PrintWriter getWriter() {
        return this.writer;
    }
}
