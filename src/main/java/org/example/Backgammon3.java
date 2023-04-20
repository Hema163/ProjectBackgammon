package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Backgammon3 {


    private static final int NUM_POINTS = 24;
    private static final int NUM_CHECKERS = 15;
    private static final int BAR_INDEX = 0;
    private static final int BEAR_OFF_INDEX = 25;
    private static final int[] INITIAL_BOARD = new int[]{2, 0, 0, 1, 0, 0, -5, 1, 0, 0, 0, 0, -3, 0, 0, 0, 1, 0, 2, 0, 2, 5, 0, 0, 0};
    private static final String[] MOVE_CODES = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private static final String[] HINT_CODES = new String[]{"roll", "quit", "pip", "hint"};
    private Board board;
    private int currentPlayer;
    private static int[] dice  = new int[2];
    private int[] score;
    private boolean isGameInProgress;
    private static int matchLength;
    private String player1Name;
    private String player2Name;
    private static int flagPlot = 0;
    private static int currentDouble = 0;
    private static int p1Score = 0;
    private static int p2Score = 0;
    private static int stake = 1;


    // Constructor

    public static void processFile(String filename) {
        InputStream inputStream = Backgammon3.class.getClassLoader().getResourceAsStream(filename);
        Scanner scanner = new Scanner(inputStream);

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            processCommand(command);
        }

        scanner.close();
    }

    public static void processCommand(String command) {
        String[] tokens = command.split(" ");
        String player1 = "";
        String player2 = "";
        int[] board = INITIAL_BOARD.clone();

        if (tokens[0].equalsIgnoreCase("test") && tokens.length > 1) {
            // "test <filename>" command
            processFile(tokens[1]);
        } else if (tokens[0].equalsIgnoreCase("dice") && tokens.length > 2) {
            // "dice <int> <int>" command
            try {
                int die1 = Integer.parseInt(tokens[1]);
                int die2 = Integer.parseInt(tokens[2]);
                int firstPlayer = die1 > die2 ? 1 : 2;

                Scanner scanner = new Scanner(System.in);

                // Initialize the game loop
                boolean gameOver = false;
                int currentPlayer = firstPlayer;

                if (die1 >= 1 && die1 <= 6 && die2 >= 1 && die2 <= 6) {
                    dice[0] = die1;
                    dice[1] = die2;
                    System.out.println("Dice set to " + die1 + " and " + die2 + ".");
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

                        // Apply the selected move to the board
//                        int moveIndex = moveCodeToIndex(move);
//                        applyMove(board, currentPlayer, legalMoves[moveIndex]);

                        // Check if the game is over
                        if (isGameOver(board)) {
                            int winner = determineWinner(board);
                            System.out.println(playerName(winner) + " wins!");
                        } else {
                            // Switch to the other player
                            currentPlayer = otherPlayer(currentPlayer);
                        }
                    }
                } else {
                    System.out.println("Invalid dice values.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid dice values.");
            }
        } else if (tokens[0].equalsIgnoreCase("pip") && tokens.length > 2) {
            System.out.println("Pip count for " + player1 + ": " + pipCount(board, 1));
            System.out.println("Pip count for " + player2 + ": " + pipCount(board, 2));


        }else if (tokens[0].equalsIgnoreCase("hint") && tokens.length > 2) {
            // Display a list of allowed commands
            System.out.println("Allowed commands:");
            for (String hintCode : HINT_CODES) {
                System.out.println(hintCode);
            }


        }else if (tokens[0].equalsIgnoreCase("name") && tokens.length > 2) {
            // Display a list of allowed commands
            player1 = tokens[1];
            player2  = tokens[2];
            System.out.println("Play 1: " + player1 + "\n Play 2 : "+player2);


        }




    }

    public static void main(String[] args) {
        processFile("test.txt");
        Scanner scanner = new Scanner(System.in);
        // Initialize the player names and the board
        Scanner input = new Scanner(System.in);
        System.out.print("Enter Player 1's name: ");
        String player1 = input.nextLine();
        System.out.print("Enter Player 2's name: ");
        String player2 = input.nextLine();
        System.out.print("Enter match length: ");
        Integer matchLength = input.nextInt();
        System.out.println(player1 + " vs. " + player2);
        System.out.println("Match length: " + matchLength);



        int[] board = INITIAL_BOARD.clone();

        // Roll the dice to determine the first player
        Random random = new Random();
        int die1 = rollDie();
        int die2 = rollDie();
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
            System.out.println(playerName(currentPlayer) + ", enter \"roll\" to roll the dice, \"pip\" to show the pip count, \"hint\" to show a list of allowed commands, \"douple\" to douple stack,or \"quit\" to quit:");
            String command = scanner.nextLine();
            if (command.equals("quit")) {
                gameOver = true;
            } else if (command.equals("roll")) {
                // Roll the dice and display the result
                die1 = rollDie();
                die2 = rollDie();
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
                        currentDouble++;
                        System.out.println("Current double: " + currentDouble);
                        System.out.println("Owned by: " + (currentPlayer == Board.PLAYER_1 ? player1 : player2));
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
                    ++flagPlot;
                    if (isGameOver(board)) {
                        int winner = determineWinner(board);
                        System.out.println(playerName(winner) + " wins!");
                        gameOver = true;
                    } else {
                        // Switch to the other player
                        if(flagPlot%2 == 0){
                            currentPlayer = otherPlayer(currentPlayer);
                            System.out.println("Display the match score and update it after each game");
                            System.out.println("Pip count for " + player1 + ": " + pipCount(board, 1));
                            System.out.println("Pip count for " + player2 + ": " + pipCount(board, 2));
                        }

                    }
                }

            }else if (command.equals("pip")) {
                // Display the pip count for both players
                System.out.println("Pip count for " + player1 + ": " + pipCount(board, 1));
                System.out.println("Pip count for " + player2 + ": " + pipCount(board, 2));
            } else if (command.equals("hint")) {
                // To update the functionality of the "hint" command, modify the generateLegalMoves method to exclude illegal moves based on the current state of the game (e.g. only allow bearing off when all checkers are in the inner board).
                String[] legalMoves = generateLegalMoves(board, currentPlayer, die1, die2);
                if (legalMoves.length == 0) {
                    System.out.println("No legal moves available.");
                } else {
                    System.out.println("Legal moves:");
                    for (String move : legalMoves) {
                        System.out.println(move);
                    }
                }
            }else if (command.equals("double")) {
                // Display a list of allowed commands
                System.out.println("Are you want douple:");
                String a = input.next();
                if(a == "yes"){
                    currentDouble = currentDouble*2;
                    stake = stake *2;

                }else {

                }

            } else {
                System.out.println("Invalid command.");
            }
        }
    }

    private static int rollDie() {
        return (int) (Math.random() * 6) + 1;
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
        // Check if either player has borne off all their checkers
        int p1BorneOff = 0, p2BorneOff = 0;
        for (int i = 0; i < Board.NUM_POINTS; i++) {
            if (board[i] > 0) {
                if (i >= Board.BEAR_OFF) {
                    if (board[i] == Board.PLAYER_1) {
                        p1BorneOff += board[i];
                    } else {
                        p2BorneOff += board[i];
                    }
                } else {
                    return false;
                }
            }
        }

        // Calculate the score based on whether it was a Single, Gammon, or Backgammon
        int winner = (p1BorneOff == Board.NUM_CHECKERS) ? Board.PLAYER_1 : Board.PLAYER_2;
        int points = 1;
        if (winner == Board.PLAYER_1) {
            if (p2BorneOff == 0) {
                points = 2; // gammon
                if (board[Board.BEAR_OFF + Board.NUM_POINTS - 1] == Board.PLAYER_2) {
                    points = 3; // backgammon
                }
            }
        } else {
            if (p1BorneOff == 0) {
                points = 2; // gammon
                if (board[Board.BEAR_OFF] == Board.PLAYER_1) {
                    points = 3; // backgammon
                }
            }
        }

        // Update the match score accordingly
        if (winner == Board.PLAYER_1) {
            p1Score += points * stake;
        } else {
            p2Score += points * stake;
        }




        for (int i = 0; i < Board.NUM_POINTS; i++) {
            if (board[i] > 0 && i < Board.BEAR_OFF) {
                player1Finished = false;
            }
        }

        int poin = pipCount(board, determineWinner(board));
        if(poin>matchLength) {
            System.out.println(determineWinner(board) + " wins the match!");

            player1Finished = true;
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
