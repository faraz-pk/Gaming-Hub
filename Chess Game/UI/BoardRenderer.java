package UI;

import javax.swing.*;
import java.awt.*;

public class BoardRenderer {

    public static void styleTile(JPanel tile, int row, int col) {

        if ((row + col) % 2 == 0) {
            tile.setBackground(Color.WHITE);
        } else {
            tile.setBackground(Color.GRAY);
        }
    }
}