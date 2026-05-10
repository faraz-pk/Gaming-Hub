package Util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

public class ImageLoader {

    public static ImageIcon load(String path) {
        URL url = ImageLoader.class.getResource(path);
        if (url != null) {
            return new ImageIcon(url);
        }

        String normalized = path.startsWith("/") ? path.substring(1) : path;
        File file = new File("Chess Game", normalized);
        if (file.exists()) {
            return new ImageIcon(file.getAbsolutePath());
        }

        return createPlaceholderIcon();
    }

    public static ImageIcon loadScaled(String path, int width, int height) {
        ImageIcon original = load(path);
        Image scaled = original.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private static ImageIcon createPlaceholderIcon() {
        BufferedImage image = new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(new Color(210, 210, 210));
        graphics.fillRect(0, 0, 60, 60);
        graphics.setColor(new Color(90, 90, 90));
        graphics.drawRect(0, 0, 59, 59);
        graphics.drawLine(0, 0, 59, 59);
        graphics.drawLine(59, 0, 0, 59);
        graphics.dispose();
        return new ImageIcon(image);
    }
}
