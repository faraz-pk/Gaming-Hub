package UI;

import java.awt.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import Logic.*;

public class PopUp extends javax.swing.JFrame {

    String cardImage = "";
    Game game;
    ArrayList<UnoCard> playerHand;
    int choice;
    ArrayList<JButton> cardButtons;
    GameStage gameStage;
    JButton topCardButton;
    UnoCard.Color declaredColor;

    public PopUp() {
        initComponents();
    }

    public PopUp(String cardName,
                 Game game,
                 int index,
                 ArrayList<JButton> cardButtons,
                 GameStage gameStage,
                 JButton topCardButton) {

        initComponents();

        this.cardImage = cardName;
        this.game = game;
        this.choice = index;
        this.cardButtons = cardButtons;
        this.gameStage = gameStage;
        this.topCardButton = topCardButton;

        playerHand = game.getPlayerHand(game.getCurrentPlayer());

        // Load Large Card Image
        cardLabel.setIcon(loadLargeCard(cardImage));
    }

    // IMAGE LOADER
    private ImageIcon loadLargeCard(String name) {
        return ImageLoader.load("/Cards/Large/" + name + ".jpg");
    }

    private ImageIcon loadSmallCard(String name) {
        return ImageLoader.load("/Cards/Small/" + name + ".png");
    }

    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        useCardButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        cardLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        useCardButton.setFont(new java.awt.Font("Yu Gothic UI Semibold", Font.PLAIN, 24));
        useCardButton.setText("Use Card");
        useCardButton.addActionListener(this::useCardButtonActionPerformed);

        cancelButton.setFont(new java.awt.Font("Yu Gothic UI Semibold", Font.PLAIN, 24));
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(this::cancelButtonActionPerformed);

        jPanel1.add(cardLabel);
        jPanel1.add(useCardButton);
        jPanel1.add(cancelButton);

        add(jPanel1);

        pack();
        setLocationRelativeTo(null);
    }

    private void useCardButtonActionPerformed(java.awt.event.ActionEvent evt) {

        PickColorFrame pickColor = new PickColorFrame(this);
        declaredColor = pickColor.chooseColor(playerHand.get(choice));
        // execution reaches here only AFTER user picks a color (or for non-wild cards)

        if (declaredColor != null) {
            try {
                game.submitPlayerCard(
                        game.getCurrentPlayer(),
                        playerHand.get(choice),
                        declaredColor);

            } catch (InvalidColorSubmissionException |
                     InvalidValueSubmissionException |
                     InvalidPlayerTurnException e) {
                Logger.getLogger(PopUp.class.getName())
                        .log(Level.SEVERE, null, e);
                return; // don't update UI if card was invalid
            }

            gameStage.setPidName(game.getCurrentPlayer());
            gameStage.setButtonIcons();
            topCardButton.setIcon(loadSmallCard(game.getTopCardName())); // updates top card
            dispose();
        }
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }

    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel cardLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton useCardButton;
}
