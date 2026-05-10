package Uno;

import javax.swing.*;

public class ImageLoader {

    public static ImageIcon load(String path) {

        java.net.URL url =
                ImageLoader.class.getResource(path);

        if(url == null){
            System.out.println("Image not found: " + path);
            return null;
        }

        return new ImageIcon(url);
    }
}