package chessengine;

public class XYCoordinate {
    // 0 = pawn, 1 = knight, 2 = bishop, 3 = rook, 4 = queen, 5 = king
    private int boardIndex;
    private int x;
    private int y;
    public XYCoordinate(int x, int y, int boardIndex){
        this.x = x;
        this.y = y;
        this.boardIndex = boardIndex;
    }

    public int getBoardIndex() {
        return boardIndex;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean doesItMatch(int x, int y){
        return (x == this.x && y == this.y);
    }


}
