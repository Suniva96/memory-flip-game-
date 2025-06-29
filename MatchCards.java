import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class MatchCards {
    class Card {
        String cardName;
        ImageIcon cardImageIcon;

        Card(String cardName, ImageIcon cardImageIcon) {
            this.cardName = cardName;
            this.cardImageIcon = cardImageIcon;
        }

        public String toString() {
            return cardName;
        }
    }

    String[] cardList = {
        "bear", "duck", "girl", "mice", "patrick",
        "ponyo", "rabbit", "sanomaya", "squidward", "whiteduck"
    };

    final int rows = 5;
    final int columns = 4;
    final int cardWidth = 90;
    final int cardHeight = 128;
    final int maxErrors = 15;

    ArrayList<Card> cardSet;
    ImageIcon cardBackImageIcon;

    int boardWidth = columns * cardWidth;
    int boardHeight = rows * cardHeight;

    JFrame frame = new JFrame(" Match Cards");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel restartGamePanel = new JPanel();
    JButton restartButton = new JButton();

    int errorCount = 0;
    int matchCount = 0;
    ArrayList<JButton> board;
    Timer hideCardTimer;
    boolean gameReady = false;
    JButton card1Selected;
    JButton card2Selected;

    MatchCards() {
        setupCards();
        shuffleCards();

        frame.setLayout(new BorderLayout());
        frame.setSize(boardWidth, boardHeight + 100);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Errors: " + errorCount + " / " + maxErrors);
        textPanel.setPreferredSize(new Dimension(boardWidth, 30));
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        board = new ArrayList<>();
        boardPanel.setLayout(new GridLayout(rows, columns));
        for (int i = 0; i < cardSet.size(); i++) {
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
            tile.setOpaque(true);
            tile.setIcon(cardSet.get(i).cardImageIcon);
            tile.setFocusable(false);
            tile.addActionListener(new CardClickHandler());
            board.add(tile);
            boardPanel.add(tile);
        }
        frame.add(boardPanel);

        restartButton.setFont(new Font("Arial", Font.PLAIN, 16));
        restartButton.setText("Restart Game");
        restartButton.setPreferredSize(new Dimension(boardWidth, 30));
        restartButton.setFocusable(false);
        restartButton.setEnabled(false);
        restartButton.addActionListener(e -> resetGame());
        restartGamePanel.add(restartButton);
        frame.add(restartGamePanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);

        hideCardTimer = new Timer(1200, e -> hideCards());
        hideCardTimer.setRepeats(false);
        hideCardTimer.start();
    }

    class CardClickHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (!gameReady || card2Selected != null) return;

            JButton clicked = (JButton) e.getSource();
            if (clicked.getIcon() == cardBackImageIcon) {
                int index = board.indexOf(clicked);
                clicked.setIcon(cardSet.get(index).cardImageIcon);

                if (card1Selected == null) {
                    card1Selected = clicked;
                } else {
                    card2Selected = clicked;
                    int i1 = board.indexOf(card1Selected);
                    int i2 = board.indexOf(card2Selected);

                    if (!cardSet.get(i1).cardName.equals(cardSet.get(i2).cardName)) {
                        errorCount++;
                        textLabel.setText("Errors: " + errorCount + " / " + maxErrors);
                        if (errorCount >= maxErrors) {
                            JOptionPane.showMessageDialog(frame, "You Lose! Try again");
                            gameReady = false;
                            restartButton.setEnabled(true);
                        }
                        hideCardTimer.start();
                    } else {
                        matchCount++;
                        card1Selected.setEnabled(false);
                        card2Selected.setEnabled(false);
                        card1Selected = null;
                        card2Selected = null;

                        if (matchCount == cardList.length) {
                            JOptionPane.showMessageDialog(frame, "You Win!Congrats ");
                            gameReady = false;
                            restartButton.setEnabled(true);
                        }
                    }
                }
            }
        }
    }

    void setupCards() {
        cardSet = new ArrayList<>();
        for (String cardName : cardList) {
            Image cardImg = new ImageIcon(getClass().getResource("./img/" + cardName + ".jpeg")).getImage();
            ImageIcon cardImageIcon = new ImageIcon(cardImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
            cardSet.add(new Card(cardName, cardImageIcon));
        }
        cardSet.addAll(new ArrayList<>(cardSet));

        Image backImg = new ImageIcon(getClass().getResource("./img/back.jpeg")).getImage();
        cardBackImageIcon = new ImageIcon(backImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
    }

    void shuffleCards() {
        for (int i = 0; i < cardSet.size(); i++) {
            int j = (int) (Math.random() * cardSet.size());
            Card temp = cardSet.get(i);
            cardSet.set(i, cardSet.get(j));
            cardSet.set(j, temp);
        }
    }

    void hideCards() {
        if (card1Selected != null && card2Selected != null) {
            card1Selected.setIcon(cardBackImageIcon);
            card2Selected.setIcon(cardBackImageIcon);
            card1Selected = null;
            card2Selected = null;
        } else {
            for (JButton tile : board) {
                tile.setIcon(cardBackImageIcon);
                tile.setEnabled(true);
            }
            gameReady = true;
            restartButton.setEnabled(true);
        }
    }

    void resetGame() {
        gameReady = false;
        restartButton.setEnabled(false);
        errorCount = 0;
        matchCount = 0;
        card1Selected = null;
        card2Selected = null;
        textLabel.setText("Errors: 0 / " + maxErrors);
        shuffleCards();

        for (int i = 0; i < board.size(); i++) {
            JButton tile = board.get(i);
            tile.setIcon(cardSet.get(i).cardImageIcon);
            tile.setEnabled(true);
        }

        hideCardTimer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MatchCards());
    }
}
