import java.io.*;
import java.net.Socket;
import java.util.Iterator;

/**
 * Created by sairamsankaran on 3/31/14.
 */
public class SocketRunnable implements Runnable {
    private Server server;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String username;

    private static final int EXIT_CHAT_SESSION     = 0;
    private static final int CONTINUE_CHAT_SESSION = 1;

    public SocketRunnable(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    public void run(){
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            writer.println("Welcome to chat room!");
            writer.println("Please enter a username: ");
            this.username = reader.readLine();
            while (!isValidNickname(this.username)) { // prompt for valid username
                writer.println("Please enter a valid username: ");
                this.username = reader.readLine();
            }
            writer.println("Hello " + this.username + " welcome to chat room!");
            writer.println("There are " + this.server.getSize() + " other parties in the room including you.");
            printUsers();
            writer.println("Type '/cmds' for a list of commands.");
            int continueChat = 1;
            while (true) {
                String message = reader.readLine();
                if (!isValidMessage(message)) {
                    continue; // ignore the message
                }
                if (isCommand(message)) {
                    if (isValidCommand(message)) {
                        continueChat = handleCommand(message.toLowerCase().trim().substring(1));
                    }
                    if (continueChat == 1) {
                        continue;
                    } else {
                        break;
                    }
                } else { // if valid message and not a command
                    server.sendToAll(this.username, message);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            writer.close();
            server.removeConnection(username, socket);
        }
    }

    private boolean isValidNickname(String username) {
        if (this.username != null
                && !this.username.equalsIgnoreCase("")
                && this.username.length() >= 1
                && server.addNewUser(username)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isValidMessage(String message) {
        // a message must have at least a single character
        if (message != null
                && !message.equalsIgnoreCase("")
                && message.length() >= 1) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isCommand(String string) {
        // check if string starts with a forward slash
        if (string.toLowerCase().trim().startsWith("/")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isValidCommand(String command) {
        Iterator<String> iterator = server.getCommands().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().equalsIgnoreCase(command.toLowerCase().trim().substring(1))) {
                return true;
            }
        }
        writer.println("Not a valid command!");
        writer.println("Type '/cmds' for a list of commands.");
        return false;
    }

    private int handleCommand(String command) {
        // assumption: leading '/' removed in command
        if (command.trim().equalsIgnoreCase(SharedCommandList.COMMAND_BYE)){
            writer.println("Goodbye " + this.username);
            return EXIT_CHAT_SESSION;
        } else if (command.trim().equalsIgnoreCase(SharedCommandList.COMMAND_HELP)
                || command.trim().equalsIgnoreCase(SharedCommandList.COMMAND_CMDS)) {
            printHelp();
        } else if (command.trim().equalsIgnoreCase(SharedCommandList.COMMAND_USERS)) {
            printUsers();
        }
        return CONTINUE_CHAT_SESSION; // default
    }

    private void printHelp() {
        this.writer.println("/bye   - exit chat room");
        this.writer.println("/cmds  - print this message");
        this.writer.println("/help  - print this message");
        this.writer.println("/users - print list of users in chat room");
    }

    private void printUsers() {
        Iterator<String> userIterator = server.getUsers().iterator();
        while (userIterator.hasNext()) {
            writer.println(" * " + userIterator.next());
        }
    }
}
