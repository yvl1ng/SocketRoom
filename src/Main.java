import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        App app = new App("内网通", 800, 600);
        SwingUtilities.invokeLater(() -> {
            app.setVisible(true);
        });
    }
}