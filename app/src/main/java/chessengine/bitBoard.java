package chessengine;

public class bitBoard {
    private long bitboard;

    public bitBoard(long bitboard){
        this.bitboard = bitboard;
    }

    public void AddPeice(int x, int y){
        AddPeice(positionToBitIndex(x,y));
    }

    // use bitwise OR operator to add a bit representation of piece at the bitIndex
    public void AddPeice(int bitIndex){
        this.bitboard = this.bitboard | (1L << bitIndex);
    }

    public void RemovePeice(int x, int y){
        RemovePeice(positionToBitIndex(x,y));
    }
    // use bitwise And operator to remove a bit representation of piece at the bitIndex
    public void RemovePeice(int bitIndex){
        this.bitboard = this.bitboard & ~(1L << bitIndex);
    }

    public boolean checkIfContains(int x, int y){
        return checkIfContains(positionToBitIndex(x,y));
    }

    public boolean checkIfContains(int bitIndex){
        return (positionToBitboard(bitIndex) & this.bitboard) != 0L;
    }

    private long positionToBitboard(int bitIndex) {
        // Create a long with the corresponding bit set to 1.
        return 1L << bitIndex;
    }



    private int positionToBitIndex(int x, int y){
        return  x + y * 8;
    }




}
