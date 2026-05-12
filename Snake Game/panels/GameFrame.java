package panels;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameFrame extends JFrame {
    public GameFrame() {
        GamePanel panel = new GamePanel();
        this.add(panel);

        this.setTitle("Snake Game");
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                panel.shutdown();
            }

            @Override
            public void windowClosed(WindowEvent event) {
                panel.shutdown();
            }
        });
        this.setVisible(true);

        panel.requestFocusInWindow();
    }
}
