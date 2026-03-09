package com.lld.model;

public class Game {

    private String gameId;
    private Board board;
    private GameState gameState;
    private Player playerFirst;
    private Player playerSecond;
    private Player currentPlayer;
    private boolean turn;

    public Game() {
        this.gameState = GameState.START;
    }

    public String getGameState() {
        return this.gameState.name();
    }

    public void startGame(Player playerFirst,Player playerSecond,
                          boolean turn) {
        this.gameState = GameState.IN_PROGRESS;
        this.playerFirst = playerFirst;
        this.playerSecond = playerSecond;
        if (turn) {
            this.currentPlayer = playerFirst;
        } else {
            this.currentPlayer = playerSecond;
        }
        this.board = new Board();
    }

    public void makeMove(int x,int y) {
        this.board.addMove(x,y,currentPlayer.getSymbol());
        this.board.printBoard();
        if (this.board.isWinner(x,y, currentPlayer.getSymbol())) {
            System.out.printf("The Player %s Win!!!%n", currentPlayer.getName());
            this.gameState = GameState.FINISHED;
            return;
        }
        turn=!turn;
        if (turn) {
            this.currentPlayer = playerFirst;
        } else {
            this.currentPlayer = playerSecond;
        }
    }

    public void holdGame() {
        this.gameState = GameState.HOLD;
    }

    public void cancelledGame() {
        this.gameState = GameState.CANCELLED;
    }
}
