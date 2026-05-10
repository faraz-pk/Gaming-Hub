package UI;

import javax.swing.*;
import java.io.File;

public class ImageLoader {

    public static ImageIcon load(String path) {
        java.net.URL url = ImageLoader.class.getResource(path);
        if (url != null) {
            return new ImageIcon(url);
        }

        String normalized = path.startsWith("/") ? path.substring(1) : path;
        File file = new File("Uno Game", normalized);
        if (file.exists()) {
            return new ImageIcon(file.getAbsolutePath());
        }

        System.out.println("Image not found: " + path);
        return new ImageIcon();
    }
}
