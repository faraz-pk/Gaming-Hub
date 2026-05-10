package Util;

import javax.swing.*;

public class ImageLoader {

    public static ImageIcon load(String path) {
        return new ImageIcon(ImageLoader.class.getResource(path));
    }
}