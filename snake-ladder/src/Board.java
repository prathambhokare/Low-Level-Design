import java.util.*;


public class Board {
    Cell[][] cells;

    public Board(int m,int n) {
        cells = new Cell[m][n];
    }

    public void initializeBoard() {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                this.cells[i][j] = new Cell(i, j, CellType.NORMAL);
                if (i % 2 == 0 &&) {

                } else if (i % 2 != 0 &&) {

                }
            }
        }
    }

    public int getRandomNumber(int n) {

    }
}