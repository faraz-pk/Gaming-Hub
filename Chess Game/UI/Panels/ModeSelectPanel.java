package UI.Panels;

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
        gbc.insets = new Insets(15, 15, 15, 15);

        JLabel title = new JLabel("Select Game Mode");
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(Color.WHITE);

        JButton singleBtn = createStyledButton("Single Player");
        JButton twoBtn = createStyledButton("Two Player");

        singleBtn.addActionListener(e -> {
            frame.setGameMode(GameMode.SINGLE_PLAYER);
            frame.switchState(GameState.PLAYING);
        });

        twoBtn.addActionListener(e -> {
            frame.setGameMode(GameMode.TWO_PLAYER);
            frame.switchState(GameState.PLAYING);
        });

        gbc.gridy = 0;
        add(title, gbc);

        gbc.gridy = 1;
        add(singleBtn, gbc);

        gbc.gridy = 2;
        add(twoBtn, gbc);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);

        btn.setPreferredSize(new Dimension(220, 50));
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 16));

        btn.setBackground(new Color(60, 179, 113)); // green tone
        btn.setForeground(Color.WHITE);

        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
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
}