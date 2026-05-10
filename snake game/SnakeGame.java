import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;

class GameFrame extends JFrame {

    GameFrame(){
        this.add(new GamePanel());
        this.setTitle("Snake Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);   // this will make the window appear in the middle of the screen
    }

}

final class GamePanel extends JPanel implements ActionListener {

    static final int screenWidth = 600;
    static final int screenHeight = 600;
    static final int unitSize = 25;
    static final int gameUnits = (screenWidth * screenHeight) / unitSize;
    int delay = 75;
    static final int easy = 120;
    static final int medium = 90;
    static final int hard = 50;

    final int x[] = new int[gameUnits];
    final int y[] = new int[gameUnits];
    int bodyParts = 6;
    int foodEaten = 0;
    int foodX;
    int foodY;
    char direction = 'R';
    boolean running = false;
    boolean initialScreen = false;
    boolean gameOverPlayed = false;
    Timer timer;
    Random random;
    BufferedImage backgroundimage;
    Clip backgroundmusic;
    Clip bite;
    Clip gameOver;

    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        try {
            backgroundimage = ImageIO.read(getClass().getResource("bg.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        difficultySelection();
    }

    public void playMusic(String musicFile) {
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(getClass().getResource(musicFile));
            backgroundmusic = AudioSystem.getClip();
            backgroundmusic.open(audio);
            backgroundmusic.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundmusic.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void biteSound (String soundFile){
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(getClass().getResource(soundFile));
            bite = AudioSystem.getClip();
            bite.open(audio);
            bite.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gameOverSound(String sound) {
        try {
            AudioInputStream audio1 = AudioSystem.getAudioInputStream(getClass().getResource(sound));
            gameOver = AudioSystem.getClip();
            gameOver.open(audio1);
            gameOver.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void difficultySelection() {
        initialScreen = true;
        running = false;
        repaint();
    }

    public void startGame() {

        if (timer != null){
            timer.stop();
        }

        initialScreen = false;
        playMusic("bgmusic.wav");
        newFood();
        running = true;
        timer = new Timer(delay, this);
        timer.start();
    }

    public void restartGame() {
        bodyParts = 6;
        foodEaten = 0;
        direction = 'R';
        gameOverPlayed = false;

        for (int i = 0; i < x.length; i++){
            x[i] = 0;
            y[i] = 0;
        }
        startGame();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundimage != null) {
            g.drawImage(backgroundimage, 0, 0, screenWidth, screenHeight, this);
        }
        draw(g);
    }

    public void draw(Graphics g) {
        if (initialScreen){
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics fontMetrices = getFontMetrics(g.getFont());
            int levelY = 150;
            g.drawString("Choose Difficulty", (screenWidth - fontMetrices.stringWidth("Choose Difficulty")) / 2, levelY);

            g.setFont(new Font("Calibri", Font.BOLD, 35));
            g.drawString("1 - Easy", screenWidth/2 - 70, levelY + 100);
            g.drawString("2 - Medium", screenWidth/2 - 90, levelY + 150);
            g.drawString("3 - Hard", screenWidth/2 - 70, levelY + 200);

            return;
        }

        if (running) {
            /*
            for (int i = 0; i < screenHeight / unitSize; i++) {
                
                // To visualize the unit size of the game.
                g.drawLine(i * unitSize, 0 , i * unitSize, screenHeight);
                g.drawLine(0 , i * unitSize , screenHeight, i * unitSize);
            }
            */
            g.setColor(Color.red);
            g.fillOval(foodX, foodY, unitSize, unitSize);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], unitSize, unitSize);
                }
                else {
                    g.setColor(new Color(45, 180, 0));
                    // To make the snake multi-colored
                    // g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                    g.fillRect(x[i], y[i], unitSize, unitSize);
                }
            }
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrices1 = getFontMetrics(g.getFont());
            int scoreY = 40;
            g.drawString("Score: " + foodEaten, (screenWidth - metrices1.stringWidth("Score: " + foodEaten)) / 2, scoreY);

            g.setColor(Color.BLACK);
            g.setFont(new Font("Calibri", Font.BOLD, 25));
            FontMetrics metrices2 = getFontMetrics(g.getFont());
            int pauseY = scoreY + 25;
            g.drawString("P = pause , R = Resume" , (screenWidth - metrices2.stringWidth("P = pause , R = Resume")) / 2, pauseY);

        }
        else {
            gameOver(g);
        }
    }

    public void newFood() {
        foodX  = random.nextInt((int)(screenWidth / unitSize)) * unitSize;
        foodY  = random.nextInt((int)(screenHeight / unitSize)) * unitSize;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--){
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        switch (direction) {
            case 'U':
                y[0] = y[0] - unitSize;
                break;
            case 'D':
                y[0] = y[0] + unitSize;
                break;
            case 'L':
                x[0] = x[0] - unitSize;
                break;
            case 'R':
                x[0] = x[0] + unitSize;
                break; 
        }
    }

    public void checkFood() {
        if ((x[0] == foodX) && (y[0] == foodY)) {
            bodyParts++;
            foodEaten++;
            biteSound("bite.wav");
            newFood();
        }
    }

    public void checkCollision() {
        // checks if head collides with body
        for (int i = bodyParts; i > 0; i--){
            if ((x[0] == x[i]) && (y[0] == y[i])){
                running = false;
            }
        }
        // checks if head touches left border
        if (x[0] < 0) {
            running = false;
        }
        // checks if head touches right border
        if (x[0] >= screenWidth) {
            running = false;
        }
        // checks if head touches top border
        if (y[0] < 0) {
            running = false;
        }
        // checks if head touches bottom border
        if (y[0] >= screenHeight) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        // Score
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrices1 = getFontMetrics(g.getFont());
        int scoreY = 40;
        g.drawString("Score: " + foodEaten, (screenWidth - metrices1.stringWidth("Score: " + foodEaten)) / 2, scoreY);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Calibri", Font.BOLD, 25));
        FontMetrics metrices2 = getFontMetrics(g.getFont());
        int againY = scoreY + 25;
        g.drawString("Press \'Enter\' to restart the game", (screenWidth - metrices2.stringWidth("Press \'Enter\' to restart the game")) / 2, againY);

        // Game Over text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrices3 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (screenWidth - metrices3.stringWidth("Game Over")) / 2, screenHeight / 2);
        if (!gameOverPlayed) {
            backgroundmusic.stop();
            gameOverSound("gameOver.wav");
            gameOverPlayed = true;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFood();
            checkCollision();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e){
            switch(e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (initialScreen)
                        break;
                    if(direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (initialScreen)
                        break;
                    if(direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (initialScreen)
                        break;
                    if(direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (initialScreen)
                        break;
                    if(direction != 'U') {
                        direction = 'D';
                    }
                    break;
                // Restart Game
                case KeyEvent.VK_ENTER:
                    if(!running) {
                        // GameFrame frame = new GameFrame();
                        restartGame();
                    }
                    break;
                // Pause Game
                case KeyEvent.VK_P:
                    if (running){
                        timer.stop();
                        backgroundmusic.stop();
                    }
                    break;
                // Resume Game
                case KeyEvent.VK_R:
                    if(running) {
                        timer.start();
                        backgroundmusic.start();
                    }
                    break;
                case KeyEvent.VK_1:
                    delay = easy;
                    startGame();
                    break;
                case KeyEvent.VK_2:
                    delay = medium;
                    startGame();
                    break;
                case KeyEvent.VK_3:
                    delay = hard;
                    startGame();
                    break;
            }
        }
    }
}

public class SnakeGame {
    public static void main(String args []) {

        new GameFrame();

    }
}