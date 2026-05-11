package UI.Panels;

import AI.ChessAi;
import Game.GameMode;
import Game.GameState;
import UI.GameFrame;

import javax.swing.*;
import java.awt.*;

public class ModeSelectPanel extends JPanel {

    public ModeSelectPanel(GameFrame frame) {
        setLayout(new GridBagLayout());
        setBackground(new Color(30, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Select Game Mode", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(Color.WHITE);

        JLabel difficultyLabel = new JLabel("AI Difficulty");
        difficultyLabel.setFont(new Font("Arial", Font.BOLD, 16));
        difficultyLabel.setForeground(Color.WHITE);

        JComboBox<ChessAi.Difficulty> difficultyBox = new JComboBox<>(ChessAi.Difficulty.values());
        difficultyBox.setSelectedItem(frame.getSelectedDifficulty());
        difficultyBox.setFont(new Font("Arial", Font.PLAIN, 15));

        JButton singleBtn = createStyledButton("Single Player");
        JButton twoBtn = createStyledButton("Two Player");
        JButton backBtn = createSecondaryButton("Back");

        singleBtn.addActionListener(e -> {
            frame.setSelectedDifficulty((ChessAi.Difficulty) difficultyBox.getSelectedItem());
            frame.setGameMode(GameMode.SINGLE_PLAYER);
            frame.switchState(GameState.PLAYING);
        });

        twoBtn.addActionListener(e -> {
            frame.setGameMode(GameMode.TWO_PLAYER);
            frame.switchState(GameState.PLAYING);
        });

        backBtn.addActionListener(e -> frame.switchState(GameState.START));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(difficultyLabel, gbc);

        gbc.gridx = 1;
        add(difficultyBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(singleBtn, gbc);

        gbc.gridy = 3;
        add(twoBtn, gbc);

        gbc.gridy = 4;
        add(backBtn, gbc);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(240, 50));
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(new Color(60, 179, 113));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(90, 210, 140));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(60, 179, 113));
            }
        });
        return btn;
    }

    private JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(240, 46));
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 15));
        btn.setBackground(new Color(83, 92, 104));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
