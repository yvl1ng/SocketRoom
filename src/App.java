import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Objects;

interface Callback {
    void onMessageReceived(String message);
}

public class App extends JFrame implements Callback {
    // 配置组件
    private final JTextField hostField = new JTextField(10);
    private final JTextField portField = new JTextField(5);
    private final JLabel infoLabel = new JLabel("程序就绪");

    // 输入输出组件
    private final JTextField inputField = new JTextField(60);
    private final JTextArea messageTextArea = new JTextArea();

    // 配置信息
    private String mode;
    private String host;
    private String port;

    // Socket对象
    Server server;
    Client client;

    private void Start() throws IOException {
        if ((!Objects.equals(mode, "") && !Objects.equals(host, "") && !Objects.equals(port, "")) && (Utils.isValidIP(host) && Utils.isValidPort(port))) {
            if (Objects.equals(mode, "server")) {
                String _host = host;
                int _port = Integer.parseInt(port);

                server = new Server(_host, _port, this);
                Thread serverThread = new Thread(server);
                serverThread.start();
            } else if (Objects.equals(mode, "client")) {
                String _host = host;
                int _port = Integer.parseInt(port);

                client = new Client(_host, _port, this);
                Thread clientThread = new Thread(client);
                clientThread.start();
            }

            infoLabel.setText("程序启动");

        } else {
            JOptionPane.showMessageDialog(null, "非法主机地址或主机端口，请检查后重试");
        }
    }

    private void Stop() {
        if (Objects.equals(mode, "server")) {
            server.shutdown();
        } else if (Objects.equals(mode, "client")) {
            client.shutdown();
        }

        infoLabel.setText("程序停止");
    }

    private void sendMessage() {
        String input = inputField.getText();
        if (!Objects.equals(input, "")) {
            inputField.setText("");
            if (Objects.equals(mode, "server")) {
                server.send(input);
            } else if (Objects.equals(mode, "client")) {
                client.send(input);
            }
        }
    }

    public App(String title, int width, int height) {
        setTitle(title);
        setSize(width, height);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 配置面板
        JPanel configPanel = new JPanel();
        configPanel.setBorder(BorderFactory.createTitledBorder("配置信息"));

        ButtonGroup csGroup = new ButtonGroup();
        JRadioButton serverRadio = new JRadioButton("服务端");
        serverRadio.addActionListener(e -> {
            if (serverRadio.isSelected()) {
                host = "0.0.0.0";
                hostField.setText(host);
                hostField.setEditable(false);
            }
        });

        JRadioButton clientRadio = new JRadioButton("客户端");
        clientRadio.addActionListener(e -> {
            if (clientRadio.isSelected()) {
                host = "";
                hostField.setText(host);
                hostField.setEditable(true);
            }
        });

        csGroup.add(serverRadio);
        csGroup.add(clientRadio);
        configPanel.add(serverRadio);
        configPanel.add(clientRadio);

        JLabel hostLabel = new JLabel("主机地址:");
        JLabel portLabel = new JLabel("主机端口:");
        configPanel.add(hostLabel);
        configPanel.add(hostField);
        configPanel.add(portLabel);
        configPanel.add(portField);
        configPanel.add(Box.createHorizontalStrut(20));

        JButton startButton = new JButton("启动");
        startButton.setPreferredSize(new Dimension(60, 20));
        startButton.addActionListener(e -> {
            if (serverRadio.isSelected()) {
                mode = "server";
            } else if (clientRadio.isSelected()) {
                mode = "client";
            }

            host = hostField.getText();
            port = portField.getText();

            try {
                Start();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        JButton stopButton = new JButton("停止");
        stopButton.setPreferredSize(new Dimension(60, 20));
        stopButton.addActionListener(e -> {
            Stop();
        });

        configPanel.add(startButton);
        configPanel.add(stopButton);

        configPanel.add(Box.createHorizontalStrut(20));
        configPanel.add(infoLabel);



        // 消息面板
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createTitledBorder("消息窗口"));

        messageTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        messagePanel.add(scrollPane, BorderLayout.CENTER);


        // 输入面板
        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(BorderFactory.createTitledBorder("输入窗口"));
        JButton sendButton = new JButton("发送");
        sendButton.setPreferredSize(new Dimension(60, 20));
        sendButton.addActionListener(e -> {
            sendMessage();
        });

        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        inputPanel.add(inputField);
        inputPanel.add(sendButton);


        // 拼装组件
        add(configPanel, BorderLayout.NORTH);
        add(messagePanel, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }

    @Override
    public void onMessageReceived(String message) {
        SwingUtilities.invokeLater(() -> messageTextArea.append(message + "\n"));
    }
}
