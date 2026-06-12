import ui.MainFrame;
import org.h2.tools.Server;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            Server.createWebServer("-webPort", "8082").start();
            System.out.println("H2 консоль: http://localhost:8082");
            System.out.println("JDBC URL: jdbc:h2:./bookstore");
            System.out.println("User: sa");
            System.out.println("Password:");
        } catch (Exception e) {
            System.out.println("Консоль не запущена: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}