import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server implements Runnable {
    private final String host;
    private final Integer port;
    private Callback callback;
    private ServerSocket serverSocket;
    private ArrayList<Socket> clientSocketList;
    private ExecutorService executorService;

    private void handleClient(Socket socket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String inputLine;
        while ((inputLine = reader.readLine()) != null) {
            String message = "[" + socket.getRemoteSocketAddress().toString().replace("/", "") + "] " + inputLine;

            for (Socket client : clientSocketList) {
                PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
                writer.println(message);
            }
            callback.onMessageReceived(message);
        }
    }

    public Server(String host, Integer port, Callback callback) {
        this.host = host;
        this.port = port;
        this.callback = callback;
        this.clientSocketList = new ArrayList<>();
        this.executorService = Executors.newCachedThreadPool();
    }

    public void send(String _message) {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            String ip = localhost.getHostAddress();
            String message = "[" + ip + ":" + port + "] " + _message;
            for (Socket client : clientSocketList) {
                PrintWriter writer = null;
                writer = new PrintWriter(client.getOutputStream(), true);
                writer.println(message);
            }
            callback.onMessageReceived(message);
        } catch (Exception e) {

        }
    }

    public void shutdown() {
        executorService.shutdownNow();
        try {
            if (serverSocket != null) serverSocket.close();
            callback.onMessageReceived("Socket服务端停止...");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        callback.onMessageReceived("Socket服务端启动...");
        while (!serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                clientSocketList.add(clientSocket);
                executorService.submit(() -> {
                    try {
                        this.handleClient(clientSocket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                callback.onMessageReceived("已连接 ==> " + clientSocket.getRemoteSocketAddress().toString().replace("/", ""));
            } catch (Exception e) {

            }
        }
    }
}
