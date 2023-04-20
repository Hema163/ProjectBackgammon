package org.example;

public class Move {
    private int from;
    private int to;

    public Move(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public String toString() {
        return "(" + from + ", " + to + ")";
    }
}

