import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

public class MainMenuGUI extends JFrame {

    private static final Color BACKGROUND_TOP = new Color(7, 15, 30);
    private static final Color BACKGROUND_BOTTOM = new Color(20, 33, 61);
    private static final Color SHELL = new Color(13, 21, 38, 230);
    private static final Color CARD = new Color(31, 44, 72);
    private static final Color CARD_HOVER = new Color(43, 61, 98);
    private static final Color ACCENT = new Color(72, 187, 255);
    private static final Color ACCENT_SOFT = new Color(110, 231, 255, 70);
    private static final Color TEXT = new Color(241, 245, 249);
    private static final Color MUTED = new Color(180, 193, 214);
    private static final Color BUTTON_TEXT = new Color(7, 18, 33);

    public MainMenuGUI() {
        setTitle("Gaming Hub");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(buildContent());
        setSize(1100, 720);
        setMinimumSize(new Dimension(940, 620));
        setLocationRelativeTo(null);
    }

    private JComponent buildContent() {
        GradientPanel root = new GradientPanel();
        root.setLayout(new GridBagLayout());
        root.setBorder(new EmptyBorder(26, 26, 26, 26));

        RoundedPanel shell = new RoundedPanel(36, SHELL);
        shell.setLayout(new BorderLayout(0, 26));
        shell.setBorder(new EmptyBorder(28, 30, 28, 30));
        shell.setPreferredSize(new Dimension(930, 590));

        shell.add(buildHeader(), BorderLayout.NORTH);
        shell.add(buildBody(), BorderLayout.CENTER);
        shell.add(buildFooter(), BorderLayout.SOUTH);

        root.add(shell);
        return root;
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout(18, 0));
        header.setOpaque(false);

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));

        JLabel eyebrow = new JLabel("ARCADE COLLECTION");
        eyebrow.setFont(new Font("SansSerif", Font.BOLD, 13));
        eyebrow.setForeground(ACCENT);
        eyebrow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Gaming Hub");
        title.setFont(new Font("Serif", Font.BOLD, 42));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleBlock.add(eyebrow);
        titleBlock.add(Box.createVerticalStrut(10));
        titleBlock.add(title);

        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        badgeRow.setOpaque(false);
        badgeRow.add(createBadge("6 Games"));
        badgeRow.add(createBadge("Desktop Arcade"));
        badgeRow.add(createBadge("One Click Launch"));

        header.add(titleBlock, BorderLayout.CENTER);
        header.add(badgeRow, BorderLayout.EAST);
        return header;
    }

    private JComponent buildBody() {
        JPanel body = new JPanel(new BorderLayout(0, 18));
        body.setOpaque(false);

        body.add(buildSpotlight(), BorderLayout.NORTH);
        body.add(buildGameGrid(), BorderLayout.CENTER);
        return body;
    }

    private JComponent buildSpotlight() {
        RoundedPanel spotlight = new RoundedPanel(28, new Color(20, 34, 58, 220));
        spotlight.setLayout(new BorderLayout(18, 0));
        spotlight.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel copy = new JPanel();
        copy.setOpaque(false);
        copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));

        JLabel spotlightTitle = new JLabel("Pick your next challenge");
        spotlightTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        spotlightTitle.setForeground(TEXT);
        spotlightTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel spotlightText = new JLabel(
                "<html>From tactical matches in Chess to fast reflex runs in Snake, every tile opens a focused mini-game.</html>"
        );
        spotlightText.setFont(new Font("SansSerif", Font.PLAIN, 15));
        spotlightText.setForeground(MUTED);
        spotlightText.setAlignmentX(Component.LEFT_ALIGNMENT);

        copy.add(spotlightTitle);
        copy.add(Box.createVerticalStrut(8));
        copy.add(spotlightText);

        JPanel right = new JPanel(new GridLayout(1, 3, 10, 0));
        right.setOpaque(false);
        right.add(createMiniStat("Strategy", "Chess, Sudoku"));
        right.add(createMiniStat("Action", "Snake, Maze"));
        right.add(createMiniStat("Party", "UNO, Tic Tac Toe"));

        spotlight.add(copy, BorderLayout.CENTER);
        spotlight.add(right, BorderLayout.EAST);
        return spotlight;
    }

    private JComponent buildGameGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 3, 18, 22));
        grid.setOpaque(false);

        grid.add(createGameButton("Chess", "chess.png", "Plan sharp moves against a friend or the AI.", "Strategy Duel", this::launchChess));
        grid.add(createGameButton("Snake", "snake.png", "Survive longer, move cleaner, and chase high scores.", "Arcade Sprint", this::launchSnake));
        grid.add(createGameButton("Sudoku", "sudoku.png", "Slow down and solve number logic with focus.", "Puzzle Calm", this::launchSudoku));
        grid.add(createGameButton("Maze", "maze.png", "Navigate twisting paths and race to the exit.", "Adventure Run", this::launchMaze));
        grid.add(createGameButton("Tic Tac Toe", "tictactoe.png", "Quick matches with instant replay value.", "Classic Match", this::launchTicTacToe));
        grid.add(createGameButton("UNO", "uno.png", "Bring in colorful chaos and party-game energy.", "Party Cards", this::launchUno));

        return grid;
    }

    private JComponent buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        JLabel hint = new JLabel("Tip: every game opens in its own window, so you can return to the hub anytime.");
        hint.setFont(new Font("SansSerif", Font.PLAIN, 14));
        hint.setForeground(MUTED);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actions.setOpaque(false);

        JButton exitButton = createFooterButton("Exit Hub", ACCENT, BUTTON_TEXT);
        exitButton.addActionListener(event -> dispose());

        actions.add(exitButton);
        footer.add(hint, BorderLayout.WEST);
        footer.add(actions, BorderLayout.EAST);
        return footer;
    }

    private JComponent createBadge(String text) {
        RoundedPanel badge = new RoundedPanel(18, new Color(255, 255, 255, 18));
        badge.setLayout(new GridBagLayout());
        badge.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(TEXT);
        badge.add(label);
        return badge;
    }

    private JComponent createMiniStat(String heading, String detail) {
        RoundedPanel stat = new RoundedPanel(22, new Color(255, 255, 255, 18));
        stat.setLayout(new BoxLayout(stat, BoxLayout.Y_AXIS));
        stat.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel headingLabel = new JLabel(heading);
        headingLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        headingLabel.setForeground(TEXT);
        headingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel detailLabel = new JLabel("<html>" + detail + "</html>");
        detailLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        detailLabel.setForeground(MUTED);
        detailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        stat.add(headingLabel);
        stat.add(Box.createVerticalStrut(4));
        stat.add(detailLabel);
        return stat;
    }

    private JButton createGameButton(String gameName, String imageName, String description, String tag, Runnable launcher) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setMargin(new Insets(0, 0, 0, 0));

        RoundedPanel cardFace = new RoundedPanel(28, CARD);
        cardFace.setLayout(new BorderLayout(16, 0));
        cardFace.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 18), 1),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JLabel imageLabel = new JLabel(loadGameIcon(imageName, 88, 88));
        imageLabel.setVerticalAlignment(SwingConstants.TOP);
        imageLabel.setBorder(new EmptyBorder(0, 0, 0, 2));

        JPanel textColumn = new JPanel();
        textColumn.setOpaque(false);
        textColumn.setLayout(new BoxLayout(textColumn, BoxLayout.Y_AXIS));

        JLabel gameLabel = new JLabel(gameName);
        gameLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        gameLabel.setForeground(TEXT);
        gameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagLabel = new JLabel(tag);
        tagLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        tagLabel.setForeground(ACCENT);
        tagLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descriptionLabel = new JLabel("<html><div style='width:240px;'>" + description + "</div></html>");
        descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        descriptionLabel.setForeground(MUTED);
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottom.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JLabel playLabel = new JLabel("Launch Game");
        playLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        playLabel.setForeground(TEXT);

        JPanel accentBar = new JPanel();
        accentBar.setPreferredSize(new Dimension(0, 10));
        accentBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        accentBar.setBackground(ACCENT_SOFT);

        bottom.add(playLabel, BorderLayout.WEST);
        bottom.add(accentBar, BorderLayout.SOUTH);

        textColumn.add(gameLabel);
        textColumn.add(Box.createVerticalStrut(6));
        textColumn.add(tagLabel);
        textColumn.add(Box.createVerticalStrut(8));
        textColumn.add(descriptionLabel);
        textColumn.add(Box.createVerticalGlue());
        textColumn.add(bottom);

        cardFace.add(imageLabel, BorderLayout.WEST);
        cardFace.add(textColumn, BorderLayout.CENTER);

        button.add(cardFace, BorderLayout.CENTER);
        button.addActionListener(event -> launcher.run());
        installCardHover(button, cardFace);
        return button;
    }

    private ImageIcon loadGameIcon(String imageName, int width, int height) {
        URL resource = MainMenuGUI.class.getResource("/Assets/" + imageName);
        ImageIcon icon;

        if (resource != null) {
            icon = new ImageIcon(resource);
        } else {
            File file = new File("Assets", imageName);
            if (!file.exists()) {
                return new ImageIcon(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
            }
            icon = new ImageIcon(file.getAbsolutePath());
        }

        Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private JButton createFooterButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 20, 12, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void installCardHover(JButton button, RoundedPanel cardFace) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                cardFace.setFill(CARD_HOVER);
                cardFace.repaint();
            }

            @Override
            public void mouseExited(MouseEvent event) {
                cardFace.setFill(CARD);
                cardFace.repaint();
            }
        });
    }

    private void launchChess() {
        launchFrame("UI.GameFrame");
    }

    private void launchSnake() {
        launchFrame("panels.GameFrame");
    }

    private void launchSudoku() {
        launchFrame("UI.SudokuFrame");
    }

    private void launchTicTacToe() {
        launchFrame("TicTacToe");
    }

    private void launchUno() {
        launchFrame("UI.Menu");
    }

    private void launchMaze() {
        try {
            Class<?> controllerClass = Class.forName("Controller.GameController");
            Object controller = controllerClass.getDeclaredConstructor().newInstance();

            Class<?> frameClass = Class.forName("UI.MainFrame");
            Constructor<?> frameConstructor = frameClass.getDeclaredConstructor(controllerClass);
            Object frame = frameConstructor.newInstance(controller);

            Method setMainFrame = controllerClass.getMethod("setMainFrame", frameClass);
            setMainFrame.invoke(controller, frame);

            Method setVisible = frameClass.getMethod("setVisible", boolean.class);
            setVisible.invoke(frame, true);

            Method showStartScreen = controllerClass.getMethod("showStartScreen");
            showStartScreen.invoke(controller);
        } catch (ReflectiveOperationException exception) {
            showLaunchError("Maze", exception);
        }
    }

    private void launchFrame(String className) {
        try {
            Class<?> frameClass = Class.forName(className);
            Object instance = frameClass.getDeclaredConstructor().newInstance();

            if (instance instanceof Window) {
                ((Window) instance).setVisible(true);
            } else {
                Method setVisible = frameClass.getMethod("setVisible", boolean.class);
                setVisible.invoke(instance, true);
            }
        } catch (ReflectiveOperationException exception) {
            showLaunchError(className, exception);
        } catch (RuntimeException exception) {
            showLaunchError(className, exception);
        }
    }

    private void showLaunchError(String gameName, Exception exception) {
        Throwable root = exception;
        if (exception instanceof InvocationTargetException && ((InvocationTargetException) exception).getCause() != null) {
            root = ((InvocationTargetException) exception).getCause();
        }

        String message = root.getMessage();
        if (message == null || message.isBlank()) {
            message = root.getClass().getSimpleName();
        }

        JOptionPane.showMessageDialog(
                this,
                "Could not launch " + gameName + ".\n" + message,
                "Launch Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenuGUI().setVisible(true));
    }

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gradient = new GradientPaint(
                    0, 0, BACKGROUND_TOP,
                    0, getHeight(), BACKGROUND_BOTTOM
            );
            g2.setPaint(gradient);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(new Color(255, 255, 255, 22));
            g2.fillOval(-80, -40, 260, 260);
            g2.setColor(new Color(72, 187, 255, 28));
            g2.fillOval(getWidth() - 260, 30, 220, 220);
            g2.setColor(new Color(110, 231, 255, 20));
            g2.fillOval(getWidth() / 2 - 120, getHeight() - 150, 240, 240);
            g2.dispose();
        }
    }

    private static class RoundedPanel extends JPanel {
        private final int arc;
        private Color fill;

        RoundedPanel(int arc, Color fill) {
            this.arc = arc;
            this.fill = fill;
            setOpaque(false);
        }

        void setFill(Color fill) {
            this.fill = fill;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.dispose();
            super.paintComponent(graphics);
        }
    }
}
