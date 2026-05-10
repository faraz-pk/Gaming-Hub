package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * NumberPadPanel.java
 * A row of digit buttons (1–9) plus an Erase button.
 * Allows mouse-based number entry in addition to keyboard.
 * OOP Concepts: Encapsulation, Event-driven design
 */
public class NumberPadPanel extends JPanel {

    public interface NumberPadListener {
        void onNumberSelected(int number); // 0 = erase
    }

    private NumberPadListener listener;

    public NumberPadPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 6, 0));
        setOpaque(false);
        buildButtons();
    }

    private void buildButtons() {
        for (int i = 1; i <= 9; i++) {
            final int num = i;
            JButton btn = createButton(String.valueOf(i));
            btn.addActionListener(_ -> {
                if (listener != null) listener.onNumberSelected(num);
            });
            add(btn);
        }

        JButton erase = createButton("⌫");
        erase.setForeground(new Color(200, 60, 60));
        erase.addActionListener(_ -> {
            if (listener != null) listener.onNumberSelected(0);
        });
        add(erase);
    }

    private JButton createButton(String label) {
        JButton btn = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bg = getModel().isPressed()
                        ? new Color(140, 180, 255)
                        : getModel().isRollover()
                          ? new Color(200, 220, 255)
                          : new Color(235, 240, 252);

                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                g2.setColor(new Color(30, 40, 90));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Segue UI", Font.BOLD, 18));
        btn.setForeground(new Color(30, 40, 90));
        btn.setPreferredSize(new Dimension(48, 48));
        btn.setFocusable(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void setNumberPadListener(NumberPadListener listener) {
        this.listener = listener;
    }
}
