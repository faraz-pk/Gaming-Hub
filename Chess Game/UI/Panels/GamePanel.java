package UI.Panels;

import AI.ChessAi;
import Game.GameMode;
import Game.GameState;
import Logic.CheckDetector;
import Logic.GameController;
import Model.Board;
import Model.Move;
import Model.Tile;
import Pieces.Piece;
import UI.GameFrame;
import UI.TilePanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GamePanel extends JPanel {

    private final GameFrame frame;
    private GameController controller;
    private ChessAi ai;
    private final JPanel boardPanel;
    private final JLabel statusLabel;
    private final JLabel modeLabel;
    private final DefaultListModel<String> moveHistoryModel;
    private final JList<String> moveHistoryList;
    private boolean aiThinking = false;
    private Timer aiTimer;
    private SwingWorker<Move, Void> aiWorker;

    public GamePanel(GameFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout(18, 18));
        setBackground(new Color(24, 26, 32));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        boardPanel = new JPanel(new GridLayout(8, 8));
        boardPanel.setBorder(BorderFactory.createLineBorder(new Color(58, 62, 74), 2));

        statusLabel = new JLabel("White to move");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 22));
        statusLabel.setForeground(Color.WHITE);

        modeLabel = new JLabel();
        modeLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        modeLabel.setForeground(new Color(210, 214, 220));

        moveHistoryModel = new DefaultListModel<>();
        moveHistoryList = new JList<>(moveHistoryModel);
        moveHistoryList.setBackground(new Color(40, 44, 52));
        moveHistoryList.setForeground(Color.WHITE);
        moveHistoryList.setFont(new Font("Consolas", Font.PLAIN, 14));

        add(createTopBar(), BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(createSidebar(), BorderLayout.EAST);
    }

    public void startGame(GameMode mode) {
        controller = new GameController();
        ai = mode == GameMode.SINGLE_PLAYER ? new ChessAi(frame.getSelectedDifficulty()) : null;
        stopAiTimer();
        stopAiWorker();
        aiThinking = false;
        moveHistoryModel.clear();
        modeLabel.setText(buildModeLabel(mode));

        boardPanel.removeAll();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int currentRow = row;
                int currentCol = col;
                TilePanel tilePanel = new TilePanel(row, col);
                tilePanel.setLayout(new BorderLayout());
                tilePanel.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        handleBoardClick(currentRow, currentCol);
                    }
                });
                boardPanel.add(tilePanel);
            }
        }

        refreshBoard();
        revalidate();
        repaint();
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JPanel labelsPanel = new JPanel(new GridLayout(2, 1));
        labelsPanel.setOpaque(false);
        labelsPanel.add(statusLabel);
        labelsPanel.add(modeLabel);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionsPanel.setOpaque(false);

        JButton restartButton = createActionButton("Restart");
        restartButton.addActionListener(e -> restartGame());

        JButton menuButton = createActionButton("Main Menu");
        menuButton.addActionListener(e -> {
            stopAiTimer();
            stopAiWorker();
            aiThinking = false;
            frame.switchState(GameState.START);
        });

        actionsPanel.add(restartButton);
        actionsPanel.add(menuButton);

        topBar.add(labelsPanel, BorderLayout.WEST);
        topBar.add(actionsPanel, BorderLayout.EAST);
        return topBar;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout(0, 10));
        sidebar.setPreferredSize(new Dimension(300, 0));
        sidebar.setBackground(new Color(33, 37, 45));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(58, 62, 74), 1),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        JLabel historyTitle = new JLabel("Move History");
        historyTitle.setFont(new Font("Arial", Font.BOLD, 18));
        historyTitle.setForeground(Color.WHITE);

        JTextArea tipsArea = new JTextArea(
                "Highlights:\n" +
                "- Blue: selected piece\n" +
                "- Green: legal moves\n" +
                "- Gold: last move\n" +
                "- Red: king in check"
        );
        tipsArea.setEditable(false);
        tipsArea.setOpaque(false);
        tipsArea.setForeground(new Color(214, 218, 224));
        tipsArea.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(moveHistoryList);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(74, 80, 92)));

        sidebar.add(historyTitle, BorderLayout.NORTH);
        sidebar.add(scrollPane, BorderLayout.CENTER);
        sidebar.add(tipsArea, BorderLayout.SOUTH);
        return sidebar;
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void handleBoardClick(int row, int col) {
        if (controller == null || controller.isGameOver() || aiThinking) {
            return;
        }

        if (frame.getGameMode() == GameMode.SINGLE_PLAYER && !controller.isWhiteTurn()) {
            return;
        }

        controller.handleClick(row, col);
        refreshBoard();

        if (frame.getGameMode() == GameMode.SINGLE_PLAYER
                && !controller.isWhiteTurn()
                && !controller.isGameOver()) {
            scheduleAiMove();
        }
    }

    private void scheduleAiMove() {
        stopAiTimer();
        stopAiWorker();
        aiThinking = true;
        statusLabel.setText("Black is thinking...");

        aiTimer = new Timer(500, e -> {
            Board snapshot = controller.getBoard().copy();
            aiWorker = new SwingWorker<>() {
                @Override
                protected Move doInBackground() {
                    return ai != null ? ai.getBestMove(snapshot, controller) : null;
                }

                @Override
                protected void done() {
                    try {
                        if (!isCancelled()) {
                            Move aiMove = get();
                            if (aiMove != null && !controller.isGameOver() && !controller.isWhiteTurn()) {
                                controller.executeMove(aiMove);
                            }
                        }
                    } catch (Exception ignored) {
                        statusLabel.setText("AI move failed, try restarting the match");
                    } finally {
                        aiThinking = false;
                        aiWorker = null;
                        refreshBoard();
                    }
                }
            };
            aiWorker.execute();
            aiTimer = null;
        });
        aiTimer.setRepeats(false);
        aiTimer.start();
    }

    private void restartGame() {
        stopAiTimer();
        stopAiWorker();
        if (frame.getGameMode() != null) {
            startGame(frame.getGameMode());
        }
    }

    private void stopAiTimer() {
        if (aiTimer != null) {
            aiTimer.stop();
            aiTimer = null;
        }
    }

    private void stopAiWorker() {
        if (aiWorker != null) {
            aiWorker.cancel(true);
            aiWorker = null;
        }
    }

    private void refreshBoard() {
        if (controller == null) {
            return;
        }

        updateHistory();
        statusLabel.setText(controller.getStatusMessage());

        Board board = controller.getBoard();
        Component[] components = boardPanel.getComponents();
        int checkedKingRow = -1;
        int checkedKingCol = -1;

        if (CheckDetector.isKingInCheck(board, controller.isWhiteTurn())) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    Tile tile = board.getTile(row, col);
                    if (tile.isOccupied()) {
                        Piece piece = tile.getPiece();
                        if ("King".equals(piece.getClass().getSimpleName()) && piece.isWhite() == controller.isWhiteTurn()) {
                            checkedKingRow = row;
                            checkedKingCol = col;
                            break;
                        }
                    }
                }
                if (checkedKingRow != -1) {
                    break;
                }
            }
        }

        Move lastMove = controller.getLastMove();
        int selectedRow = controller.getSelectedRow();
        int selectedCol = controller.getSelectedCol();
        List<Move> validMoves = controller.getCurrentValidMoves();

        for (int i = 0; i < components.length; i++) {
            TilePanel tilePanel = (TilePanel) components[i];
            int row = i / 8;
            int col = i % 8;

            boolean isCheckedKing = row == checkedKingRow && col == checkedKingCol;
            boolean isSelected = row == selectedRow && col == selectedCol;
            boolean isValidMove = validMoves.stream().anyMatch(m -> m.getToRow() == row && m.getToCol() == col);
            boolean isLastMove = lastMove != null &&
                    ((lastMove.getFromRow() == row && lastMove.getFromCol() == col)
                            || (lastMove.getToRow() == row && lastMove.getToCol() == col));

            tilePanel.setBackground(resolveTileColor(row, col, isCheckedKing, isSelected, isValidMove, isLastMove));
            tilePanel.removeAll();

            Tile tile = board.getTile(row, col);
            if (tile.isOccupied()) {
                JLabel label = new JLabel(tile.getPiece().getIcon());
                label.setHorizontalAlignment(SwingConstants.CENTER);
                tilePanel.add(label, BorderLayout.CENTER);
            }

            tilePanel.revalidate();
            tilePanel.repaint();
        }

        if (!moveHistoryModel.isEmpty()) {
            moveHistoryList.ensureIndexIsVisible(moveHistoryModel.size() - 1);
        }
    }

    private Color resolveTileColor(int row, int col, boolean isCheckedKing, boolean isSelected,
                                   boolean isValidMove, boolean isLastMove) {
        Color light = new Color(238, 238, 210);
        Color dark = new Color(118, 150, 86);
        Color base = (row + col) % 2 == 0 ? light : dark;

        if (isCheckedKing) {
            return new Color(214, 69, 65);
        }
        if (isSelected) {
            return new Color(93, 156, 236);
        }
        if (isValidMove) {
            return new Color(144, 201, 120);
        }
        if (isLastMove) {
            return new Color(226, 196, 90);
        }
        return base;
    }

    private void updateHistory() {
        moveHistoryModel.clear();
        for (String entry : controller.getMoveHistory()) {
            moveHistoryModel.addElement(entry);
        }
    }

    private String buildModeLabel(GameMode mode) {
        if (mode == GameMode.SINGLE_PLAYER) {
            return "Single Player - AI " + frame.getSelectedDifficulty().name();
        }
        return "Two Player";
    }
}
