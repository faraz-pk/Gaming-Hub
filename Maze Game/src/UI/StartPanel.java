package UI;

import Controller.GameController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StartPanel extends JPanel {

    private final GameController controller;

    public StartPanel(GameController controller) {
        this.controller = controller;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new GridBagLayout());
        setBackground(new Color(20, 20, 30)); // plain dark background

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("MAZE GAME");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 42));

        JLabel subtitleLabel = new JLabel("Find your way to the exit");
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setForeground(new Color(200, 200, 200));
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));

        JButton startButton = createStartButton();

        content.add(titleLabel);
        content.add(Box.createVerticalStrut(12));
        content.add(subtitleLabel);
        content.add(Box.createVerticalStrut(40));
        content.add(startButton);

        add(content);
    }

    private JButton createStartButton() {
        JButton button = new JButton("Start Maze");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusable(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 22));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(new Color(18, 18, 28));
        button.setBorder(BorderFactory.createEmptyBorder(14, 28, 14, 28));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // If later you add icon image:
        // button.setIcon(new ImageIcon("resources/maze_icon.png"));

        button.addActionListener(e -> controller.showDifficultyScreen());
        return button;
    }
}