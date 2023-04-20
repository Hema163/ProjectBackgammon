package org.example;

public class Board {
    public static final int BEAR_OFF = 24;
    public static final int PLAYER_1 = 1;
    public static final int PLAYER_2 = 1;
    public static final int NUM_POINTS = 24;
    public static final int NUM_CHECKERS = 1 ;

    private int[] board;

    public Board() {
        board = new int[NUM_POINTS + 1];
    }

    public void setPoint(int point, int count) {
        board[point] = count;
    }

    public int getPoint(int point) {
        return board[point];
    }

    public void bearOff(int player, int point) {
        if (player == PLAYER_1) {
            board[point] -= 1;
        } else {
            board[point] += 1;
        }
    }

    public boolean canBearOff(int player, int die) {
        int count = 0;
        if (player == PLAYER_1) {
            for (int i = 0; i < NUM_POINTS - 1; i++) {
                if (board[i] > 0) {
                    return false;
                }
            }
            for (int i = NUM_POINTS - 1; i <= BEAR_OFF; i++) {
                if (board[i] > 0) {
                    count += board[i];
                }
            }
            return count == die;
        } else {
            for (int i = 1; i < BEAR_OFF; i++) {
                if (board[i] < 0) {
                    return false;
                }
            }
            for (int i = 0; i < NUM_POINTS - 1; i++) {
                if (board[i] < 0) {
                    count -= board[i];
                }
            }
            return count == die;
        }
    }
}

