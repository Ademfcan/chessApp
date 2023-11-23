package chessengine;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;


import java.util.*;

public class pieceLocationHandler {

    private int[] peicesOnBoard = {8,2,2,2,1,1,8,2,2,2,1,1};
    private final long blackPawns = 0b0000000000000000000000000000000000000000000000001111111100000000L;
    private final long blackKnights = 0b0000000000000000000000000000000000000000000000000000000001000010L;
    private final long blackBishops = 0b00000000000000000000000000000000000000000000000000000000000100100L;
    private final long blackRooks = 0b0000000000000000000000000000000000000000000000000000000010000001L;
    private final long blackQueens = 0b0000000000000000000000000000000000000000000000000000000000001000L;
    private final long blackKings = 0b0000000000000000000000000000000000000000000000000000000000010000L;

    private final long whitePawns = 0b0000000011111111000000000000000000000000000000000000000000000000L;
    private final long whiteKnights = 0b0100001000000000000000000000000000000000000000000000000000000000L;
    private final long whiteBishops = 0b0010010000000000000000000000000000000000000000000000000000000000L;
    private final long whiteRooks = 0b1000000100000000000000000000000000000000000000000000000000000000L;
    private final long whiteQueens = 0b0000100000000000000000000000000000000000000000000000000000000000L;
    private final long whiteKings = 0b0001000000000000000000000000000000000000000000000000000000000000L;

    // Flipped positioning
    public long[] blackPiecesC = {blackPawns, blackKnights, blackBishops, blackRooks, blackQueens, blackKings};
    public long[] whitePiecesC = {whitePawns, whiteKnights, whiteBishops, whiteRooks, whiteQueens, whiteKings};

    private final long[] blackPiecesStart = {blackPawns, blackKnights, blackBishops, blackRooks, blackQueens, blackKings};
    private final long[] whitePiecesStart = {whitePawns, whiteKnights, whiteBishops, whiteRooks, whiteQueens, whiteKings};


    private ArrayList<long[][]> boardSave = new ArrayList<>();

    public int moveIndx = -1;
    public int maxIndex = -1;



    private boolean whiteCastleRight = true;
    public boolean blackCastleRight = true;

    private boolean whiteShortRookMove = true;
    private boolean whiteLongRookMove = true;
    public boolean blackShortRookMove = true;
    private boolean blackLongRookMove = true;
    private int blackCastleIndx = 1000;
    private int whiteCastleIndx = 1000;
    private int whiteShortRookIndx = 1000;
    private int whiteLongRookIndx = 1000;
    private int blackShortRookIndx = 1000;
    private int blackLongRookIndx = 1000;

    private boolean gameOver;

    private HBox eatenWhites;
    private HBox eatenBlacks;

    private GridPane chessPeiceBoard;



    public pieceLocationHandler(boolean gameOver, HBox eatenWhites, HBox eatenBlacks, GridPane chessPeiceboard) {
        this.gameOver = gameOver;
        this.eatenWhites = eatenWhites;
        this.eatenBlacks = eatenBlacks;
        this.chessPeiceBoard = chessPeiceboard;
    }

    public void removePeice(boolean isWhite,int x ,int y,boolean istest, long[] whitePieces, long[] blackPieces){
        int from  = positionToBitIndex(x, y);
        long mask = ~(1L << from); // Create a mask with a 0 at the index to be cleared
        int indx = getBoardWithPiece(x,y,isWhite,whitePieces,blackPieces);


        long currbitboard;

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
        if(!istest){
            ImageView smallPeice = createNewPeice(indx,isWhite,chessPeiceBoard,true);
            smallPeice.setUserData(Integer.toString(indx));
            int jump = isWhite ? 0 : 6;
            createBoardEntry(whitePieces,blackPieces);
            if(isWhite){
                eatenWhites.getChildren().add(smallPeice);
            }
            else{
                eatenBlacks.getChildren().add(smallPeice);
            }

            peicesOnBoard[jump+indx]--;
        }
    }



    public List<String> getPossibleMoves(int x, int y, boolean isWhite, long[] whitePieces, long[] blackPieces){
        int indx = getBoardWithPiece(x,y,isWhite,whitePieces,blackPieces);
        List<String> baseMoves = getMoveOfType(x,y,isWhite,indx,whitePieces,blackPieces);

        if(isChecked(isWhite,whitePieces,blackPieces)){
            if(indx != 5){
                baseMoves.retainAll(getCheckedFile(isWhite,whitePieces,blackPieces));

            }
            return baseMoves;
        }
        else{
            return baseMoves;
        }

    }

    private List<String> getMoveOfType(int x, int y, boolean isWhite, int indx, long[] whitePieces, long[] blackPieces){
        return switch (indx) {
            case 0 -> calculatePawnMoves(x, y, isWhite,whitePieces,blackPieces);
            case 1 -> calculateKnightMoves(x, y, isWhite, false,whitePieces,blackPieces);
            case 2 -> calculateBishopMoves(x, y, isWhite, false,false,whitePieces,blackPieces);
            case 3 -> calculateRookMoves(x, y, isWhite, false,false,whitePieces,blackPieces);
            case 4 -> calculateQueenMoves(x, y, isWhite, false,whitePieces,blackPieces);
            case 5 -> calculateKingMoves(x, y, isWhite,whitePieces,blackPieces);
            default -> null;
        };
    }


    public void movePiece(boolean isWhite, int OldX, int OldY, int NewX, int NewY,int indx ,boolean isRemove,boolean isTest, long[] whitePieces, long[] blackPieces){
        int from  = positionToBitIndex(OldX, OldY);
        int to  = positionToBitIndex(NewX, NewY);
        long clearMask = ~(1L << from);

        // Set the bit at 'to' position to 1 using a left shift.
        long setBit = 1L << to;
        long currbitboard;

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

        if(!isRemove && !isTest){
            createBoardEntry(whitePieces,blackPieces);
        }

    }


    public void movePiece(boolean isWhite, int OldX, int OldY, int NewX, int NewY, boolean isRemove,boolean isTest, long[] whitePieces, long[] blackPieces){
        int indx = getBoardWithPiece(OldX,OldY,isWhite,whitePieces,blackPieces);
        movePiece(isWhite,OldX,OldY,NewX,NewY,indx,isRemove,isTest,whitePieces,blackPieces);




    }

    public long createFullBoard(long[] whitePieces, long[] blackPieces){
        long board = 0L;
        for(long l : whitePieces){
            board = board | l;
        }
        for(long l : blackPieces){
            board = board | l;
        }
        return board;
    }

    private long createFullBoard(boolean isWhite,long[] whitePieces, long[] blackPieces){
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

    public String getPieceType(int x, int y, Boolean isWhite,long[] whitePieces, long[] blackPieces){
       int indx = getBoardWithPiece(x,y,isWhite,whitePieces,blackPieces);
        return switch (indx) {
            case 0 -> "Pawn";
            case 1 -> "Knight";
            case 2 -> "Bishop";
            case 3 -> "Rook";
            case 4 -> "Queen";
            case 5 -> "King";
            default -> "null";
        };
    }

    public String getPieceType(int indx){
        return switch (indx) {
            case 0 -> "Pawn";
            case 1 -> "Knight";
            case 2 -> "Bishop";
            case 3 -> "Rook";
            case 4 -> "Queen";
            case 5 -> "King";
            default -> null;
        };
    }

    /// this
    public boolean[] checkIfContains(int x, int y, long[] whitePieces, long[] blackPieces){
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

    public boolean checkIfContains(int x, int y, boolean isWhite, long[] whitePieces, long[] blackPieces){
        long board  = positionToBitboard(x,y);
        if(isWhite){
            for(long l : whitePieces){
                long sum = board & l;
                if(sum != 0L){
                    return true;
                }
            }
        }
        else{
            for(long l : blackPieces){
                long sum = board & l;
                if(sum != 0L){
                    return true;
                }
            }
        }
        return false;
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

    private int getBoardWithPiece(int x, int y, boolean isWhite, long[] whitePieces, long[] blackPieces){
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
    private List<String> calculatePawnMoves(int x, int y, boolean isWhite, long[] whitePieces, long[] blackPieces){
        ArrayList<String> moves = new ArrayList<>();
        pawnHome = isWhite ? 6 : 1;
        move = isWhite ? -1 : 1;
        eatY = y + move;
        eatX1 = x + 1;
        eatX2 = x - 1;
        if(checkIfContains(eatX1,eatY,!isWhite, whitePieces,blackPieces)){
            // pawn can capture to the right
            moves.add(eatX1 + "," + eatY);
        }
        if(checkIfContains(eatX2,eatY,!isWhite, whitePieces,blackPieces)){
            // pawn can capture to the left
            moves.add(eatX2 + "," + eatY);
        }
        int depth = 1;
        if(y == pawnHome){
            depth = 2;
        }
        for(int i = 1; i< depth+1;i++){
            int newY = y + i*move;
            // pawns cannot eat forwards
            if(!checkIfContains(x,newY,isWhite, whitePieces,blackPieces) && !checkIfContains(x,newY,!isWhite, whitePieces,blackPieces)){
                // pawn can capture to the right
                moves.add(x + "," + newY);
            }
            else{
                break;
            }
        }
        return moves;

    }

    private List<String> calculateKnightMoves(int x, int y, boolean isWhite, boolean edgesOnly,long[] whitePieces, long[] blackPieces) {
        ArrayList<String> moves = new ArrayList<>();

        int[] dx = {1, 2, 2, 1, -1, -2, -2, -1};
        int[] dy = {-2, -1, 1, 2, 2, 1, -1, -2};

        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (isValidMove(newX, newY) && !checkIfContains(newX, newY, isWhite, whitePieces,blackPieces)) {
                if(edgesOnly){
                    if(checkIfContains(newX,newY,!isWhite, whitePieces,blackPieces)){
                        moves.add(newX + "," + newY);
                    }
                }
                else{
                    moves.add(newX + "," + newY);

                }
            }
        }

        return moves;
    }

    private List<String> calculateBishopMoves(int x, int y, boolean isWhite, boolean edgesOnly, boolean directionCheck, long[] whitePieces, long[] blackPieces) {
        ArrayList<String> moves = new ArrayList<>();

        int[] dx = {1, 1, -1, -1};
        int[] dy = {1, -1, 1, -1};

        for (int i = 0; i < 4; i++) {
            int newX = x;
            int newY = y;
            while (true) {
                newX += dx[i];
                newY += dy[i];
                if (isValidMove(newX, newY)) {
                    boolean result = checkIfContains(newX, newY, isWhite,whitePieces,blackPieces);
                    boolean result2 = checkIfContains(newX, newY, !isWhite,whitePieces,blackPieces);
                    String response = directionCheck ? newX + "," + newY + "," + i : newX + "," + newY;
                    if(result2){
                        moves.add(response);
                        break;
                    }
                    if (!result) {
                        if(!edgesOnly){
                            moves.add(response);
                        }
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

    private List<String> calculateRookMoves(int x, int y, boolean isWhite, boolean edgesOnly, boolean directionCheck, long[] whitePieces, long[] blackPieces) {
        ArrayList<String> moves = new ArrayList<>();

        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        for (int i = 0; i < 4; i++) {
            int newX = x;
            int newY = y;
            while (true) {
                newX += dx[i];
                newY += dy[i];
                if (isValidMove(newX, newY)) {
                    boolean result = checkIfContains(newX, newY, isWhite,whitePieces,blackPieces);
                    boolean result2 = checkIfContains(newX, newY, !isWhite,whitePieces,blackPieces);
                    String response = directionCheck ? newX + "," + newY + "," + i : newX + "," + newY;
                    if(result2){
                        moves.add(response);
                        break;
                    }
                    if (!result) {
                        if(!edgesOnly){
                            moves.add(response);
                        }
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

    private List<String> calculateQueenMoves(int x, int y, boolean isWhite, boolean edgesOnly, long[] whitePieces, long[] blackPieces) {
        ArrayList<String> moves = new ArrayList<>();

        List<String> rookMoves = calculateRookMoves(x, y, isWhite,edgesOnly,false,whitePieces,blackPieces);
        List<String> bishopMoves = calculateBishopMoves(x, y, isWhite,edgesOnly,false,whitePieces,blackPieces);

        moves.addAll(rookMoves);
        moves.addAll(bishopMoves);

        return moves;
    }

    private List<String> basicKingMoveCalc(int x, int y, boolean isWhite, long[] whitePieces, long[] blackPieces){
        ArrayList<String> moves = new ArrayList<>();
        int[] dx = {1, -1, 0, 0, 1, -1, 1, -1};
        int[] dy = {0, 0, 1, -1, 1, -1, -1, 1};

        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (isValidMove(newX, newY) && !checkIfContains(newX, newY, isWhite,whitePieces,blackPieces)) {
                moves.add(newX + "," + newY);
            }
        }
        return moves;
    }

    private List<String> calculateKingMoves(int x, int y, boolean isWhite,long[] whitePieces,long[] blackPieces) {

        ArrayList<String> moves = new ArrayList<>();
        boolean canCastle = isWhite ? whiteCastleRight : blackCastleRight;
        boolean shortRook = isWhite ? whiteShortRookMove : blackShortRookMove;
        boolean longRook = isWhite ? whiteLongRookMove : blackLongRookMove;

        if(canCastle){
            // short castle
            if(!checkIfContains(x+1,y,isWhite,whitePieces,blackPieces) && !checkIfContains(x+2,y,isWhite,whitePieces,blackPieces) && shortRook && !isChecked(x+1,y,isWhite,whitePieces,blackPieces) && !isChecked(x+2,y,isWhite,whitePieces,blackPieces)){
                moves.add((x+2) + "," + y + ",9");
            }
            if(!checkIfContains(x-1,y,isWhite,whitePieces,blackPieces) && !checkIfContains(x-2,y,isWhite,whitePieces,blackPieces) && !checkIfContains(x-3,y,isWhite,whitePieces,blackPieces) && !isChecked(x-1,y,isWhite,whitePieces,blackPieces) && !isChecked(x-2,y,isWhite,whitePieces,blackPieces) && !isChecked(x-3,y,isWhite,whitePieces,blackPieces) && longRook){
                moves.add((x-3) + "," + y + ",9");

            }
        }

        int[] dx = {1, -1, 0, 0, 1, -1, 1, -1};
        int[] dy = {0, 0, 1, -1, 1, -1, -1, 1};

        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (isValidMove(newX, newY) && !checkIfContains(newX, newY, isWhite,whitePieces,blackPieces) && !isChecked(newX,newY,isWhite,whitePieces,blackPieces)) {
                moves.add(newX + "," + newY);
            }
        }
        return moves;
    }

    public boolean isKingMovePossible(boolean isWhite,long[] whitePieces,long[] blackPieces){
        List<String> kingLocation = getPieceCoords(isWhite ? whitePieces[5] : blackPieces[5]);
        int[] coord = parseStrCoord(kingLocation.get(0));
        if(!calculateKingMoves(coord[0], coord[1], isWhite,whitePieces,blackPieces).isEmpty()){
            return true;
        }
        List<String> moves = getCheckedFile(isWhite,whitePieces,blackPieces);
        if(moves == null){
            System.out.println("Weird case..");
            return false;
        }
        for(String s : moves){
            int[] coords = parseStrCoord(s);
            // using ischecked with inverse peice color to see if one of your own peices can block
            if(isChecked(coords[0],coords[1],!isWhite,whitePieces,blackPieces)){
                return true;
            }
        }
        return false;
    }





    private final String[] rookLocations = {"7,7,s,w","0,7,l,w","0,0,l,b","7,0,s,b"};
    public void removeRookMoveRight(int x, int y){
        System.out.println("Removing rook right at x: " + x + ", y: " + y);
        for(String s : rookLocations){
            String[] rInfo = s.split(",");
            int rookX = Integer.parseInt(rInfo[0]);
            int rookY = Integer.parseInt(rInfo[1]);
            if(rookX == x && rookY == y){
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

    public boolean isChecked(boolean isWhite, long[] whitePieces, long[] blackPieces){
        List<String> kingLocation = getPieceCoords(isWhite ? whitePieces[5] : blackPieces[5]);
        int[] coord = parseStrCoord(kingLocation.get(0));

        // king coordinates

        return isChecked(coord[0],coord[1],isWhite,whitePieces,blackPieces);
    }

    private boolean isChecked(int x, int y, boolean isWhite, long[] whitePieces, long[] blackPieces){
        // general checking if a square is checked
        List<String> possibleRookFiles = calculateRookMoves(x,y,isWhite,true,false,whitePieces,blackPieces);
        List<String> possibleBishopFiles = calculateBishopMoves(x,y,isWhite,true,false,whitePieces,blackPieces);
        List<String> possibleHorseJumps = calculateKnightMoves(x,y,isWhite,true,whitePieces,blackPieces);
        List<String> possibleKingMoves = basicKingMoveCalc(x,y,isWhite,whitePieces,blackPieces);

        // check pawns
        int jump = isWhite ? 1 : -1;
        if(getPieceType(x-jump,y-jump,!isWhite,whitePieces,blackPieces).equals("Pawn") || getPieceType(x+jump,y-jump,!isWhite,whitePieces,blackPieces).equals("Pawn")){
            return true;
        }
        for(String s : possibleKingMoves){
            int[] coords = parseStrCoord(s);
            String peiceType = getPieceType(coords[0],coords[1],!isWhite,whitePieces,blackPieces);
            if(peiceType.equals("King")){
                return true;
            }
        }
        for(String s : possibleRookFiles){
            int[] coords = parseStrCoord(s);
            String peiceType = getPieceType(coords[0],coords[1],!isWhite,whitePieces,blackPieces);
            if(peiceType.equals("Rook") || peiceType.equals("Queen")){
                return true;
            }
        }
        for(String s : possibleHorseJumps){
            int[] coords = parseStrCoord(s);
            String peiceType = getPieceType(coords[0],coords[1],!isWhite,whitePieces,blackPieces);
            if(peiceType.equals("Knight")){
                return true;
            }
        }
        for(String s : possibleBishopFiles){
            int[] coords = parseStrCoord(s);
            String peiceType = getPieceType(coords[0],coords[1],!isWhite,whitePieces,blackPieces);
            if(peiceType.equals("Bishop") || peiceType.equals("Queen")){
                return true;
            }
        }
        return false;
    }
    List<String> specialMoves = new ArrayList<>();



    private List<String> getCheckedFile(boolean isWhite,long[] whitePieces,long[] blackPieces){
        // general checking if a square is checked
        List<String> kingLocation = getPieceCoords(isWhite ? whitePieces[5] : blackPieces[5]);
        String[] coord = kingLocation.get(0).split(",");
        int x = Integer.parseInt(coord[0]);
        int y = Integer.parseInt(coord[1]);
        specialMoves.clear();
        List<String> possibleRookFiles = calculateRookMoves(x,y,isWhite,true,true,whitePieces,blackPieces);
        List<String> possibleBishopFiles = calculateBishopMoves(x,y,isWhite,true,true,whitePieces,blackPieces);
        List<String> possibleHorseJumps = calculateKnightMoves(x,y,isWhite,true,whitePieces,blackPieces);
        // check pawns
        int jump = isWhite ? 1 : -1;

        if(getPieceType(x-jump,y-jump,!isWhite,whitePieces,blackPieces).equals("Pawn")){
            specialMoves.add((x-jump) + "," + (y-jump));
            return specialMoves;
        }

        if(getPieceType(x+jump,y-jump,!isWhite,whitePieces,blackPieces).equals("Pawn")){
            specialMoves.add((x+jump) + "," + (y-jump));
            return specialMoves;

        }

        for(String s : possibleRookFiles){
            int[] coords = parseStrCoord(s);
            String peiceType = getPieceType(coords[0],coords[1],!isWhite,whitePieces,blackPieces);
            if(peiceType.equals("Rook") || peiceType.equals("Queen")){
                return calculateRookMoves(x,y,isWhite,false,true,whitePieces,blackPieces).stream().filter(g -> g.split(",")[2].equals(Integer.toString(coords[2]))).map(t -> t.substring(0,3)).toList();

            }
        }
        for(String s : possibleHorseJumps){
            int[] coords = parseStrCoord(s);
            String peiceType = getPieceType(coords[0],coords[1],!isWhite,whitePieces,blackPieces);
            if(peiceType.equals("Knight")){
                specialMoves.add(coords[0] + "," + coords[1]);
                return specialMoves;



            }
        }
        for(String s : possibleBishopFiles){
            int[] coords = parseStrCoord(s);
            String peiceType = getPieceType(coords[0],coords[1],!isWhite,whitePieces,blackPieces);

            if(peiceType.equals("Bishop") || peiceType.equals("Queen")){
                return calculateBishopMoves(x,y,isWhite,false,true,whitePieces,blackPieces).stream().filter(g -> g.split(",")[2].equals(Integer.toString(coords[2]))).map(t -> t.substring(0,3)).toList();



            }
        }
        return null;
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
    private final double[][] pawnMap = {
            {0.1, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.1},
            {0.0, 0.0, 0.0, 0.2, 0.2, 0.0, 0.0, 0.0},
            {0.1, 0.1, 0.1, 0.3, 0.3, 0.1, 0.1, 0.1},
            {0.2, 0.2, 0.2, 0.4, 0.4, 0.2, 0.2, 0.2},
            {0.3, 0.3, 0.3, 0.5, 0.5, 0.3, 0.3, 0.3},
            {0.4, 0.4, 0.4, 0.6, 0.6, 0.4, 0.4, 0.4},
            {0.5, 0.5, 0.5, 0.7, 0.7, 0.5, 0.5, 0.5},
            {0.5, 0.5, 0.5, 0.8, 0.8, 0.5, 0.5, 0.5},
            {0.5, 0.5, 0.5, 0.9, 0.9, 0.5, 0.5, 0.5}
    };


    private final double[][] knightMap = {
            {-0.1, 0.0, 0.1, 0.1, 0.1, 0.1, 0.0, -0.1},
            {0.0, 0.1, 0.2, 0.2, 0.2, 0.2, 0.1, 0.0},
            {0.1, 0.2, 0.3, 0.3, 0.3, 0.3, 0.2, 0.1},
            {0.1, 0.2, 0.3, 0.4, 0.4, 0.3, 0.2, 0.1},
            {0.1, 0.2, 0.3, 0.4, 0.4, 0.3, 0.2, 0.1},
            {0.1, 0.2, 0.3, 0.3, 0.3, 0.3, 0.2, 0.1},
            {0.0, 0.1, 0.2, 0.2, 0.2, 0.2, 0.1, 0.0},
            {-0.1, 0.0, 0.1, 0.1, 0.1, 0.1, 0.0, -0.1}
    };


    private final double[][] bishopMap = {
            {-0.3, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.3},
            {-0.2, 0.2, 0.3, 0.3, 0.3, 0.3, 0.2, -0.2},
            {-0.2, 0.3, 0.5, 0.6, 0.6, 0.5, 0.3, -0.2},
            {-0.2, 0.6, 0.6, 0.7, 0.7, 0.6, 0.6, -0.2},
            {-0.2, 0.3, 0.6, 0.7, 0.7, 0.7, 0.3, -0.2},
            {-0.2, 0.6, 0.6, 0.7, 0.7, 0.6, 0.6, -0.2},
            {-0.2, 0.3, 0.2, 0.2, 0.2, 0.2, 0.3, -0.2},
            {-0.3, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.3}
    };
    private final double[][] rookMap = {
            {-0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1, -0.1},
            {0.2, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.2},
            {-0.6, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.6},
            {-0.6, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.6},
            {-0.6, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.6},
            {-0.6, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.6},
            {-0.6, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.6},
            {-0.1, -0.1, -0.1, 0.2, 0.2, -0.1, -0.1, -0.1}
    };

    private final double[][] kingMap = {
            {0.3, 0.0, 0.0, -0.3, -0.3, 0.0, 0.0, 0.3},
            {0.0, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, 0.0},
            {0.0, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, 0.0},
            {-0.3, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, -0.3},
            {-0.5, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, -0.5},
            {0.0, -0.3, -0.3, -0.3, -0.3, -0.3, -0.5, 0.0},
            {0.0, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, 0.0},
            {0.3, 0.0, 0.0, -0.3, -0.3, 0.0, 0.0, 0.3}
    };


    private final double[][] queenMap = {
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.5, 0.7, 0.7, 0.9, 0.9, 0.7, 0.7, 0.5},
            {0.3, 0.5, 0.5, 0.7, 0.7, 0.5, 0.5, 0.3},
            {0.0, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.0},
            {-0.3, -0.3, 0.0, 0.0, 0.0, 0.0, -0.3, -0.3},
            {-0.3, -0.5, -0.3, -0.3, -0.3, -0.3, -0.5, -0.3}
    };


    double[][][] maps = {pawnMap, knightMap, bishopMap, rookMap, queenMap, kingMap};

    public boolean isCheckmated(boolean isWhite,long[] whitePieces,long[] blackPieces){
        return isChecked(isWhite,whitePieces,blackPieces) && !isKingMovePossible(isWhite,whitePieces,blackPieces);
    }

    public boolean isCheckmated(long[] whitePieces,long[] blackPieces){
        return (isChecked(false,whitePieces,blackPieces) && !isKingMovePossible(false,whitePieces,blackPieces) )||( isChecked(true,whitePieces,blackPieces) && !isKingMovePossible(true,whitePieces,blackPieces));
    }


    int[] countW = new int[6];
    int[] countB = new int[6];

    public double getFullEval(long[] whitep, long[] blackP){
        // todo: fix board reversing so these are balanced
        if(isCheckmated(true,blackP,whitep)){
            return 1000000;
        }
        else if(isCheckmated(false,blackP,whitep)){
            return -1000000;
        }
        Arrays.fill(countW,0);
        Arrays.fill(countB,0);
        double sum1 = 0;
        for(int i = 0; i< whitep.length; i++){
            List<String> coords = getPieceCoords(whitep[i]);
            for(String s : coords){
                String[] coord = s.split(",");
                //System.out.println("W: " + s);

                // reverse coordinates to match white peices
                int x = Integer.parseInt(coord[0]);

                int y = Integer.parseInt(coord[1]);
                sum1 += valueMap[i] + maps[i][x][y];

                countW[i]++;


            }
        }
        double sum2 = 0;
        for(int i = 0; i< blackP.length; i++){
            List<String> coords = getPieceCoords(blackP[i]);
            for(String s : coords){
                String[] coord = s.split(",");
                //System.out.println("b: " +s);

                // reverse coordinates to match white peices

                int Normx = Integer.parseInt(coord[0]);
                int Normy = 7-Integer.parseInt(coord[1]);
                sum2 += valueMap[i] + maps[i][Normx][Normy];

                countB[i]++;


            }

        }

//
//        for(int c : countW){
//            System.out.print(c+ " ");
//        }
//        System.out.println("\n");
//        for(int c : countB){
//            System.out.print(c + " ");
//        }

        return sum1 - sum2;

    }


    private void createBoardEntry(long[] whitePieces, long[] blackPieces){
        if (moveIndx != boardSave.size() - 1) {
            clearIndx();
        }
        System.out.println("Saving..");

        boardSave.add(new long[][]{Arrays.copyOf(whitePieces,whitePieces.length),Arrays.copyOf(blackPieces,blackPieces.length)});
        //System.out.println("Save size: " + boardSave.size());

        moveIndx ++;
        maxIndex = moveIndx;

    }

    public void clearIndx(){
        int to = boardSave.size();
        if (to > moveIndx + 1) {
            System.out.println("Removing old entries");

            boardSave.subList(moveIndx + 1, to).clear();
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



    public void ChangeBoard(ImageView[][] pieceLocations, Boolean isWhiteTurn, long[] whitePieces, long[] blackPieces){
        whiteCastleRight = moveIndx <= whiteCastleIndx;
        blackCastleRight = moveIndx <= blackCastleIndx;
        whiteLongRookMove = moveIndx <= whiteLongRookIndx;
        whiteShortRookMove = moveIndx <= whiteShortRookIndx;
        blackLongRookMove = moveIndx <= blackLongRookIndx;
        blackShortRookMove = moveIndx <= blackShortRookIndx;


        List<String>[] changes = getChangesNeeded(whitePieces,blackPieces,false);
        List<String> thingsToAdd = changes[0];
        List<String> thingsToRemove = changes[1];
        MatrixToString(pieceLocations);

//        System.out.println("Things to add size : " + thingsToAdd.size());
//        System.out.println("Things to remove size : " + thingsToRemove.size());
//        for(int i = 0; i<thingsToAdd.size();i++){
//            System.out.println(i + " Add: " + thingsToAdd.get(i));
//        }for(int i = 0; i<thingsToRemove.size();i++){
//            System.out.println(i + " Rem: " + thingsToRemove.get(i));
//        }

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
            removePeice(brdRmvIndex,positionToBitIndex(OldX,OldY),isWhite,whitePieces,blackPieces);
            ImageView smallPeice = createNewPeice(brdRmvIndex,isWhite,chessPeiceBoard,true);
            smallPeice.setUserData(Integer.toString(brdRmvIndex));
            if(isWhite){
                eatenWhites.getChildren().add(smallPeice);
            }
            else{
                eatenBlacks.getChildren().add(smallPeice);
            }
            z++;

        }
        while(i < thingsToAdd.size()){
            // edge case where you need to add more to the board
            String[] Moveinfo = thingsToAdd.get(i).split(",");
            int NewX = Integer.parseInt(Moveinfo[0]);
            int NewY = Integer.parseInt(Moveinfo[1]);
            int brdAddIndex = Integer.parseInt(Moveinfo[3]);
            boolean isWhite = Moveinfo[2].equals("w");
            ImageView peice = createNewPeice(brdAddIndex,isWhite,chessPeiceBoard,false);
            chessPeiceBoard.add(peice,NewX,NewY);
            pieceLocations[NewX][NewY] = peice;
            addPiece(brdAddIndex,positionToBitIndex(NewX,NewY),isWhite,whitePieces,blackPieces);
            removeFromEatenPeices(Moveinfo[3],isWhite ? eatenWhites : eatenBlacks);


            i++;


        }
        isWhiteTurn = moveIndx % 2 != 0;
        App.controller.isWhiteTurn = isWhiteTurn;

        MatrixToString(pieceLocations);

    }


    public void ChangeBoard(long[] whitePieces, long[] blackPieces){
        List<String>[] changes = getChangesNeeded(whitePieces,blackPieces,true);
        List<String> thingsToAdd = changes[0];
        List<String> thingsToRemove = changes[1];


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

            z++;

        }
        while(i < thingsToAdd.size()){
            // edge case where you need to add more to the board
            String[] Moveinfo = thingsToAdd.get(i).split(",");
            int NewX = Integer.parseInt(Moveinfo[0]);
            int NewY = Integer.parseInt(Moveinfo[1]);
            int brdAddIndex = Integer.parseInt(Moveinfo[3]);
            boolean isWhite = Moveinfo[2].equals("w");
            ImageView peice = createNewPeice(brdAddIndex,isWhite,chessPeiceBoard,false);
            chessPeiceBoard.add(peice,NewX,NewY);
            i++;


        }

    }


    private List<String>[] getChangesNeeded(long[] whitePieces, long[] blackPieces, boolean isTest){
        long[] whitePeicesOld = whitePieces;
        long[] blackPeicesOld = blackPieces;
        if(!isTest){
            long[][] save = getPeicesFromSave();
            whitePeicesOld = save[0];
            blackPeicesOld = save[1];
        }
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

    private List<String> getPieceCoords(boolean isWhite,long[] whitePieces,long[] blackPieces){
        long[] pieces = isWhite ? whitePieces : blackPieces;
        List<String> summedCoords = new ArrayList<>();
        for(int i = 0; i< pieces.length;i++){
            int j = i;
            summedCoords.addAll(getPieceCoords(pieces[i]).stream().map(s -> s + "," + j).toList());
        }
        return summedCoords;
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

    private void addPiece(int boardIndx, int bitIndex, boolean isWhite, long[] whitePieces, long[] blackPieces) {
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
    private void removePeice(int boardIndx, int bitIndex, boolean isWhite, long[] whitePieces, long[] blackPieces) {
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

    public int[] parseStrCoord(String s){
        return Arrays.stream(s.split(",")).mapToInt(Integer::parseInt).toArray();
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



    private ImageView createNewPeice(int brdIndex, boolean isWhite, GridPane chessPeiceBoard,boolean isEaten){
        String restOfPath ="";
        String pathStart = isWhite ? "w_" : "b_";
        switch (brdIndex) {
            case 0 -> restOfPath = "pawn";
            case 1 -> restOfPath = "knight";
            case 2 -> restOfPath = "bishop";
            case 3 -> restOfPath = "rook";
            case 4 -> restOfPath = "queen";
            case 5 -> restOfPath = "king";
        }
        ImageView piece = new ImageView("/ChessAssets/ChessPieces/" + pathStart + restOfPath + "_1x_ns.png");

        piece.fitHeightProperty().bind(chessPeiceBoard.heightProperty().divide(isEaten ? 25 : 8.5));
        piece.fitWidthProperty().bind(chessPeiceBoard.widthProperty().divide(isEaten ? 25 : 8.5));
        piece.setPreserveRatio(true);


        GridPane.setHalignment(piece, HPos.CENTER);
        GridPane.setValignment(piece, VPos.CENTER);
        return piece;
    }

    private void removeFromGridPane(int x, int y, GridPane pane){
        pane.getChildren().removeIf(n -> GridPane.getColumnIndex(n) == x && GridPane.getRowIndex(n) == y);
    }

    private void removeFromEatenPeices(String BoardId, HBox eatenPeices){
        Iterator<Node> it = eatenPeices.getChildren().iterator();
        while(it.hasNext()){
            ImageView v = (ImageView) it.next();
            if(v.getUserData().equals(BoardId)){
                it.remove();
                break;
            }
        }
    }

    public String getBestComputerMove(boolean isWhite){
        // minimax and everything
        HashMap<long[][],String> allPossibleCurrentMoves = getAllPossibleMovesFromPositonWCoords(whitePiecesC,blackPiecesC,isWhite);
        long[][] bestPosition = null;
        double bestEval = isWhite ? Double.MIN_VALUE : Double.MAX_VALUE;
        double currEval;
        for(long[][] l : allPossibleCurrentMoves.keySet()){
            currEval = miniMax(l[0],l[1],3,Double.MIN_VALUE,Double.MAX_VALUE,isWhite);
            if(isWhite){
                if(currEval > bestEval){
                    bestPosition = l;
                }
            }
            else{
                if(currEval < bestEval){
                    bestPosition = l;
                }
            }
        }
        return allPossibleCurrentMoves.get(bestPosition);


    }

    private HashMap<long[][],String> getAllPossibleMovesFromPositonWCoords(long[] positionWhitePeices, long[] positionBlackPeices, boolean isWhite){
        HashMap<long[][],String> childPositions = new HashMap<>();
        List<String> peiceCoords = getPieceCoords(isWhite,positionWhitePeices,positionBlackPeices);
        //peiceCoords.forEach(c -> System.out.println("PeiceCoord: " + c));
        List<String> possibleMoves = new ArrayList<>();
        for(String s : peiceCoords){
            int[] coords = parseStrCoord(s);
            // first two coordinates _,_ are old coordinates, and last two are new move
            possibleMoves.addAll(getPossibleMoves(coords[0],coords[1],isWhite,positionWhitePeices,positionBlackPeices).stream().map(m -> s + "," + m).toList());
        }
        for(String s : possibleMoves){
            childPositions.put(turnCoordinateModIntoPosition(Arrays.copyOf(positionWhitePeices,positionWhitePeices.length),Arrays.copyOf(positionBlackPeices,positionWhitePeices.length),s,isWhite),s);
        }


        return childPositions;
    }

    private List<long[][]> getAllPossibleMovesFromPositon(long[] positionWhitePeices, long[] positionBlackPeices, boolean isWhite){
        List<long[][]> childPositions = new ArrayList<>();
        List<String> peiceCoords = getPieceCoords(isWhite,positionWhitePeices,positionBlackPeices);
        //peiceCoords.forEach(c -> System.out.println("PeiceCoord: " + c));
        List<String> possibleMoves = new ArrayList<>();
        for(String s : peiceCoords){
            int[] coords = parseStrCoord(s);
            // first two coordinates _,_ are old coordinates, and last two are new move
            possibleMoves.addAll(getPossibleMoves(coords[0],coords[1],isWhite,positionWhitePeices,positionBlackPeices).stream().map(m -> s + "," + m).toList());
        }
        for(String s : possibleMoves){
            childPositions.add(turnCoordinateModIntoPosition(Arrays.copyOf(positionWhitePeices,positionWhitePeices.length),Arrays.copyOf(positionBlackPeices,positionWhitePeices.length),s,isWhite));
        }


        return childPositions;
    }

    public double miniMax(long[] whitePieces, long[] blackPieces, int depth,double alpha, double beta, boolean isMaximizingPlayer){
        int count = 0;
        if(depth == 0 || isCheckmated(whitePieces,blackPieces)){
            return getFullEval(whitePieces,blackPieces);
        }
        if(isMaximizingPlayer){
            double MaxEval = Double.MIN_VALUE;
            for(long[][] peices : getAllPossibleMovesFromPositon(whitePieces,blackPieces,true)){
                //System.out.println(count++);
                //ChangeBoard(peices[0],peices[1]);
                double eval = miniMax(peices[0],peices[1],depth-1,alpha,beta,false);
                MaxEval = Math.max(eval,MaxEval);
                alpha = Math.max(alpha,eval);
                if(beta <= alpha){
                    break;
                }
            }
            return MaxEval;
        }
        else{
            double MinEval = Double.MAX_VALUE;
            for(long[][] peices : getAllPossibleMovesFromPositon(whitePieces,blackPieces,false)){
                //System.out.println(count++);
                //ChangeBoard(peices[0],peices[1]);
                double eval = miniMax(peices[0],peices[1],depth-1,alpha,beta,true);
                MinEval = Math.min(eval,MinEval);
                beta = Math.min(beta,eval);
                if(beta <= alpha){
                    break;
                }
            }
            return MinEval;
        }
    }


    private long[][] turnCoordinateModIntoPosition(long[] whitePieceBase,long[] blackPieceBase, String coordInfo,boolean isWhite){
        long[] currPeices = isWhite ? whitePieceBase : blackPieceBase;
        long[] removingPeices = isWhite ? blackPieceBase : whitePieceBase;
        int[] coords = parseStrCoord(coordInfo);
        int boardModIndex = coords[2];
        int x = coords[3];
        int y = coords[4];
        int oldX = coords[0];
        int oldY = coords[1];
        int oldBit = positionToBitIndex(oldX,oldY);
        int newBit = positionToBitIndex(x,y);
        //System.out.println("OLDX: " + oldX + " OLDY: " +oldY + " NEWX: " + x + " NEWY: " + y + " Peice index: " + boardModIndex);
        boolean isCastleMove = coords.length > 5;
        if(checkIfContains(x,y,!isWhite,whitePieceBase,blackPieceBase)){
            //System.out.println("Eating");
            removingPeices[boardModIndex] = removePieceAtBitIndex(removingPeices[boardModIndex],newBit);
            currPeices[boardModIndex] = addPieceAtBitIndex(currPeices[boardModIndex],newBit);
            currPeices[boardModIndex] = removePieceAtBitIndex(currPeices[boardModIndex],oldBit);


        }
        else{
            currPeices[boardModIndex] = addPieceAtBitIndex(currPeices[boardModIndex],newBit);
            currPeices[boardModIndex] = removePieceAtBitIndex(currPeices[boardModIndex],oldBit);

        }

        if(isCastleMove){
            int jump = x == 6? 1 : -1;
            movePiece(isWhite,x+jump,y,x-jump,y,3,false,true, whitePieceBase, blackPieceBase);
        }



        return new long[][]{whitePieceBase,blackPieceBase};
    }
    private long addPieceAtBitIndex(long bitboard, int bitIndex) {
        return bitboard | (1L << bitIndex);
    }

    private long removePieceAtBitIndex(long bitboard, int bitIndex) {
        return bitboard & ~(1L << bitIndex);
    }









}
