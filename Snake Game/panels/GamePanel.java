package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JPanel;
import javax.swing.Timer;

public final class GamePanel extends JPanel implements ActionListener {
    final int[] x = new int[576];
    final int[] y = new int[576];
    int bodyParts = 6;
    int foodEaten = 0;
    int foodX;
    int foodY;
    char direction = 'R';
    boolean running = false;
    boolean initialScreen = false;
    boolean paused = false;
    boolean gameOverPlayed = false;
    Timer timer;
    Random random = new Random();
    BufferedImage backgroundimage;
    Clip backgroundmusic;
    Clip bite;
    Clip gameOver;
    private int delay;

    GamePanel() {
        this.setPreferredSize(new Dimension(600, 600));

        try {
            this.backgroundimage = ImageIO.read(Objects.requireNonNull(this.getClass().getResource("/assets/bg.png")));
        } catch (IOException var2) {
            var2.printStackTrace();
        }

        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        this.difficultySelection();
    }

    public void playMusic(String var1) {
        try {
            AudioInputStream var2 = AudioSystem.getAudioInputStream(Objects.requireNonNull(this.getClass().getResource(var1)));
            this.backgroundmusic = AudioSystem.getClip();
            this.backgroundmusic.open(var2);
            this.backgroundmusic.loop(Clip.LOOP_CONTINUOUSLY);
            this.backgroundmusic.start();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public void biteSound(String var1) {
        try {
            AudioInputStream var2 = AudioSystem.getAudioInputStream(Objects.requireNonNull(this.getClass().getResource(var1)));
            this.bite = AudioSystem.getClip();
            this.bite.open(var2);
            this.bite.start();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public void gameOverSound(String var1) {
        try {
            AudioInputStream var2 = AudioSystem.getAudioInputStream(Objects.requireNonNull(this.getClass().getResource(var1)));
            this.gameOver = AudioSystem.getClip();
            this.gameOver.open(var2);
            this.gameOver.start();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public void difficultySelection() {
        this.initialScreen = true;
        this.running = false;
        this.repaint();
    }

    public void startGame() {
        if (this.timer != null) {
            this.timer.stop();
        }

        this.initialScreen = false;
        this.playMusic("/assets/bgmusic.wav");
        this.newFood();
        this.running = true;
        this.timer = new Timer(this.delay, this);
        this.timer.start();
    }

    public void restartGame() {
        this.bodyParts = 6;
        this.foodEaten = 0;
        this.direction = 'R';
        this.gameOverPlayed = false;

        for(int var1 = 0; var1 < this.x.length; ++var1) {
            this.x[var1] = 0;
            this.y[var1] = 0;
        }

        if (backgroundmusic != null) {
            backgroundmusic.stop();
            backgroundmusic.close();
        }
        this.startGame();
    }

    public void paintComponent(Graphics var1) {
        super.paintComponent(var1);
        if (this.backgroundimage != null) {
            var1.drawImage(this.backgroundimage, 0, 0, 600, 600, this);
        }

        this.draw(var1);
    }

    public void draw(Graphics var1) {
        if (this.initialScreen) {
            var1.setColor(Color.red);
            var1.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics var7 = this.getFontMetrics(var1.getFont());
            short var8 = 150;
            var1.drawString("Choose Difficulty", (600 - var7.stringWidth("Choose Difficulty")) / 2, var8);
            var1.setFont(new Font("Calibri", Font.BOLD, 35));
            var1.drawString("1 - Easy", 230, var8 + 100);
            var1.drawString("2 - Medium", 210, var8 + 150);
            var1.drawString("3 - Hard", 230, var8 + 200);
        } else {
            if (this.running) {
                var1.setColor(Color.red);
                var1.fillOval(this.foodX, this.foodY, 25, 25);

                for(int var2 = 0; var2 < this.bodyParts; ++var2) {
                    if (var2 == 0) {
                        var1.setColor(Color.green);
                        var1.fillRect(this.x[var2], this.y[var2], 25, 25);
                    } else {
                        var1.setColor(new Color(45, 180, 0));
                        var1.fillRect(this.x[var2], this.y[var2], 25, 25);
                    }
                }

                var1.setColor(Color.red);
                var1.setFont(new Font("Ink Free", Font.BOLD, 40));
                FontMetrics var6 = this.getFontMetrics(var1.getFont());
                byte var3 = 40;
                var1.drawString("Score: " + this.foodEaten, (600 - var6.stringWidth("Score: " + this.foodEaten)) / 2, var3);
                var1.setColor(Color.BLACK);
                var1.setFont(new Font("Calibri", Font.BOLD, 25));
                FontMetrics var4 = this.getFontMetrics(var1.getFont());
                int var5 = var3 + 25;
                var1.drawString("P = pause , R = Resume", (600 - var4.stringWidth("P = pause , R = Resume")) / 2, var5);
            } else {
                this.gameOver(var1);
            }

        }
    }

    public void newFood() {
        this.foodX = this.random.nextInt(24) * 25;
        this.foodY = this.random.nextInt(24) * 25;
    }

    public void move() {
        for(int var1 = this.bodyParts; var1 > 0; --var1) {
            this.x[var1] = this.x[var1 - 1];
            this.y[var1] = this.y[var1 - 1];
        }

        switch (this.direction) {
            case 'D' -> this.y[0] += 25;
            case 'L' -> this.x[0] -= 25;
            case 'R' -> this.x[0] += 25;
            case 'U' -> this.y[0] -= 25;
        }

    }

    public void checkFood() {
        if (this.x[0] == this.foodX && this.y[0] == this.foodY) {
            ++this.bodyParts;
            ++this.foodEaten;
            this.biteSound("/assets/bite.wav");
            this.newFood();
        }

    }

    public void checkCollision() {
        for(int var1 = this.bodyParts; var1 > 0; --var1) {
            if (this.x[0] == this.x[var1] && this.y[0] == this.y[var1]) {
                this.running = false;
                break;
            }
        }

        if (this.x[0] < 0) {
            this.running = false;
        }

        if (this.x[0] >= 600) {
            this.running = false;
        }

        if (this.y[0] < 0) {
            this.running = false;
        }

        if (this.y[0] >= 600) {
            this.running = false;
        }

        if (!this.running) {
            this.timer.stop();
        }

    }

    public void gameOver(Graphics var1) {
        var1.setColor(Color.red);
        var1.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics var2 = this.getFontMetrics(var1.getFont());
        byte var3 = 40;
        var1.drawString("Score: " + this.foodEaten, (600 - var2.stringWidth("Score: " + this.foodEaten)) / 2, var3);
        var1.setColor(Color.BLACK);
        var1.setFont(new Font("Calibri", Font.BOLD, 25));
        FontMetrics var4 = this.getFontMetrics(var1.getFont());
        int var5 = var3 + 25;
        var1.drawString("Press 'Enter' to restart the game", (600 - var4.stringWidth("Press 'Enter' to restart the game")) / 2, var5);
        var1.setColor(Color.red);
        var1.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics var6 = this.getFontMetrics(var1.getFont());
        var1.drawString("Game Over", (600 - var6.stringWidth("Game Over")) / 2, 300);
        if (!this.gameOverPlayed) {
            this.backgroundmusic.stop();
            this.gameOverSound("/assets/gameOver.wav");
            this.gameOverPlayed = true;
        }

    }

    public void actionPerformed(ActionEvent var1) {
        if (this.running) {
            this.move();
            this.checkFood();
            this.checkCollision();
        }

        this.repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        public MyKeyAdapter() {
            super();
        }

        public void keyPressed(KeyEvent var1) {
            switch (var1.getKeyCode()) {
                case 10:
                    if (!GamePanel.this.running) {
                        GamePanel.this.restartGame();
                    }
                    break;
                case 37:
                    if (!GamePanel.this.initialScreen && GamePanel.this.direction != 'R') {
                        GamePanel.this.direction = 'L';
                    }
                    break;
                case 38:
                    if (!GamePanel.this.initialScreen && GamePanel.this.direction != 'D') {
                        GamePanel.this.direction = 'U';
                    }
                    break;
                case 39:
                    if (!GamePanel.this.initialScreen && GamePanel.this.direction != 'L') {
                        GamePanel.this.direction = 'R';
                    }
                    break;
                case 40:
                    if (!GamePanel.this.initialScreen && GamePanel.this.direction != 'U') {
                        GamePanel.this.direction = 'D';
                    }
                    break;
                case 49:
                    GamePanel.this.delay = 120;
                    GamePanel.this.startGame();
                    break;
                case 50:
                    GamePanel.this.delay = 90;
                    GamePanel.this.startGame();
                    break;
                case 51:
                    GamePanel.this.delay = 50;
                    GamePanel.this.startGame();
                    break;
                case 80:
                    if (running && !paused) {
                        paused = true;
                        timer.stop();
                        backgroundmusic.stop();
                    }
                    break;
                case 82:
                    if (running && paused) {
                        paused = false;
                        timer.start();
                        backgroundmusic.start();
                    }
                    break;
            }

        }
    }
}

