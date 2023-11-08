package chessengine;

import org.bytedeco.opencv.presets.opencv_core;

import java.util.ArrayList;
import java.util.List;

public class pieceLocationHandler {
    private long blackPawns = 0b0000000000000000000000000000000000000000000000001111111100000000L;
    private long blackKnights = 0b0000000000000000000000000000000000000000000000000000000001000010L;
    private long blackBishops = 0b00000000000000000000000000000000000000000000000000000000000100100L;
    private long blackRooks = 0b0000000000000000000000000000000000000000000000000000000010000001L;
    private long blackQueens = 0b0000000000000000000000000000000000000000000000000000000000010000L;
    private long blackKings = 0b0000000000000000000000000000000000000000000000000000000000001000L;

    private long whitePawns = 0b0000000011111111000000000000000000000000000000000000000000000000L;
    private long whiteKnights = 0b0100001000000000000000000000000000000000000000000000000000000000L;
    private long whiteBishops = 0b0010010000000000000000000000000000000000000000000000000000000000L;
    private long whiteRooks = 0b1000000100000000000000000000000000000000000000000000000000000000L;
    private long whiteQueens = 0b0001000000000000000000000000000000000000000000000000000000000000L;
    private long whiteKings = 0b0000100000000000000000000000000000000000000000000000000000000000L;

    // messed up positioning, its supposed to be flipped
    private long[] blackPieces = {blackPawns,blackKnights,blackBishops,blackRooks,blackQueens,blackKings};
    private long[] whitePeices = {whitePawns,whiteKnights,whiteBishops,whiteRooks,whiteQueens,whiteKings};

    public pieceLocationHandler() {


    }

    public void removePeice(boolean isWhite,int x ,int y){
        int from  = positionToBitIndex(x, y);
        System.out.println(from);
        long mask = ~(1L << from); // Create a mask with a 0 at the index to be cleared
        int indx = getBoardWithPiece(x,y,isWhite);
        System.out.println(indx);
        long currbitboard = 0L;

        if(isWhite){
            currbitboard = whitePeices[indx];
        }
        else{
            currbitboard = blackPieces[indx];

        }


        currbitboard = (currbitboard & mask);

        if(isWhite){
            whitePeices[indx] = currbitboard;
        }
        else{
            blackPieces[indx] = currbitboard;

        }
    }

    public List<String> getPossibleMoves(int x, int y, boolean isWhite){
        int indx = getBoardWithPiece(x,y,isWhite);
        switch (indx){
            case 0:
                return calculatePawnMoves(x,y,isWhite);
            case 1:
                return calculateKnightMoves(x,y,isWhite);

            case 2:
                return calculateBishopMoves(x,y,isWhite);

            case 3:
                return calculateRookMoves(x,y,isWhite);

            case 5:
                return calculateQueenMoves(x,y,isWhite);

            case 4:
                return calculateKingMoves(x,y,isWhite);


        }
        return null;
    }

    public void movePiece(boolean isWhite, int OldX, int OldY, int NewX, int NewY){
        System.out.println("Here");
        int from  = positionToBitIndex(OldX, OldY);
        int to  = positionToBitIndex(NewX, NewY);
        long clearMask = ~(1L << from);

        // Set the bit at 'to' position to 1 using a left shift.
        long setBit = 1L << to;
        long currbitboard = 0L;

        int indx = getBoardWithPiece(OldX,OldY,isWhite);
        if(isWhite){
            currbitboard = whitePeices[indx];
        }
        else{
            currbitboard = blackPieces[indx];

        }


        // Use the mask to clear the 'from' bit and then set the 'to' bit.
        currbitboard = (currbitboard & clearMask) | setBit;

        if(isWhite){
            whitePeices[indx] = currbitboard;
        }
        else{
            blackPieces[indx] = currbitboard;

        }



    }

    private long createFullBoard(){
        Long board = 0L;
        for(long l : whitePeices){
            board = board | l;
        }
        for(long l : blackPieces){
            board = board | l;
        }
        return board;
    }

    private long createFullBoard(boolean isWhite){
        long board = 0L;
        if(isWhite){
            for(long l : whitePeices){
                board = board | l;
            }
        }
        else{
            for(long l : blackPieces){
                board = board | l;
            }
        }


        return board;
    }

    public String getPieceType(int x, int y, Boolean isWhite){
       int indx = getBoardWithPiece(x,y,isWhite);
       switch (indx){
           case 0:
               return "Pawn";
           case 1:
               return "Knight";
           case 2:
               return "Bishop";
           case 3:
               return "Rook";
           case 4:
               return "Queen";
           case 5:
               return "King";

       }
       return null;
    }

    public boolean[] checkIfContains(int x, int y){
        long board  = positionToBitboard(x,y);
        for(long l : whitePeices){
            long sum = board & l;
            if(sum != 0L){
                return new boolean[]{true, true};
            }
        }
        for(long l : blackPieces){
            long sum = board & l;
            if(sum != 0L){
                return new boolean[]{true, false};
            }
        }
        return new boolean[]{false,false};
    }

    public boolean[] checkIfContains(int x, int y, boolean isWhite){
        long board  = positionToBitboard(x,y);
        if(isWhite){
            for(long l : whitePeices){
                long sum = board & l;
                if(sum != 0L){
                    return new boolean[]{true, true};
                }
            }
        }
        else{
            for(long l : blackPieces){
                long sum = board & l;
                if(sum != 0L){
                    return new boolean[]{true, false};
                }
            }
        }
        return new boolean[]{false,false};
    }

    private int positionToBitIndex(int x, int y){
        return  x + y * 8;
    }

    private long positionToBitboard(int x, int y) {


        // Calculate the index of the bit corresponding to the (x, y) position.
        int bitIndex = x + y * 8;

        // Create a long with the corresponding bit set to 1.
        return 1L << bitIndex;
    }

    private int getBoardWithPiece(int x, int y, Boolean isWhite){
        long bitIndex = positionToBitboard(x,y);
        System.out.println(bitIndex);
        if(isWhite){
            for(int i = 0; i<whitePeices.length;i++){
                long sum = bitIndex & whitePeices[i];
                if(sum != 0L){
                    return i;
                }
            }
        }
        else{
            for(int i = 0; i<blackPieces.length;i++){
                long sum = bitIndex & blackPieces[i];
                if(sum != 0L){
                    return i;
                }
            }
        }


        return -10;
    }
    int pawnHome;
    int move;
    int eatY;
    int eatX1;
    int eatX2;
    private List<String> calculatePawnMoves(int x, int y, boolean isWhite){
        ArrayList<String> moves = new ArrayList<>();
        pawnHome = isWhite ? 6 : 1;
        move = isWhite ? -1 : 1;
        eatY = y + move;
        eatX1 = x + 1;
        eatX2 = x - 1;
        if(checkIfContains(eatX1,eatY,!isWhite)[0]){
            // pawn can capture to the right
            moves.add(Integer.toString(eatX1) + "," + Integer.toString(eatY));
        }
        if(checkIfContains(eatX2,eatY,!isWhite)[0]){
            // pawn can capture to the left
            moves.add(Integer.toString(eatX2) + "," + Integer.toString(eatY));
        }
        int depth = 1;
        if(y == pawnHome){
            depth = 2;
        }
        for(int i = 1; i< depth+1;i++){
            int newY = y + i*move;
            // pawns cannot eat forwards
            if(!checkIfContains(x,newY,isWhite)[0] && !checkIfContains(x,newY,!isWhite)[0]){
                // pawn can capture to the right
                moves.add(Integer.toString(x) + "," + Integer.toString(newY));
            }
            else{
                break;
            }
        }
        return moves;

    }

    private List<String> calculateKnightMoves(int x, int y, boolean isWhite) {
        ArrayList<String> moves = new ArrayList<>();

        int[] dx = {1, 2, 2, 1, -1, -2, -2, -1};
        int[] dy = {-2, -1, 1, 2, 2, 1, -1, -2};

        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (isValidMove(newX, newY) && !checkIfContains(newX, newY, isWhite)[0]) {
                moves.add(newX + "," + newY);
            }
        }

        return moves;
    }

    private List<String> calculateBishopMoves(int x, int y, boolean isWhite) {
        ArrayList<String> moves = new ArrayList();

        int[] dx = {1, 1, -1, -1};
        int[] dy = {1, -1, 1, -1};

        for (int i = 0; i < 4; i++) {
            int newX = x;
            int newY = y;
            while (true) {
                newX += dx[i];
                newY += dy[i];
                if (isValidMove(newX, newY)) {
                    boolean[] result = checkIfContains(newX, newY, isWhite);
                    if (!result[0]) {
                        moves.add(newX + "," + newY);
                    }
                    else{
                        break;
                    }
                } else {
                    break;
                }
            }
        }

        return moves;
    }

    private List<String> calculateRookMoves(int x, int y, boolean isWhite) {
        ArrayList<String> moves = new ArrayList();

        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        for (int i = 0; i < 4; i++) {
            int newX = x;
            int newY = y;
            while (true) {
                newX += dx[i];
                newY += dy[i];
                if (isValidMove(newX, newY)) {
                    boolean[] result = checkIfContains(newX, newY, isWhite);
                    if (!result[0]) {
                        moves.add(newX + "," + newY);
                    }
                    else{
                        break;
                    }
                } else {
                    break;
                }
            }
        }

        return moves;
    }

    private List<String> calculateQueenMoves(int x, int y, boolean isWhite) {
        ArrayList<String> moves = new ArrayList();

        List<String> rookMoves = calculateRookMoves(x, y, isWhite);
        List<String> bishopMoves = calculateBishopMoves(x, y, isWhite);

        moves.addAll(rookMoves);
        moves.addAll(bishopMoves);

        return moves;
    }

    private List<String> calculateKingMoves(int x, int y, boolean isWhite) {
        ArrayList<String> moves = new ArrayList<>();

        int[] dx = {1, -1, 0, 0, 1, -1, 1, -1};
        int[] dy = {0, 0, 1, -1, 1, -1, -1, 1};

        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (isValidMove(newX, newY) && !checkIfContains(newX, newY, isWhite)[0]) {
                moves.add(newX + "," + newY);
            }
        }

        return moves;
    }

    private boolean isValidMove(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

}
