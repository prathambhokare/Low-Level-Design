package com.lld.model;

public class Board {
    private char[][] cells;

    public Board() {
        this.cells = new char[3][3];
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                this.cells[i][j] = '-';
            }
        }
    }

    public void addMove(int x,int y,char symbol) {
        if (x < 0 || y < 0 || x >= cells.length || y >= cells[0].length) {
            throw new RuntimeException("Invalid Move Played!!");
        }
        this.cells[x][y] = symbol;
    }

    public boolean isWinner(int x,int y,char symbol) {
        //check for rows
        //check for cols
        //check for diagonals
        boolean isAllSame = true;
        for (int i = 0; i < cells[0].length - 1; i++) {
            if (cells[x][i] != symbol) {
                isAllSame = false;
            }
        }
        if (isAllSame) return true;
        isAllSame = true;
        for (int j = 0; j < cells.length - 1; j++) {
            if (cells[j][y] != symbol) {
                isAllSame = false;
            }
        }
        if (isAllSame) return true;
        return false;
    }

    public void printBoard() {
        for (int i=0;i<cells.length;i++) {
            System.out.print(" | ");
            for (int j=0;j<cells[0].length;j++) {
                System.out.print(this.cells[i][j] + " | ");
            }
            System.out.println();
        }
    }
}
