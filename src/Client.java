import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by sairamsankaran on 3/31/14.
 */
public class Client extends  Thread {
    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    public Client(String host, int port) throws IOException {
        setLayout( new BorderLayout() );
        add("North", tf);
        add("Center", ta);
        tf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processMessage(e.getActionCommand());
            }
        });
        try {
            socket = new Socket(host, port);
            System.out.println("Connected to client socket " + socket);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            new Thread(this).start();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void processMessage(String message) throws  IOException {
        try {
            dataOutputStream.writeUTF(message);
            tf.setText("");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                String message = dataInputStream.readUTF();
                ta.append(message + "\n");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
