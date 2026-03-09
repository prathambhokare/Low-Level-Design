package com.lld.model;

public class Player {

    private String name;
    private char symbol;
    private int totalGamePlayed;
    private int totalGameWin;

    public Player(String name,char symbol) {
        this.name = name;
        this.symbol=symbol;
    }

    public String getName() {
        return name;
    }

    public char getSymbol() {
        return symbol;
    }
}
