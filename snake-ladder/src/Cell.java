import java.util.*;


public class Cell {
    int row;
    int col;
    CellType cellType;

    public Cell(int row, int col, CellType cellType) {
        this.row=row;
        this.col=col;
        this.cellType=cellType;
    }
}