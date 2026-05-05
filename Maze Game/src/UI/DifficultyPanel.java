package UI;

import Controller.GameController;
import Logic.MazeGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DifficultyPanel extends JPanel {

    private final GameController controller;

    public DifficultyPanel(GameController controller) {
        this.controller = controller;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new GridBagLayout());
        setBackground(new Color(28, 28, 40));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Select Difficulty");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));

        JButton easyButton = createDifficultyButton("Easy", new Color(76, 175, 80));
        easyButton.addActionListener(e -> controller.startGame(MazeGenerator.Difficulty.EASY));

        JButton mediumButton = createDifficultyButton("Medium", new Color(255, 152, 0));
        mediumButton.addActionListener(e -> controller.startGame(MazeGenerator.Difficulty.MEDIUM));

        JButton hardButton = createDifficultyButton("Hard", new Color(244, 67, 54));
        hardButton.addActionListener(e -> controller.startGame(MazeGenerator.Difficulty.HARD));

        JButton backButton = createSecondaryButton("Back");
        backButton.addActionListener(e -> controller.showStartScreen());

        content.add(titleLabel);
        content.add(Box.createVerticalStrut(35));
        content.add(easyButton);
        content.add(Box.createVerticalStrut(16));
        content.add(mediumButton);
        content.add(Box.createVerticalStrut(16));
        content.add(hardButton);
        content.add(Box.createVerticalStrut(28));
        content.add(backButton);

        add(content);
    }

    private JButton createDifficultyButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusable(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 20));
        button.setBackground(bgColor);
        button.setForeground(new Color(18, 18, 28));
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(240, 52));
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusable(false);
        button.setFont(new Font("SansSerif", Font.PLAIN, 16));
        button.setBackground(new Color(90, 90, 110));
        button.setForeground(new Color(18, 18, 28));
        button.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(200, 44));
        return button;
    }
}