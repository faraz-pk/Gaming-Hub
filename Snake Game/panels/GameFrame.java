package panels;
import javax.swing.*;

public class GameFrame extends JFrame {
    public GameFrame() {
        GamePanel panel = new GamePanel();
        this.add(panel);

        this.setTitle("Snake Game");
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        panel.requestFocusInWindow();
    }
}
