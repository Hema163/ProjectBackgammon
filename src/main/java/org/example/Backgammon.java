package org.example;
import java.util.Scanner;
import java.util.Random;

public class Backgammon {

    public static void main(String[] args) {
        // Initialize the board with checkers in their initial positions
//        int[] board = new int[] {2, 0, 0, 0, -1, 0, 5, 0, 0, -3, 0, 0, 5, 0, 0, -2};

        // Initialize the player names
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the name of player 1: ");
        String player1 = scanner.nextLine();
        System.out.print("Enter the name of player 2: ");
        String player2 = scanner.nextLine();

        // Initialize the random number generator
        Random random = new Random();

        // Initialize the game loop
        boolean gameOver = false;
        int currentPlayer = 1;

        // Game loop
        while (!gameOver) {
            // Prompt the player to roll the dice
            System.out.println(playerName(currentPlayer) + ", enter \"roll\" to roll the dice or \"quit\" to quit:");
            String command = scanner.nextLine();
            if (command.equals("quit")) {
                gameOver = true;
            } else if (command.equals("roll")) {
                // Roll the dice and display the result
                int die1 = random.nextInt(6) + 1;
                int die2 = random.nextInt(6) + 1;
                System.out.println(playerName(currentPlayer) + " rolled " + die1 + " and " + die2);

                // TODO: Update the board based on the dice roll

                // Switch to the other player
                currentPlayer = otherPlayer(currentPlayer);
            } else {
                System.out.println("Invalid command.");
            }
        }

        // Game over
        System.out.println("Game over.");
    }

    // Returns the name of the player with the given player number
    private static String playerName(int player) {
        return player == 1 ? "Player 1" : "Player 2";
    }

    // Returns the number of the other player (i.e. not the given player)
    private static int otherPlayer(int player) {
        return player == 1 ? 2 : 1;
    }

}
