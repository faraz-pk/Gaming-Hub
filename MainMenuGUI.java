import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainMenuGUI extends JFrame {

    private static final Color BACKGROUND = new Color(15, 23, 42);
    private static final Color PANEL = new Color(30, 41, 59);
    private static final Color ACCENT = new Color(56, 189, 248);
    private static final Color TEXT = new Color(226, 232, 240);
    private static final Color MUTED = new Color(148, 163, 184);

    public MainMenuGUI() {
        setTitle("Gaming Hub");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(buildContent());
        setSize(920, 640);
        setMinimumSize(new Dimension(820, 560));
        setLocationRelativeTo(null);
    }

    private JComponent buildContent() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(28, 28, 28, 28));

        JPanel centerPanel = new JPanel(new BorderLayout(0, 24));
        centerPanel.setBackground(PANEL);
        centerPanel.setBorder(new EmptyBorder(28, 28, 28, 28));
        centerPanel.setPreferredSize(new Dimension(720, 460));

        centerPanel.add(buildHeader(), BorderLayout.NORTH);
        centerPanel.add(buildGameGrid(), BorderLayout.CENTER);
        centerPanel.add(buildFooter(), BorderLayout.SOUTH);

        root.add(centerPanel);
        return root;
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Gaming Hub");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("Choose a game from the central panel and start playing.");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitle.setForeground(MUTED);

        header.add(title);
        header.add(Box.createVerticalStrut(8));
        header.add(subtitle);
        return header;
    }

    private JComponent buildGameGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 3, 18, 18));
        grid.setOpaque(false);

        grid.add(createGameButton("Chess", this::launchChess));
        grid.add(createGameButton("Snake", this::launchSnake));
        grid.add(createGameButton("Sudoku", this::launchSudoku));
        grid.add(createGameButton("Maze", this::launchMaze));
        grid.add(createGameButton("Tic Tac Toe", this::launchTicTacToe));
        grid.add(createGameButton("UNO", this::launchUno));

        return grid;
    }

    private JComponent buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        footer.setOpaque(false);

        JButton exitButton = new JButton("Exit Hub");
        exitButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        exitButton.setBackground(ACCENT);
        exitButton.setForeground(new Color(8, 15, 30));
        exitButton.setFocusPainted(false);
        exitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        exitButton.addActionListener(event -> dispose());

        footer.add(exitButton);
        return footer;
    }

    private JButton createGameButton(String gameName, Runnable launcher) {
        JButton button = new JButton("<html><center>" + gameName + "</center></html>");
        button.setFont(new Font("SansSerif", Font.BOLD, 20));
        button.setBackground(new Color(51, 65, 85));
        button.setForeground(TEXT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT, 2),
                new EmptyBorder(24, 12, 24, 12)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(event -> launcher.run());
        return button;
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
}
