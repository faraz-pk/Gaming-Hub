package Logic;

import java.awt.*;
import java.util.*;
import javax.swing.*;

public class Game {
    private int currentPlayer;
    private final String[] playerIds;

    private final UnoDeck deck;
    private final ArrayList<ArrayList<UnoCard>> playerHand;
    private final ArrayList<UnoCard> stockPile;

    private UnoCard.Color validColor;
    private UnoCard.Value validValue;

    boolean gameDirection;

    public Game(String[] pids) {
        deck = new UnoDeck();
        deck.shuffle();
        stockPile = new ArrayList<>();

        this.playerIds = pids;
        currentPlayer = 0;
        gameDirection = false;

        playerHand = new ArrayList<>();

        for (int i = 0; i < pids.length; i++) {
            ArrayList<UnoCard> hand = new ArrayList<>(Arrays.asList(deck.drawCard(4)));
            playerHand.add(hand);
        }

    }

    public void start(Game game) {
        UnoCard card = deck.drawCard();
        validColor = card.getColor();
        validValue = card.getValue();

        if (card.getValue() == UnoCard.Value.Wild) {
            start(game);
        }

        if (card.getValue() == UnoCard.Value.WildDrawFour || card.getValue() == UnoCard.Value.DrawTwo) {
            start(game);
        }

        if (card.getValue() == UnoCard.Value.Skip) {
            JLabel message = new JLabel(playerIds[currentPlayer] + " was skipped!");
            message.setFont(new Font("Arial", Font.BOLD, 48));
            JOptionPane.showMessageDialog(null, message);

            if (!gameDirection) {
                currentPlayer = (currentPlayer + 1) % playerIds.length;
            }
            else if (gameDirection) {
                currentPlayer = (currentPlayer - 1) % playerIds.length;
                if (currentPlayer == -1){
                    currentPlayer = playerIds.length -1;
                }
            }
        }

        if (card.getValue() == UnoCard.Value.Reverse) {
            JLabel message = new JLabel(playerIds[currentPlayer] + " The game Direction changed!");
            message.setFont(new Font("Arial", Font.BOLD, 48));
            JOptionPane.showMessageDialog(null, message);
            gameDirection ^= true;
            currentPlayer = playerIds.length - 1;
        }

        stockPile.add(card);
    }

    public UnoCard getTopCard() {
        return new UnoCard(validColor, validValue);
    }

    public ImageIcon getTopCardImage() {
        return new ImageIcon(validColor + "_" + validValue + ".png");
    }

    public boolean isGameOver() {
        for (String player : this.playerIds) {
            if (hasEmptyHand(player)) {
                return true;
            }
        }
        return false;
    }

    public String getCurrentPlayer() {
        return this.playerIds[this.currentPlayer];
    }

    public String getPreviousPlayer (int i) {
        int index = this.currentPlayer - i;
        if (index == -1) {
            index = playerIds.length - 1;
        }
        return this.playerIds[index];
    }

    public String[] getPlayers() {
        return playerIds;
    }

    public String getTopCardName() {
        if (validValue == UnoCard.Value.Wild || validValue == UnoCard.Value.WildDrawFour) {
            return "Wild_" + validValue;
        }
        return validColor + "_" + validValue;
    }

    public ArrayList<UnoCard> getPlayerHand(String pid) {
        int index = Arrays.asList(playerIds).indexOf(pid);
        return playerHand.get(index);
    }

    public int getPlayerHandSize(String pid) {
        return getPlayerHand(pid).size();
    }

    public UnoCard getPlayerCard(String pid, int choice) {
        ArrayList<UnoCard> hand = getPlayerHand(pid);
        return hand.get(choice);
    }

    public boolean hasEmptyHand(String pid) {
        return getPlayerHand(pid).isEmpty();
    }

    public boolean validCardPlay(UnoCard card) {
        return card.getColor() == validColor || card.getValue() == validValue || card.getColor() == UnoCard.Color.Wild;
    }

    public void checkPlayerTurn(String pid) throws InvalidPlayerTurnException {
        if (!this.playerIds[this.currentPlayer].equals(pid)) {
            throw new InvalidPlayerTurnException("it is not " + pid + "'s turn" , pid);

        }
    }

    public void submitDraw(String pid) throws InvalidPlayerTurnException {
        checkPlayerTurn(pid);

        if (deck.isEmpty()) {
            deck.replaceDeckWith(stockPile);
            deck.shuffle();
        }

        getPlayerHand(pid).add(deck.drawCard());

        if (gameDirection == false) {
            currentPlayer = (currentPlayer + 1) % playerIds.length;
        }
        else if (gameDirection == true) {
            currentPlayer = (currentPlayer - 1) % playerIds.length;
            if (currentPlayer == -1) {
                currentPlayer = playerIds.length - 1;
            }
        }
    }

    public void setCardColor(UnoCard.Color color) {
        validColor = color;
    }

    public void submitPlayerCard(String pid, UnoCard card, UnoCard.Color declaredColor) throws InvalidColorSubmissionException, InvalidValueSubmissionException, InvalidPlayerTurnException{
        checkPlayerTurn(pid);

        ArrayList<UnoCard> pHand = getPlayerHand(pid);

        if (!validCardPlay(card)) {
            if (card.getColor() == UnoCard.Color.Wild) {
                validColor = card.getColor();
                validValue = card.getValue();
            }

            if (card.getColor() != validColor) {
                JLabel message = new JLabel("Invalid Player move, expected color: " + validColor + " but got color " + card.getColor());
                message.setFont(new Font("Arial", Font.BOLD, 48));
                JOptionPane.showMessageDialog(null, message);
                throw new InvalidColorSubmissionException("Invalid Player move, expected color: " + validColor + " but got color " + card.getColor(), card.getColor(), validColor);
            }
            else if (card.getValue() != validValue) {
                JLabel message2 = new JLabel("Invalid player move, expected value: " + validValue + " but got value " + card.getValue());
                message2.setFont(new Font("Arial", Font.BOLD, 48));
                JOptionPane.showMessageDialog(null, message2);
                throw new InvalidValueSubmissionException("Invalid player move, expected value: " + validValue + " but got value " + card.getValue(), card.getValue(), validValue);
            }
        }
        pHand.remove(card);

        if (hasEmptyHand(this.playerIds[currentPlayer])) {
            JLabel message2 = new JLabel(this.playerIds[currentPlayer] + " won the game! Thank you for playing");
            message2.setFont(new Font("Arial", Font.BOLD, 48));
            JOptionPane.showMessageDialog(null, message2);
            System.exit(0);
        }

        validColor = card.getColor();
        validValue = card.getValue();
        stockPile.add(card);

        if (!gameDirection) {
            currentPlayer = (currentPlayer + 1) % playerIds.length;
        }
        else if (gameDirection) {
            currentPlayer = (currentPlayer - 1) % playerIds.length;
            if (currentPlayer == -1) {
                currentPlayer = playerIds.length -1;
            }
        }

        if (card.getColor() == UnoCard.Color.Wild) {
            validColor = declaredColor;
        }

        if (card.getValue() == UnoCard.Value.DrawTwo) {
            pid = playerIds[currentPlayer];
            getPlayerHand(pid).add(deck.drawCard());
            getPlayerHand(pid).add(deck.drawCard());
            JLabel message = new JLabel(pid + " draw 2 cards");
            message.setFont(new Font("Arial", Font.BOLD, 48));
            JOptionPane.showMessageDialog(null, message);
        }

        if (card.getValue() == UnoCard.Value.WildDrawFour) {
            pid = playerIds[currentPlayer];
            getPlayerHand(pid).add(deck.drawCard());
            getPlayerHand(pid).add(deck.drawCard());
            getPlayerHand(pid).add(deck.drawCard());
            getPlayerHand(pid).add(deck.drawCard());
            JLabel message = new JLabel(pid + " draw 4 cards");
            message.setFont(new Font("Arial", Font.BOLD, 48));
            JOptionPane.showMessageDialog(null, message);
        }

        if (card.getValue() == UnoCard.Value.Skip) {
            JLabel message = new JLabel(playerIds[currentPlayer] + " was skipped");
            message.setFont(new Font("Arial", Font.BOLD, 48));
            JOptionPane.showMessageDialog(null, message);
            if (!gameDirection) {
                currentPlayer = (currentPlayer + 1) % playerIds.length;
            }
            else if(gameDirection) {
                currentPlayer = (currentPlayer - 1) % playerIds.length;
                if (currentPlayer == -1) {
                    currentPlayer = playerIds.length - 1;
                }
            }
        }

        if (card.getValue() == UnoCard.Value.Reverse) {
            gameDirection ^= true;
            if (gameDirection) {
                currentPlayer = (currentPlayer - 2) % playerIds.length;
                if (currentPlayer == -1) {
                    currentPlayer = playerIds.length - 1;
                }

                if (currentPlayer == -2) {
                    currentPlayer = playerIds.length - 2;
                }
            }
            else if(!gameDirection) {
                currentPlayer = (currentPlayer + 2) % playerIds.length;

            }
        }
    }
}

