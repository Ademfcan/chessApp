package chessengine;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.apache.arrow.flatbuf.Int;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class pieceLocationHandler {

    private int[] peicesOnBoard = {8,2,2,2,1,1,8,2,2,2,1,1};
    private long blackPawns = 0b0000000000000000000000000000000000000000000000001111111100000000L;
    private long blackKnights = 0b0000000000000000000000000000000000000000000000000000000001000010L;
    private long blackBishops = 0b00000000000000000000000000000000000000000000000000000000000100100L;
    private long blackRooks = 0b0000000000000000000000000000000000000000000000000000000010000001L;
    private long blackQueens = 0b0000000000000000000000000000000000000000000000000000000000001000L;
    private long blackKings = 0b0000000000000000000000000000000000000000000000000000000000010000L;

    private long whitePawns = 0b0000000011111111000000000000000000000000000000000000000000000000L;
    private long whiteKnights = 0b0100001000000000000000000000000000000000000000000000000000000000L;
    private long whiteBishops = 0b0010010000000000000000000000000000000000000000000000000000000000L;
    private long whiteRooks = 0b1000000100000000000000000000000000000000000000000000000000000000L;
    private long whiteQueens = 0b0000100000000000000000000000000000000000000000000000000000000000L;
    private long whiteKings = 0b0001000000000000000000000000000000000000000000000000000000000000L;

    // Flipped positioning
    public long[] blackPieces = {blackPawns, blackKnights, blackBishops, blackRooks, blackQueens, blackKings};
    public long[] whitePieces = {whitePawns, whiteKnights, whiteBishops, whiteRooks, whiteQueens, whiteKings};

    private long[] blackPiecesStart = {blackPawns, blackKnights, blackBishops, blackRooks, blackQueens, blackKings};
    private long[] whitePiecesStart = {whitePawns, whiteKnights, whiteBishops, whiteRooks, whiteQueens, whiteKings};


    private ArrayList<long[][]> boardSave = new ArrayList<long[][]>();

    public int moveIndx = -1;
    public int maxIndex = -1;

    private int blackCastleIndx = 1000;
    private int whiteCastleIndx = 1000;

    private boolean whiteCastleRight = true;
    public boolean blackCastleRight = true;

    private boolean whiteShortRookMove = true;
    private boolean whiteLongRookMove = true;
    public boolean blackShortRookMove = true;
    private boolean blackLongRookMove = true;

    private int whiteShortRookIndx = 1000;
    private int whiteLongRookIndx = 1000;
    private int blackShortRookIndx = 1000;
    private int blackLongRookIndx = 1000;



    public pieceLocationHandler() {
    }

    public void removePeice(boolean isWhite,int x ,int y){
        int from  = positionToBitIndex(x, y);
        long mask = ~(1L << from); // Create a mask with a 0 at the index to be cleared
        int indx = getBoardWithPiece(x,y,isWhite);
        long currbitboard = 0L;

        if(isWhite){
            currbitboard = whitePieces[indx];
        }
        else{
            currbitboard = blackPieces[indx];

        }


        currbitboard = (currbitboard & mask);

        if(isWhite){
            whitePieces[indx] = currbitboard;
        }
        else{
            blackPieces[indx] = currbitboard;

        }
        int jump = isWhite ? 0 : 6;
        createBoardEntry();

        peicesOnBoard[jump+indx]--;
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

            case 4:
                return calculateQueenMoves(x,y,isWhite);

            case 5:
                return calculateKingMoves(x,y,isWhite);


        }
        return null;
    }




    public void movePiece(boolean isWhite, int OldX, int OldY, int NewX, int NewY, boolean isRemove){
        int from  = positionToBitIndex(OldX, OldY);
        int to  = positionToBitIndex(NewX, NewY);
        long clearMask = ~(1L << from);

        // Set the bit at 'to' position to 1 using a left shift.
        long setBit = 1L << to;
        long currbitboard = 0L;

        int indx = getBoardWithPiece(OldX,OldY,isWhite);
        if(isWhite){
            currbitboard = whitePieces[indx];
        }
        else{
            currbitboard = blackPieces[indx];

        }


        // Use the mask to clear the 'from' bit and then set the 'to' bit.
        currbitboard = (currbitboard & clearMask) | setBit;

        if(isWhite){
            whitePieces[indx] = currbitboard;
        }
        else{
            blackPieces[indx] = currbitboard;

        }

        if(!isRemove){
            createBoardEntry();
        }



    }

    private long createFullBoard(){
        Long board = 0L;
        for(long l : whitePieces){
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
            for(long l : whitePieces){
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

    public String getPieceType(int indx){
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
        for(long l : whitePieces){
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
            for(long l : whitePieces){
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
        if(isWhite){
            for(int i = 0; i<whitePieces.length;i++){
                long sum = bitIndex & whitePieces[i];
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
                    boolean[] result2 = checkIfContains(newX, newY, !isWhite);
                    if(result2[0]){
                        moves.add(newX + "," + newY);
                        break;
                    }
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
                    boolean[] result2 = checkIfContains(newX, newY, !isWhite);
                    if(result2[0]){
                        moves.add(newX + "," + newY);
                        break;
                    }
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
        boolean canCastle = isWhite ? whiteCastleRight : blackCastleRight;
        boolean shortRook = isWhite ? whiteShortRookMove : blackShortRookMove;
        boolean longRook = isWhite ? whiteLongRookMove : blackLongRookMove;

        if(canCastle){
            // short castle
            if(!checkIfContains(x+1,y,isWhite)[0] && !checkIfContains(x+2,y,isWhite)[0] && shortRook){
                moves.add((x+2) + "," + y + ",c");
            }
            if(!checkIfContains(x-1,y,isWhite)[0] && !checkIfContains(x-2,y,isWhite)[0] && !checkIfContains(x-3,y,isWhite)[0] && longRook){
                moves.add((x-3) + "," + y + ",c");

            }
        }

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

    private final String[] rookLocations = {"7,7,s,w","0,7,l,w","0,0,l,b","7,0,s,b"};
    public void removeRookMoveRight(int x, int y){
        for(String s : rookLocations){
            String[] rInfo = s.split(",");
            int rookX = Integer.parseInt(rInfo[0]);
            int rookY = Integer.parseInt(rInfo[1]);
            if(rookX == x && rookY == y){
                System.out.println(s);
                if(rInfo[2].equals("s")){
                    if(rInfo[3].equals("w")){
                        whiteShortRookIndx = moveIndx;
                        whiteShortRookMove = false;
                    }
                    else{
                        blackShortRookIndx = moveIndx;
                        blackShortRookMove = false;
                    }
                }
                else{
                    if(rInfo[3].equals("w")){
                        whiteLongRookIndx = moveIndx;
                        whiteLongRookMove = false;
                    }
                    else{
                        blackShortRookIndx = moveIndx;
                        blackShortRookMove = false;
                    }
                }
            }
        }
    }

    public void removeCastlingRight(boolean isWhite){
        System.out.println("rm cr: " +isWhite);
        if(isWhite){
            if(whiteCastleRight){
                whiteCastleRight = false;
                whiteCastleIndx = moveIndx;
            }

        }
        else{
            if(blackCastleRight){
                blackCastleRight = false;
                blackCastleIndx = moveIndx;
            }

        }

    }

    private boolean isValidMove(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }
    int[] valueMap = {1,3,3,5,9,100000};

    public int getSimpleEval(){
        int eval = 0;
        for(int i =0 ;i<peicesOnBoard.length;i++){
            if(i < 6){
                eval += peicesOnBoard[i] * valueMap[i];
            }
            else{
                eval -= peicesOnBoard[i] * valueMap[i-6];
            }
        }
        return eval;

    }
    private double[][] pawnMap = {{1,1,1,1,1,1,1,1},
                                {.9f,.9f,.9f,.9f,.9f,.9f,.9f,.9f},
                                {1,1,1,1,1,1,1,1},
                                {1.1f,1.1f,1.1f,1.1f,1.1f,1.1f,1.1f,1.1f},
                                {1.15f,1.15f,1.15f,1.15f,1.15f,1.15f,1.15f,1.15f},
                                {1.25f,1.25f,1.25f,1.25f,1.25f,1.25f,1.25f,1.25f},
                                {1.5f,1.5f,1.5f,1.5f,1.5f,1.5f,1.5f,1.5f},
                                {2.5f,2.5f,2.5f,2.5f,2.5f,2.5f,2.5f,2.5f}};
    private double[][] knightMap = {
            {0.5f, 0.6f, 0.7f, 0.7f, 0.7f, 0.7f, 0.6f, 0.5f},
            {0.6f, 0.8f, 1.0f, 1.0f, 1.0f, 1.0f, 0.8f, 0.6f},
            {0.7f, 1.0f, 1.5f, 1.5f, 1.5f, 1.5f, 1.0f, 0.7f},
            {0.7f, 1.0f, 1.5f, 2.0f, 2.0f, 1.5f, 1.0f, 0.7f},
            {0.7f, 1.0f, 1.5f, 2.0f, 2.0f, 1.5f, 1.0f, 0.7f},
            {0.7f, 1.0f, 1.5f, 1.5f, 1.5f, 1.5f, 1.0f, 0.7f},
            {0.6f, 0.8f, 1.0f, 1.0f, 1.0f, 1.0f, 0.8f, 0.6f},
            {0.5f, 0.6f, 0.7f, 0.7f, 0.7f, 0.7f, 0.6f, 0.5f}
    };

    private double[][] bishopMap = {
            {0.0f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.0f},
            {0.2f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.2f},
            {0.2f, 1.0f, 1.5f, 2.0f, 2.0f, 1.5f, 1.0f, 0.2f},
            {0.2f, 1.5f, 1.5f, 2.0f, 2.0f, 1.5f, 1.5f, 0.2f},
            {0.2f, 1.0f, 2.0f, 2.0f, 2.0f, 2.0f, 1.0f, 0.2f},
            {0.2f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 0.2f},
            {0.2f, 1.5f, 1.0f, 1.0f, 1.0f, 1.0f, 1.5f, 0.2f},
            {0.0f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.0f}
    };

    private double[][] rookMap = {
            {1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
            {1.5f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 1.5f},
            {0.5f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.5f},
            {0.5f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.5f},
            {0.5f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.5f},
            {0.5f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.5f},
            {0.5f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.5f},
            {1.0f, 1.0f, 1.0f, 1.5f, 1.5f, 1.0f, 1.0f, 1.0f}
    };

    private double[][] kingMap = {
            {2.0f, 1.0f, 1.0f, 0.5f, 0.5f, 1.0f, 1.0f, 2.0f},
            {1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f},
            {1.0f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 0.0f, 1.0f},
            {0.5f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 0.0f, 0.5f},
            {0.0f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 0.0f, 0.0f},
            {1.0f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.0f, 1.0f},
            {1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f},
            {2.0f, 1.0f, 1.0f, 0.5f, 0.5f, 1.0f, 1.0f, 2.0f}
    };

    private double[][] queenMap = {
            {3.0f, 4.0f, 4.0f, 5.0f, 5.0f, 4.0f, 4.0f, 3.0f},
            {3.0f, 4.0f, 4.0f, 5.0f, 5.0f, 4.0f, 4.0f, 3.0f},
            {3.0f, 4.0f, 4.0f, 5.0f, 5.0f, 4.0f, 4.0f, 3.0f},
            {3.0f, 4.0f, 4.0f, 5.0f, 5.0f, 4.0f, 4.0f, 3.0f},
            {2.0f, 3.0f, 3.0f, 4.0f, 4.0f, 3.0f, 3.0f, 2.0f},
            {1.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 1.0f},
            {-2.0f, -2.0f, 0.0f, 0.0f, 0.0f, 0.0f, -2.0f, -2.0f},
            {-2.0f, -3.0f, -1.0f, 0.0f, 0.0f, -1.0f, -3.0f, -2.0f}
    };

    double[][][] maps = {pawnMap,knightMap,bishopMap,rookMap,queenMap,kingMap};


    int[] countW = new int[6];
    int[] countB = new int[6];

    public double getFullEval(long[] blackP, long[] whitep){
        // todo: fix board reversing so these are balanced
        Arrays.fill(countW,0);
        Arrays.fill(countB,0);
        System.out.println(whitep[0]);
        double sum1 = 0;
        for(int i = 0; i< whitep.length; i++){
            List<String> coords = getPieceCoords(whitep[i]);
            for(String s : coords){
                String[] coord = s.split(",");
                System.out.println("W: " + s);

                // reverse coordinates to match white peices
                // todo
                int Normx = Integer.parseInt(coord[0]);
                int Normy = 7-Integer.parseInt(coord[1]);
                sum1 += valueMap[i];// * maps[i][Normx][Normy];
                System.out.println(valueMap[i]);

                countW[i]++;


            }
        }
        double sum2 = 0;
        for(int i = 0; i< blackP.length; i++){
            List<String> coords = getPieceCoords(blackP[i]);
            for(String s : coords){
                String[] coord = s.split(",");
                System.out.println("b: " +s);

                // reverse coordinates to match white peices
                int x = Integer.parseInt(coord[0]);

                int y = Integer.parseInt(coord[1]);
                System.out.println(valueMap[i]);
                sum2 += valueMap[i];// * maps[i][x][y];
                countB[i]++;


            }

        }
        System.out.println(sum1);
        System.out.println(sum2);

        for(int c : countW){
            System.out.print(c+ " ");
        }
        System.out.println();
        for(int c : countB){
            System.out.print(c + " ");
        }

        return sum1 - sum2;

    }


    private void createBoardEntry(){
        if (moveIndx != boardSave.size() - 1) {
            clearIndx();
        }
        System.out.println("Saving..");


        boardSave.add(new long[][]{Arrays.copyOf(whitePieces,whitePieces.length),Arrays.copyOf(blackPieces,blackPieces.length)});


        moveIndx ++;
        maxIndex = moveIndx;

    }

    public void clearIndx(){
        System.out.println("Removing old entries");
        int to = boardSave.size();
        for (int i = to - 1; i > moveIndx; i--) {
            boardSave.remove(i);
        }


    }

    private long[][] getPeicesFromSave(){
        long[] whitePeicesOld;
        long[] blackPeicesOld;
        if(moveIndx < 0){
            whitePeicesOld = whitePiecesStart;
            blackPeicesOld = blackPiecesStart;
        }
        else{

            whitePeicesOld = boardSave.get(moveIndx)[0];
            blackPeicesOld = boardSave.get(moveIndx)[1];
        }


        return new long[][] {whitePeicesOld,blackPeicesOld};


    }

    public void ChangeBoard(GridPane chessPeiceBoard, ImageView[][] pieceLocations, Boolean isWhiteTurn){
        whiteCastleRight = moveIndx <= whiteCastleIndx;
        blackCastleRight = moveIndx <= blackCastleIndx;
        whiteLongRookMove = moveIndx <= whiteLongRookIndx;
        whiteShortRookMove = moveIndx <= whiteShortRookIndx;
        blackLongRookMove = moveIndx <= blackLongRookIndx;
        blackShortRookMove = moveIndx <= blackShortRookIndx;

        List<String>[] changes = getChangesNeeded();
        List<String> thingsToAdd = changes[0];
        List<String> thingsToRemove = changes[1];
        MatrixToString(pieceLocations);
        System.out.println(whitePieces[0]);

        System.out.println("Things to add size : " + thingsToAdd.size());
        System.out.println("Things to remove size : " + thingsToRemove.size());
        for(int i = 0; i<thingsToAdd.size();i++){
            System.out.println(i + " Add: " + thingsToAdd.get(i));
        }for(int i = 0; i<thingsToRemove.size();i++){
            System.out.println(i + " Rem: " + thingsToRemove.get(i));
        }

        int i = 0;
        int z = 0;

        while(z < thingsToRemove.size()){
            // edge case where you need to remove more to the board
            String[] Delinfo = thingsToRemove.get(z).split(",");
            int OldX = Integer.parseInt(Delinfo[0]);
            int OldY = Integer.parseInt(Delinfo[1]);
            boolean isWhite = Delinfo[2].equals("w");
            int brdRmvIndex = Integer.parseInt(Delinfo[3]);
            removeFromGridPane(OldX,OldY,chessPeiceBoard);
            pieceLocations[OldX][OldY] = null;
            removePeice(brdRmvIndex,positionToBitIndex(OldX,OldY),isWhite);
            z++;

        }
        while(i < thingsToAdd.size()){
            // edge case where you need to add more to the board
            String[] Moveinfo = thingsToAdd.get(i).split(",");
            int NewX = Integer.parseInt(Moveinfo[0]);
            int NewY = Integer.parseInt(Moveinfo[1]);
            int brdAddIndex = Integer.parseInt(Moveinfo[3]);
            boolean isWhite = Moveinfo[2].equals("w");
            ImageView peice = createNewPeice(brdAddIndex,isWhite,chessPeiceBoard);
            chessPeiceBoard.add(peice,NewX,NewY);
            pieceLocations[NewX][NewY] = peice;
            addPiece(brdAddIndex,positionToBitIndex(NewX,NewY),isWhite);
            i++;


        }
        isWhiteTurn = moveIndx % 2 != 0;
        App.controller.isWhiteTurn = isWhiteTurn;

        MatrixToString(pieceLocations);
        System.out.println(whitePieces[0]);

    }


    private List<String>[] getChangesNeeded(){
        long[][] save = getPeicesFromSave();
        long[] whitePeicesOld = save[0];
        long[] blackPeicesOld = save[1];
        List<String> changesAdd = new ArrayList<>();
        List<String> changesRemove = new ArrayList<>();
        for(int i = 0; i<whitePieces.length;i++){
            long old = whitePeicesOld[i];
            long cur = whitePieces[i];
            //System.out.println(whitePeicesOld[i]);
            //System.out.println(whitePieces[i]);
            long xorResult = old ^ cur;

            // Find missing bit indices to add
            for (int z = 0; z < 64; z++) {
                long mask = 1L << z;
                if ((xorResult & mask) != 0 && (old & mask) != 0) {
                    int[] coords = bitindexToXY(z);
                    changesAdd.add(coords[0] + "," + coords[1] + ",w," + i);
                }
                if ((xorResult & mask) != 0 && (cur  & mask) != 0) {
                    int[] coords = bitindexToXY(z);
                    changesRemove.add(coords[0] + "," + coords[1] + ",w," + i);
                }
            }
            // Find missing bit indices to remove


        }
        for(int i = 0; i<blackPieces.length;i++){
            long old = blackPeicesOld[i];
            long cur = blackPieces[i];

            long xorResult = old ^ cur;

            // Find missing bit indices to add
            for (int z = 0; z < 64; z++) {
                long mask = 1L << z;
                if ((xorResult & mask) != 0 && (old & mask) != 0) {
                    int[] coords = bitindexToXY(z);
                    changesAdd.add(coords[0] + "," + coords[1] + ",b," + i);
                }
                if ((xorResult & mask) != 0 && (cur  & mask) != 0) {
                    int[] coords = bitindexToXY(z);
                    changesRemove.add(coords[0] + "," + coords[1] + ",b," + i);
                }
            }
            // Find missing bit indices to remove


        }
        return new List[]{changesAdd,changesRemove};
    }

    public void updateMoveIndex(int amnt){
        moveIndx += amnt;
    }



    private int[] bitindexToXY(int bitIndex){
        return new int[] {bitIndex%8, bitIndex/8};
    }

    private List<String> getPieceCoords(long board) {
        List<String> coord = new ArrayList<>();

        for (int z = 0; z < 64; z++) {
            long mask = 1L << z;

            if ((board & mask) != 0) {
                int[] coords = bitindexToXY(z);
                coord.add(coords[0] + "," + coords[1]);
            }
        }

        return coord;
    }

    private void addPiece(int boardIndx, int bitIndex, boolean isWhite) {
        // Create a mask with the bit at bitIndex set to 1
        long board = isWhite ? whitePieces[boardIndx] : blackPieces[boardIndx];

        long mask = 1L << bitIndex;
        long result = board | mask;
        if(isWhite){
            whitePieces[boardIndx] = result;
        }
        else{
            blackPieces[boardIndx] =result;
        }
        int jump = isWhite ? 0 : 6;
        peicesOnBoard[jump+boardIndx]++;
        System.out.println("Adding peice");
        // Use bitwise OR to add the piece to the board
    }
    private void removePeice(int boardIndx, int bitIndex, boolean isWhite) {
        // Create a mask with the bit at bitIndex set to 1
        long board = isWhite ? whitePieces[boardIndx] : blackPieces[boardIndx];


        long mask = 1L << bitIndex;
        long result = board & ~mask;
        if(isWhite){
            whitePieces[boardIndx] = result;
        }
        else{
            blackPieces[boardIndx] =result;
        }
        int jump = isWhite ? 0 : 6;
        peicesOnBoard[jump+boardIndx]--;
        System.out.println("Removing peice");

    }

    private void MatrixToString(ImageView[][] matrix){
        for(int i = 0; i< matrix.length;i++){
            for(int j = 0; j< matrix[i].length;j++){
                System.out.print((matrix[j][i] != null ? "X" : "_" )+ " ");
            }
            System.out.println();
        }
    }

    public  void printBitboard(long bitboard) {
        System.out.println("bitboard:");
        for (int row = 7; row >= 0; row--) {
            for (int col = 0; col < 8; col++) {
                int index = row * 8 + col;
                long mask = 1L << index;
                char square = ((bitboard & mask) != 0) ? '1' : '0';
                System.out.print(square + " ");
            }
            System.out.println();
        }
        System.out.println();
    }



    private ImageView createNewPeice(int brdIndex, boolean isWhite, GridPane chessPeiceBoard){
        String restOfPath ="";
        String pathStart = isWhite ? "w_" : "b_";
        switch (brdIndex) {
            case 0:
                restOfPath = "pawn";
                break;
            case 1:
                restOfPath = "knight";
                break;

            case 2:
                restOfPath = "bishop";
                break;
            case 3:
                restOfPath = "rook";
                break;

            case 4:
                restOfPath = "queen";
                break;

            case 5:
                restOfPath = "king";
                break;


        }
        ImageView piece = new ImageView("/ChessAssets/ChessPieces/" + pathStart + restOfPath + "_1x_ns.png");

        piece.fitHeightProperty().bind(chessPeiceBoard.heightProperty().divide(9));
        piece.fitWidthProperty().bind(chessPeiceBoard.widthProperty().divide(8));
        piece.setPreserveRatio(true);


        GridPane.setHalignment(piece, HPos.CENTER);
        GridPane.setValignment(piece, VPos.CENTER);
        return piece;
    }

    private void removeFromGridPane(int x, int y, GridPane pane){
        pane.getChildren().removeIf(n -> GridPane.getColumnIndex(n) == x && GridPane.getRowIndex(n) == y);
    }






}
