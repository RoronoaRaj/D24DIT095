import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

public class MemoryMatchingGame extends JFrame implements ActionListener {
    private JButton[][] buttons = new JButton[4][4]; // The buttons on the grid
    private int[][] values = new int[4][4];         // Hidden values behind the buttons
    private boolean[][] revealed = new boolean[4][4]; // Revealed status for each button
    private int firstRow = -1, firstCol = -1;       // First clicked button's coordinates
    private int player1Score = 0, player2Score = 0;
    private int currentPlayer = 1;
    private JLabel scoreLabel;
    private boolean waitingForSecond = false;      // To manage turns between two clicks

    public MemoryMatchingGame() {
        setTitle("Memory Matching Game");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize grid and shuffle values
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(4, 4));
        initializeValues();

        // Create buttons and add them to the grid
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                buttons[i][j] = new JButton("*");
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 20));
                buttons[i][j].addActionListener(this);
                gridPanel.add(buttons[i][j]);
            }
        }

        // Add score label at the bottom
        scoreLabel = new JLabel("Player 1: 0 | Player 2: 0");
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(gridPanel, BorderLayout.CENTER);
        add(scoreLabel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Initialize shuffled values for the grid
    private void initializeValues() {
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            numbers.add(i);
            numbers.add(i); // Each number twice
        }
        Collections.shuffle(numbers); // Shuffle numbers

        int index = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                values[i][j] = numbers.get(index);
                revealed[i][j] = false;
                index++;
            }
        }
    }

    // Handle button clicks
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();

        // Find the position of the clicked button
        int clickedRow = -1, clickedCol = -1;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (buttons[i][j] == clickedButton) {
                    clickedRow = i;
                    clickedCol = j;
                }
            }
        }

        // If the button is already revealed, ignore the click
        if (revealed[clickedRow][clickedCol]) {
            return;
        }

        // Show the value of the clicked button
        buttons[clickedRow][clickedCol].setText(String.valueOf(values[clickedRow][clickedCol]));

        // If waiting for the second button to be clicked
        if (!waitingForSecond) {
            firstRow = clickedRow;
            firstCol = clickedCol;
            waitingForSecond = true;
        } else {
            final int secondRow = clickedRow;  // Use final variables inside Timer
            final int secondCol = clickedCol;

            // Check if the second button matches the first
            if (values[clickedRow][clickedCol] == values[firstRow][firstCol]) {
                revealed[clickedRow][clickedCol] = true;
                revealed[firstRow][firstCol] = true;

                // Update score
                if (currentPlayer == 1) {
                    player1Score++;
                } else {
                    player2Score++;
                }

                updateScoreLabel();
                checkGameOver();

            } else {
                // No match, hide both after a short delay
                Timer timer = new Timer(1000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        buttons[secondRow][secondCol].setText("*");
                        buttons[firstRow][firstCol].setText("*");
                    }
                });
                timer.setRepeats(false);
                timer.start();

                // Switch player turns
                currentPlayer = (currentPlayer == 1) ? 2 : 1;
            }
            waitingForSecond = false;
        }
    }

    // Update the score label
    private void updateScoreLabel() {
        scoreLabel.setText("Player 1: " + player1Score + " | Player 2: " + player2Score);
    }

    // Check if the game is over
    private void checkGameOver() {
        if (player1Score + player2Score == 8) { // All pairs are found
            String winner;
            if (player1Score > player2Score) {
                winner = "Player 1 wins!";
            } else if (player2Score > player1Score) {
                winner = "Player 2 wins!";
            } else {
                winner = "It's a tie!";
            }
            JOptionPane.showMessageDialog(this, winner + "\nFinal Score:\nPlayer 1: " + player1Score + "\nPlayer 2: " + player2Score);
        }
    }

    public static void main(String[] args) {
        new MemoryMatchingGame();
    }
}
