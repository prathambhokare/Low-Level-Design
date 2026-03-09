public class Game {
    public String gameId;
    public GAMESTATE gamestate;
    public Board board;
    public Player playerFirst;
    public Player playerSecond;

    public Game() {
        //initialize game
        this.board = new Board();
    }

    public void startGame() {
        //assign players name
        //their symbols
    }

    public void playMove(int x,int y,char symbol) {
        if (this.board.isValid(x,y)) {
            this.board.playMove(x, y, symbol);
            if (this.board.checkWinner(x, y, symbol)) {
                //win game
            }
        }
    }

    public void cancelGame() {

    }

    public void holdGame() {

    }
}