package Util;

import javax.sound.sampled.*;
import java.io.File;
import java.io.BufferedInputStream;
import java.util.List;
import java.net.URL;
import java.util.concurrent.CopyOnWriteArrayList;

public class SoundPlayer {

    private static final List<Clip> ACTIVE_CLIPS = new CopyOnWriteArrayList<>();

    public static void play(String fileName) {
        try {
            URL url = resolveSound(fileName);
            if (url == null) return;

            AudioInputStream audio = AudioSystem.getAudioInputStream(new BufferedInputStream(url.openStream()));
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    ACTIVE_CLIPS.remove(clip);
                }
            });
            ACTIVE_CLIPS.add(clip);
            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static URL resolveSound(String fileName) {
        URL url = SoundPlayer.class.getResource("/Assets/Sounds/" + fileName);
        if (url != null) {
            return url;
        }

        File file = new File("Chess Game\\Assets\\Sounds", fileName);
        if (!file.exists()) {
            return null;
        }

        try {
            return file.toURI().toURL();
        } catch (Exception exception) {
            return null;
        }
    }

}
