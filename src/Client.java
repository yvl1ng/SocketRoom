import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {
    private String host;
    private int port;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private Callback callback;

    public Client(String host, int port, Callback callback) {
        this.host = host;
        this.port = port;
        this.callback = callback;
    }

    public void send(String message) {
        writer.println(message);
    }

    public void shutdown() {
        try {
            socket.close();
        } catch (IOException e) {

        }
    }

    @Override
    public void run() {
        try {
            socket = new Socket(host, port);
            callback.onMessageReceived("Socket连接成功 ==> " + host + ":" + port);

            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String serverMessage;
            while ((serverMessage = reader.readLine()) != null) {
                callback.onMessageReceived(serverMessage);
            }

        } catch (IOException e) {
            callback.onMessageReceived("Socket连接失败 ==> " + host + ":" + port);
        }
    }
}
