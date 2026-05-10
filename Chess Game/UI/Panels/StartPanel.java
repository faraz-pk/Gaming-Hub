package UI.Panels;

import Game.GameState;
import UI.GameFrame;

import javax.swing.*;
import java.awt.*;

public class StartPanel extends JPanel {

    public StartPanel(GameFrame frame) {

        setLayout(new GridBagLayout());
        setBackground(new Color(30, 30, 40)); // dark background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // spacing

        JLabel title = new JLabel("Chess Game");
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.WHITE);

        JButton startBtn = createStyledButton("Start");
        JButton exitBtn = createStyledButton("Exit");

        startBtn.addActionListener(e ->
                frame.switchState(GameState.MODE_SELECT)
        );

        exitBtn.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
        });

        gbc.gridy = 0;
        add(title, gbc);

        gbc.gridy = 1;
        add(startBtn, gbc);

        gbc.gridy = 2;
        add(exitBtn, gbc);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);

        btn.setPreferredSize(new Dimension(200, 50));
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 16));

        btn.setBackground(new Color(70, 130, 180));
        btn.setForeground(Color.WHITE);

        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(100, 160, 210));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(70, 130, 180));
            }
        });

        return btn;
    }
}
