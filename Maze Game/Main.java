import Controller.GameController;
import UI.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // fallback to default Look & Feel
            }

            GameController controller = new GameController();
            MainFrame frame = new MainFrame(controller);
            controller.setMainFrame(frame);

            frame.setVisible(true);
            controller.showStartScreen();
        });
    }
}