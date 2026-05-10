package Logic;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import UI.*;

public class GameStage extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(GameStage.class.getName());

    private AddPlayerNames addPlayers = new AddPlayerNames();
    ArrayList<String> temp = new ArrayList<>();
    String[] pids;
    Game game;
    ArrayList<JButton> cardButtons = new ArrayList<>();
    ArrayList<String> cardIds;
    PopUp window;

    // IMAGE LOADER (SMALL CARDS)
    private ImageIcon loadSmallCard(String name) {

        String path = "/Cards/Small/" + name + ".png";

        java.net.URL url = getClass().getResource(path);

        if (url == null) {
            System.out.println("Image NOT FOUND: " + path);
            return new ImageIcon();
        }

        return new ImageIcon(url);
    }

    public GameStage() {}

    public GameStage(ArrayList<String> playerIds) {

        initComponents();

        temp = playerIds;
        pids = temp.toArray(new String[temp.size()]);
        game = new Game(pids);

        populateArrayList();

        game.start(game);

        setPidName();

        // FIXED TOP CARD IMAGE
        topCardButton.setIcon(loadSmallCard(game.getTopCardName())
        );

        setButtonIcons();
    }

    public void setButtonIcons() {

        String listString = game.getPlayerHand(game.getCurrentPlayer())
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));

        String[] cardNames = listString.split(",");
        cardIds = new ArrayList<>(Arrays.asList(cardNames));

        for (int i = 0; i < cardIds.size(); i++) {

            cardButtons.get(i)
                    .setIcon(loadSmallCard(cardIds.get(i))); // ✅ CORRECT
        }

        for (int i = cardIds.size(); i < cardButtons.size(); i++) {
            cardButtons.get(i).setIcon(null);
        }
    }

    public void populateArrayList() {

        cardButtons.add(jButton1);
        cardButtons.add(jButton2);
        cardButtons.add(jButton3);
        cardButtons.add(jButton4);
        cardButtons.add(jButton5);
        cardButtons.add(jButton6);
        cardButtons.add(jButton7);
    }

    public void setPidName() {
        String currentPlayer = game.getCurrentPlayer();
        pidNameLabel.setText(currentPlayer + "'s cards");
    }

    public void setPidName(String currentPlayer) {
        pidNameLabel.setText(currentPlayer + "'s cards");
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        pidNameLabel = new javax.swing.JLabel();
        drawCardButton = new javax.swing.JButton();
        downCard = new javax.swing.JButton();
        topCardButton = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();

        // Button listeners
        jButton1.addActionListener(this::jButton1ActionPerformed);
        jButton2.addActionListener(this::jButton2ActionPerformed);
        jButton3.addActionListener(this::jButton3ActionPerformed);
        jButton4.addActionListener(this::jButton4ActionPerformed);
        jButton5.addActionListener(this::jButton5ActionPerformed);
        jButton6.addActionListener(this::jButton6ActionPerformed);
        jButton7.addActionListener(this::jButton7ActionPerformed);
        drawCardButton.addActionListener(this::drawCardButtonActionPerformed);
        topCardButton.addActionListener(this::topCardButtonActionPerformed);

        // Labels
        pidNameLabel.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 36));
        pidNameLabel.setText(".");

        drawCardButton.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 24));
        drawCardButton.setText("Draw Card");

        // Deck image
        java.net.URL downUrl = getClass().getResource("/Cards/Small/Downward.png");
        if (downUrl != null) downCard.setIcon(new javax.swing.ImageIcon(downUrl));
        else downCard.setText("Deck");

        topCardButton.setText("");

        // Bottom row: the 7 card buttons
        javax.swing.JPanel handPanel = new javax.swing.JPanel(
                new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 8, 8));
        handPanel.add(jButton1);
        handPanel.add(jButton2);
        handPanel.add(jButton3);
        handPanel.add(jButton4);
        handPanel.add(jButton5);
        handPanel.add(jButton6);
        handPanel.add(jButton7);

        // Centre: top card + deck + draw button
        javax.swing.JPanel centerPanel = new javax.swing.JPanel(
                new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 24, 24));
        centerPanel.add(topCardButton);
        centerPanel.add(downCard);
        centerPanel.add(drawCardButton);

        // Main panel
        jPanel1.setLayout(new java.awt.BorderLayout(8, 8));
        jPanel1.add(pidNameLabel, java.awt.BorderLayout.NORTH);
        jPanel1.add(centerPanel,  java.awt.BorderLayout.CENTER);
        jPanel1.add(handPanel,    java.awt.BorderLayout.SOUTH);

        // Frame setup
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("UNO");
        getContentPane().add(jPanel1);
        pack();
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
    }

    // Button Events
    private void openPopup(int index) {

        if (cardIds.get(index) != null) {
            String cardId = cardIds.get(index);

            window = new PopUp(
                    cardId,
                    game,
                    index,
                    cardButtons,
                    this,
                    topCardButton);

            // window.setBounds(750, 40, 700, 800);
            window.setSize(700,800);
            window.setLocationRelativeTo(null);
            window.setVisible(true);
            window.setResizable(false);
            window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt){openPopup(0);}
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt){openPopup(1);}
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt){openPopup(2);}
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt){openPopup(3);}
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt){openPopup(4);}
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt){openPopup(5);}
    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt){openPopup(6);}

    private void drawCardButtonActionPerformed(java.awt.event.ActionEvent evt) {

        JLabel message =
                new JLabel(game.getCurrentPlayer() + " draw a card.");

        message.setFont(new Font("Arial", Font.BOLD, 48));
        JOptionPane.showMessageDialog(null, message);

        try {
            game.submitDraw(game.getCurrentPlayer());
        } catch (InvalidPlayerTurnException e) {
            Logger.getLogger(GameStage.class.getName())
                    .log(Level.SEVERE, null, e);
        }

        setPidName(game.getCurrentPlayer());
        setButtonIcons();
    }

    private void topCardButtonActionPerformed(java.awt.event.ActionEvent evt) {}
    private void downCardActionPerformed(java.awt.event.ActionEvent evt) {}

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() ->
                new GameStage().setVisible(true));
    }

    // Variables declaration
    private javax.swing.JButton downCard;
    private javax.swing.JButton drawCardButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel pidNameLabel;
    private javax.swing.JButton topCardButton;
}
