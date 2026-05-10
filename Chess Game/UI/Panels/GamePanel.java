package UI.Panels;

import AI.ChessAi;
import Game.GameMode;
import Logic.CheckDetector;
import Logic.GameController;
import Model.Board;
import Model.Move;
import Model.Tile;
import Pieces.Piece;
import UI.GameFrame;
import Util.SoundPlayer;
import UI.TilePanel;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

    private GameFrame frame;
    private GameController controller;
    private ChessAi ai = new ChessAi(ChessAi.Difficulty.MEDIUM);

    public GamePanel(GameFrame frame) {
        this.frame = frame;
        setLayout(new GridLayout(8, 8));
    }

    public void startGame(GameMode mode) {
        controller = new GameController();
        removeAll();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int r = row;
                int c = col;
                TilePanel tilePanel = new TilePanel(row, col);
                tilePanel.setLayout(new BorderLayout());

                tilePanel.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        if (controller.isGameOver()) {
                            return;
                        }

                        if (frame.getGameMode() == GameMode.SINGLE_PLAYER && !controller.isWhiteTurn()) {
                            return;
                        }
                        controller.handleClick(r, c);
                        refreshBoard();

                        if (frame.getGameMode() == GameMode.SINGLE_PLAYER
                                && !controller.isWhiteTurn()
                                && !controller.isGameOver()) {

                            Timer timer = new Timer(3000, e -> {
                                Move aiMove = ai.getBestMove(controller.getBoard(), controller);

                                if (aiMove != null) {
                                    controller.handleClick(aiMove.getFromRow(), aiMove.getFromCol());
                                    controller.handleClick(aiMove.getToRow(), aiMove.getToCol());
                                    refreshBoard();
                                }
                            });

                            timer.setRepeats(false);
                            timer.start();
                        }
                    }
                });

                add(tilePanel);
            }
        }

        refreshBoard();
        revalidate();
        repaint();
    }

    private void refreshBoard() {
        Board board = controller.getBoard();
        Component[] components = getComponents();

        // Find king position for highlighting if in check
        int checkedKingRow = -1, checkedKingCol = -1;
        if (CheckDetector.isKingInCheck(board, controller.isWhiteTurn())) {
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    Tile tile = board.getTile(r, c);
                    if (tile.isOccupied()) {
                        Piece p = tile.getPiece();
                        if (p.getClass().getSimpleName().equals("King")
                                && p.isWhite() == controller.isWhiteTurn()) {
                            checkedKingRow = r;
                            checkedKingCol = c;
                            break;
                        }
                    }
                }
                if (checkedKingRow != -1) break;
            }
        }

        for (int i = 0; i < components.length; i++) {
            TilePanel tilePanel = (TilePanel) components[i];
            int row = i / 8;
            int col = i % 8;

            boolean isHighlight = controller.getCurrentValidMoves().stream()
                    .anyMatch(m -> m.getToRow() == row && m.getToCol() == col);

            // Highlight checked king in red, valid moves in green
            Color backgroundColor;
            if (row == checkedKingRow && col == checkedKingCol) {
                backgroundColor = new Color(255, 0, 0, 150);  // Red for checked king
            } else if (isHighlight) {
                backgroundColor = new Color(144, 238, 144);  // Green for valid moves
            } else {
                backgroundColor = (row + col) % 2 == 0 ? Color.WHITE : Color.GRAY;
            }

            tilePanel.setBackground(backgroundColor);

            // Update Icons
            tilePanel.removeAll();
            Tile tile = board.getTile(row, col);
            if (tile.isOccupied()) {
                JLabel label = new JLabel(tile.getPiece().getIcon());
                tilePanel.add(label);
            }
            tilePanel.revalidate();
            tilePanel.repaint();
        }
    }
}