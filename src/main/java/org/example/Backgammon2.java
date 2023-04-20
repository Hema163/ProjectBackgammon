package org.example;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Random;

public class Backgammon2 {

    private static final int NUM_POINTS = 24;
    private static final int NUM_CHECKERS = 15;
    private static final int BAR_INDEX = 0;
    private static final int BEAR_OFF_INDEX = 25;
    private static final int[] INITIAL_BOARD = new int[]{2, 0, 0, 1, 0, 0, -5, 1, 0, 0, 0, 0, -3, 0, 0, 0, 1, 0, 2, 0, 2, 5, 0, 0, 0};
    private static final String[] MOVE_CODES = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private static final String[] HINT_CODES = new String[]{"roll", "quit", "pip", "hint"};

    public static void main(String[] args) {
        // Initialize the player names and the board
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the name of player 1: ");
        String player1 = scanner.nextLine();
        System.out.print("Enter the name of player 2: ");
        String player2 = scanner.nextLine();
        int[] board = INITIAL_BOARD.clone();

        // Roll the dice to determine the first player
        Random random = new Random();
        int die1 = random.nextInt(6) + 1;
        int die2 = random.nextInt(6) + 1;
        int firstPlayer = die1 > die2 ? 1 : 2;
        System.out.println(playerName(firstPlayer) + " goes first (rolled " + die1 + " and " + die2 + ")");

        // Initialize the game loop
        boolean gameOver = false;
        int currentPlayer = firstPlayer;

        // Game loop
        while (!gameOver) {
            // Display the board and the current player
            displayBoard(board, currentPlayer);

            // Prompt the player to roll the dice
            System.out.println(playerName(currentPlayer) + ", enter \"roll\" to roll the dice, \"pip\" to show the pip count, \"hint\" to show a list of allowed commands, or \"quit\" to quit:");
            String command = scanner.nextLine();
            if (command.equals("quit")) {
                gameOver = true;
            } else if (command.equals("roll")) {
                // Roll the dice and display the result
                die1 = random.nextInt(6) + 1;
                die2 = random.nextInt(6) + 1;
                System.out.println(playerName(currentPlayer) + " rolled " + die1 + " and " + die2);

                // Generate a list of legal moves and prompt the player to select one
                String[] legalMoves = generateLegalMoves(board ,currentPlayer, die1, die2);
                if (legalMoves.length == 0) {
                    System.out.println("No legal moves available.");
                } else {
                    System.out.println("Legal moves:");
                    for (int i = 0; i < legalMoves.length; i++) {
                        System.out.println(MOVE_CODES[i] + ": " + legalMoves[i]);
                    }
                    // Prompt the player to select a move
                    String move = "";
                    while (!isValidMove(move, legalMoves)) {
                        System.out.println(playerName(currentPlayer) + ", enter the letter code of the move you want to make:");
                        move = scanner.nextLine().toUpperCase();
                        if (!isValidMove(move, legalMoves)) {
                            System.out.println("Invalid move.");
                        }
                    }

                    // Apply the selected move to the board
                    int moveIndex = moveCodeToIndex(move);
                    applyMove(board, currentPlayer, legalMoves[moveIndex]);

                    // Check if the game is over
                    if (isGameOver(board)) {
                        int winner = determineWinner(board);
                        System.out.println(playerName(winner) + " wins!");
                        gameOver = true;
                    } else {
                        // Switch to the other player
                        currentPlayer = otherPlayer(currentPlayer);
                    }
                }

            }else if (command.equals("pip")) {
                // Display the pip count for both players
                System.out.println("Pip count for " + player1 + ": " + pipCount(board, 1));
                System.out.println("Pip count for " + player2 + ": " + pipCount(board, 2));
            } else if (command.equals("hint")) {
                // Display a list of allowed commands
                System.out.println("Allowed commands:");
                for (String hintCode : HINT_CODES) {
                    System.out.println(hintCode);
                }
            } else {
                System.out.println("Invalid command.");
            }
        }
    }


    // Returns the name of the given player number
    private static String playerName(int player) {
        return player == 1 ? "Player 1" : "Player 2";
    }

    // Returns the index of the given move code
    private static int moveCodeToIndex(String moveCode) {
        return moveCode.charAt(0) - 'A';
    }

    // Generates a list of legal moves for the given player and dice rolls
//    private static String[] generateLegalMoves(int[] board, int player, int die1, int die2) {
//        // TODO: implement this method
//        return new String[0];
//    }
    private static String[] generateLegalMoves(int[] board, int player, int die1, int die2) {
        List<String> legalMoves = new ArrayList<>();

        // Check if the player can bear off
        if (canBearOff(board, player, die1) || canBearOff(board, player, die2)) {
            // If the player can bear off, generate all legal bear-off moves
            for (int i = 1; i <= 6; i++) {
                int point = player * 24 - i;
                try{
                    if (board[point] > 0) {
                        int bearOffPoint = Board.BEAR_OFF + (player == Board.PLAYER_1 ? die1 : die2) - 1;
                        try{
                            if (bearOffPoint <= Board.BEAR_OFF + 5 && board[bearOffPoint] <= 1) {
                                legalMoves.add(point + "/" + Board.BEAR_OFF);
                            }
                        }catch (Exception e){

                        }

                    }
                }catch (Exception e){

                }

            }
        } else {
            // If the player can't bear off, generate all legal moves
            for (int i = 0; i < Board.NUM_POINTS; i++) {
                if (board[i] > 0 && board[i] == player) {
                    int dest1 = i + die1;
                    int dest2 = i + die2;
                    if (dest1 < Board.NUM_POINTS && board[dest1] <= 1) {
                        legalMoves.add(i + "/" + dest1);
                    }
                    if (dest2 < Board.NUM_POINTS && board[dest2] <= 1 && dest2 != dest1) {
                        legalMoves.add(i + "/" + dest2);
                    }
                }
            }
        }

        return legalMoves.toArray(new String[0]);
    }


    // Generates a list of legal moves for the current player based on the roll of the dice

    private static boolean canBearOff(int[] board, int player, int die) {
        int bearOffPoint = Board.BEAR_OFF + die - 1;
        for (int i = player * 6; i < player * 6 + 6; i++) {
            if (board[i] > 0 && i + die >= Board.NUM_POINTS) {
                boolean allOpponentCheckersOnHigherPoints = true;
                for (int j = i + 1; j < Board.NUM_POINTS; j++) {
                    if (board[j] > 0 && board[j] != player) {
                        allOpponentCheckersOnHigherPoints = false;
                        break;
                    }
                }
                if (allOpponentCheckersOnHigherPoints) {
                    return true;
                }
            }
        }try{
            return board[bearOffPoint] <= 1;
        }catch (Exception e){
            return false;
        }

    }


    // Prompts the player to select a move from the list of legal moves
    private static Move promptForMove(List<Move> legalMoves, String playerName) {
        System.out.println(playerName + ", please select a move from the following list:");
        for (int i = 0; i < legalMoves.size(); i++) {
            System.out.println((i + 1) + ": " + legalMoves.get(i));
        }

        int moveIndex = -1;
        while (moveIndex < 0 || moveIndex >= legalMoves.size()) {
            System.out.print("Enter move number: ");
            Scanner scanner = new Scanner(System.in);
            moveIndex = scanner.nextInt() - 1;
        }

        return legalMoves.get(moveIndex);
    }


    // Applies the given move to the board
    private static void applyMove(int[] board, int player, String move) {
        // TODO: implement this method
    }

    // Returns true if the given move is valid for the given list of legal moves
    private static boolean isValidMove(String move, String[] legalMoves) {
        if (move.length() != 1) {
            return false;
        }
        int moveIndex = moveCodeToIndex(move);
        return moveIndex >= 0 && moveIndex < legalMoves.length;
    }

    // Returns true if the game is over
    private static boolean isGameOver(int[] board) {
        boolean player1Finished = true;

        for (int i = 0; i < Board.NUM_POINTS; i++) {
            if (board[i] > 0 && i < Board.BEAR_OFF) {
                player1Finished = false;
            }
        }

        return player1Finished;
    }

    // Returns the player number of the winner, or 0 if the game is not over
    private static int determineWinner(int[] board) {
        // TODO: implement this method
        return 0;
    }

    // Returns the pip count for the given player
    private static int pipCount(int[] board, int player) {
        int pipCount = 0;
        for (int i = 1; i <= NUM_POINTS; i++) {
            if (board[i] == player) {
                pipCount += i * Math.abs(board[i]);
            }
        }
        return pipCount;
    }

    // Returns the player number of the other player
    private static int otherPlayer(int player) {
        return player == 1 ? 2 : 1;
    }

    // Returns the pip number for the given point on the given player's turn
    private static int getPipNumber(int[] board, int point, int player) {
        if (board[point] == player) {
            return point;
        } else if (board[point] == -player) {
            return -point;
        } else {
            return 0;
        }
    }

    // Displays the board with the current player's checkers highlighted
    private static void displayBoard(int[] board, int currentPlayer) {
        // Calculate the pip numbers for each point
        int[] pips = new int[NUM_POINTS];
        for (int i = 1; i <= NUM_POINTS; i++) {
            pips[i - 1] = getPipNumber(board, i, currentPlayer);
        }

        // Display the board
        System.out.println(" 13 14 15 16 17 18 | 19 20 21 22 23 24 ");
        System.out.println("+--+--+--+--+--+ BAR +--+--+--+--+--+");
        System.out.println("|  |" + pipString(pips[12]) + "|" + pipString(pips[13]) + "|" + pipString(pips[14]) + "|" + pipString(pips[15]) + "|" + pipString(pips[16]) + "| | |" + pipString(pips[18]) + "|" + pipString(pips[19]) + "|" + pipString(pips[20]) + "|" + pipString(pips[21]) + "|" + pipString(pips[22]) + "|" + pipString(pips[23]) + "|");
        System.out.println("|" + pipString(pips[11]) + "+--+--+--+--+--+" + pipString(pips[17]) + "| | |" + pipString(pips[23]) + "+--+--+--+--+--+" + pipString(pips[10]) + "|");
        System.out.println("|  |" + pipString(pips[10]) + "|" + pipString(pips[9]) + "|" + pipString(pips[8]) + "|" + pipString(pips[7]) + "|" + pipString(pips[6]) + "| | |" + pipString(pips[5]) + "|" + pipString(pips[4]) + "|" + pipString(pips[3]) + "|" + pipString(pips[2]) + "|" + pipString(pips[1]) + "|" + pipString(pips[0]) + "|");
        System.out.println("+--+--+--+--+--+ BAR +--+--+--+--+--+");
        System.out.println(" 12 11 10  9  8  7 |  6  5  4  3  2  1 ");
        System.out.println();
    }

    // Returns a string representing the pip number for the given point
    private static String pipString(int pip) {
        if (pip == 0) {
            return "  ";
        } else if (pip > 0) {
            return String.format("%2d", pip);
        } else {
            return String.format("%2d", -pip) + "*";
        }
    }
}




