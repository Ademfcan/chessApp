package chessengine;

import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class chessBoard {
    bitBoard[] whitePeices;
    bitBoard[] blackPeices;
    // [0] = white [1] = black
    private boolean[] kingRights = {true, true};
    // stores rooks right to casle at each location
    // [0] = 7,7 [1] = 0,7 [2] = 7,0 [3] = 0,0
    // [0] = SW  [1] = LW  [2] = SB  [3] = LB
    // S = short, L  = long, W = white, B = black
    private boolean[] rookRights = {true, true, true, true};
    // stores index when you lost rook right to castle
    private int[] rookIndexes = {1000, 1000, 1000, 1000};

    private int[] kingIndexes = {1000, 1000};
    private final XYCoordinate[] rookLocations = {new XYCoordinate(7, 7, 3), new XYCoordinate(0, 7, 3),
            new XYCoordinate(7, 0, 3), new XYCoordinate(0, 0, 3)};

    public chessBoard(bitBoard[] whitePeices, bitBoard[] blackPeices) {
        this.whitePeices = whitePeices;
        this.blackPeices = blackPeices;
    }


    public void removeRookMoveRight(int x, int y, int moveIndex) {
        System.out.println("Removing rook right at x: " + x + ", y: " + y);
        for (int i = 0; i < rookLocations.length; i++) {
            if (rookLocations[i].doesItMatch(x, y)) {
                rookRights[i] = false;
                rookIndexes[i] = moveIndex;
            }
        }
    }

    public void removeKingCastleRight(boolean isWhite, int moveIndex) {
        int index = isWhite ? 0 : 1;
        kingRights[index] = false;
        // store what moveindex you lost castle right
        kingIndexes[index] = moveIndex;
    }


    // below methods are for moving a peice from one square to another
    public void movePeice(int OldX, int OldY, int NewX, int NewY, boolean isWhite, int boardIndex) {
        int oldBitIndex = xyToBitIndex(OldX, OldY);
        int newBitIndex = xyToBitIndex(NewX, NewY);
        bitBoard[] yourColor = isWhite ? whitePeices : blackPeices;
        bitBoard[] enemyColor = !isWhite ? whitePeices : blackPeices;
        // remove piece from enemy color (it doesn't matter if there's a piece there already; this way we avoid checking)
        enemyColor[boardIndex].RemovePeice(newBitIndex);
        // remove peice at old place and move to new one
        yourColor[boardIndex].RemovePeice(oldBitIndex);
        yourColor[boardIndex].AddPeice(newBitIndex);
    }

    public void movePeice(int OldX, int OldY, int NewX, int NewY, boolean isWhite) {
        int boardIndex = getPeiceBoardIndex(NewX, NewY, isWhite);
        movePeice(OldX, OldY, NewX, NewY, isWhite, boardIndex);
    }


    public List<String> getPossibleMoves(int x, int y, boolean isWhite) {
        int indx = getPeiceBoardIndex(x, y, isWhite);
        List<String> baseMoves = getMoveOfType(x, y, isWhite, indx);

        if (isChecked(isWhite)) {
            if (indx != 5) {
                // if not the king, (index of 5) make sure you can only play blocking moves
                baseMoves.retainAll(getCheckedFile(isWhite));

            }
            return baseMoves;
        } else {
            return baseMoves;
        }

    }

    private List<String> getMoveOfType(int x, int y, boolean isWhite, int indx) {
        return switch (indx) {
            case 0 -> calculatePawnMoves(x, y, isWhite);
            case 1 -> calculateKnightMoves(x, y, isWhite, false);
            case 2 -> calculateBishopMoves(x, y, isWhite, false, false);
            case 3 -> calculateRookMoves(x, y, isWhite, false, false);
            case 4 -> calculateQueenMoves(x, y, isWhite, false);
            case 5 -> calculateKingMoves(x, y, isWhite);
            default -> null;
        };
    }

    private boolean isChecked(boolean isWhite) {
        // only one king will ever be on the board, so get the 0th index
        XYCoordinate kingLocation = getPieceCoords(isWhite ? whitePeices : blackPeices, 5).get(0);
        return isChecked(kingLocation.getX(), kingLocation.getY(), isWhite);
    }

    private boolean isChecked(int x, int y, boolean isWhite) {
        // general checking if a square is checked
        List<String> possibleRookFiles = calculateRookMoves(x, y, isWhite, true, false);
        List<String> possibleBishopFiles = calculateBishopMoves(x, y, isWhite, true, false);
        List<String> possibleHorseJumps = calculateKnightMoves(x, y, isWhite, true);
        List<String> possibleKingMoves = basicKingMoveCalc(x, y, isWhite);

        // check pawns
        int jump = isWhite ? 1 : -1;
        if (getPieceType(x - jump, y - jump, !isWhite).equals("Pawn") || getPieceType(x + jump, y - jump, !isWhite).equals("Pawn")) {
            return true;
        }
        for (String s : possibleKingMoves) {
            int[] coords = parseStrCoord(s);
            String peiceType = getPieceType(coords[0], coords[1], !isWhite);
            if (peiceType.equals("King")) {
                return true;
            }
        }
        for (String s : possibleRookFiles) {
            int[] coords = parseStrCoord(s);
            String peiceType = getPieceType(coords[0], coords[1], !isWhite);
            if (peiceType.equals("Rook") || peiceType.equals("Queen")) {
                return true;
            }
        }
        for (String s : possibleHorseJumps) {
            int[] coords = parseStrCoord(s);
            String peiceType = getPieceType(coords[0], coords[1], !isWhite);
            if (peiceType.equals("Knight")) {
                return true;
            }
        }
        for (String s : possibleBishopFiles) {
            int[] coords = parseStrCoord(s);
            String peiceType = getPieceType(coords[0], coords[1], !isWhite);
            if (peiceType.equals("Bishop") || peiceType.equals("Queen")) {
                return true;
            }
        }
        return false;
    }

    // store special moves like pawn moves etc, anything that wont return a list on its own
    List<String> specialMoves = new ArrayList<>();

    private List<String> getCheckedFile(boolean isWhite) {
        // general checking if a square is checked
        XYCoordinate kinglocation = getKingLocation(isWhite);
        int x = kinglocation.getX();
        int y = kinglocation.getY();
        specialMoves.clear();
        List<String> possibleRookFiles = calculateRookMoves(x, y, isWhite, true, true);
        List<String> possibleBishopFiles = calculateBishopMoves(x, y, isWhite, true, true);
        List<String> possibleHorseJumps = calculateKnightMoves(x, y, isWhite, true);
        // check pawns
        int jump = isWhite ? 1 : -1;

        if (getPieceType(x - jump, y - jump, !isWhite).equals("Pawn")) {
            specialMoves.add((x - jump) + "," + (y - jump));
            return specialMoves;
        }

        if (getPieceType(x + jump, y - jump, !isWhite).equals("Pawn")) {
            specialMoves.add((x + jump) + "," + (y - jump));
            return specialMoves;

        }

        for (String s : possibleRookFiles) {
            int[] coords = parseStrCoord(s);
            String peiceType = getPieceType(coords[0], coords[1], !isWhite);
            if (peiceType.equals("Rook") || peiceType.equals("Queen")) {
                return calculateRookMoves(x, y, isWhite, false, true).stream().filter(g -> g.split(",")[2].equals(Integer.toString(coords[2]))).map(t -> t.substring(0, 3)).toList();

            }
        }
        for (String s : possibleHorseJumps) {
            int[] coords = parseStrCoord(s);
            String peiceType = getPieceType(coords[0], coords[1], !isWhite);
            if (peiceType.equals("Knight")) {
                specialMoves.add(coords[0] + "," + coords[1]);
                return specialMoves;


            }
        }
        for (String s : possibleBishopFiles) {
            int[] coords = parseStrCoord(s);
            String peiceType = getPieceType(coords[0], coords[1], !isWhite);

            if (peiceType.equals("Bishop") || peiceType.equals("Queen")) {
                return calculateBishopMoves(x, y, isWhite, false, true).stream().filter(g -> g.split(",")[2].equals(Integer.toString(coords[2]))).map(t -> t.substring(0, 3)).toList();


            }
        }
        return null;
    }

    // returns x,y coordinates wherever the peices in a bitboard are
    private List<XYCoordinate> getPieceCoords(bitBoard[] boards, int boardIndex) {
        List<XYCoordinate> coords = new ArrayList<>();

        for (int z = 0; z < 64; z++) {
            long mask = 1L << z;

            if (boards[boardIndex].checkIfContains(z)) {
                int[] coord = bitindexToXY(z);
                coords.add(new XYCoordinate(coord[0], coord[1], boardIndex));
            }
        }

        return coords;
    }

    private XYCoordinate getKingLocation(boolean isWhite) {
        return getPieceCoords(isWhite ? whitePeices : blackPeices, 5).get(0);
    }

    public bitBoard[] getBlackPeices() {
        return blackPeices;
    }

    public bitBoard[] getWhitePeices() {
        return whitePeices;
    }

    // create variables once
    int pawnHome;
    int move;
    int eatY;
    int eatX1;
    int eatX2;

    // calculates pawn moves by checking ahead of the pawn and also the sides to see if it can eat
    private List<String> calculatePawnMoves(int x, int y, boolean isWhite) {
        ArrayList<String> moves = new ArrayList<>();
        pawnHome = isWhite ? 6 : 1;
        // check
        move = isWhite ? -1 : 1;
        eatY = y + move;
        eatX1 = x + 1;
        eatX2 = x - 1;
        if (checkIfContains(eatX1, eatY, !isWhite)) {
            // pawn can capture to the right
            moves.add(eatX1 + "," + eatY);
        }
        if (checkIfContains(eatX2, eatY, !isWhite)) {
            // pawn can capture to the left
            moves.add(eatX2 + "," + eatY);
        }
        int depth = 1;
        if (y == pawnHome) {
            depth = 2;
        }
        for (int i = 1; i < depth + 1; i++) {
            int newY = y + i * move;
            // pawns cannot eat forwards
            if (!checkIfContains(x, newY, isWhite) && !checkIfContains(x, newY, !isWhite)) {
                // pawn can capture to the right
                moves.add(x + "," + newY);
            } else {
                break;
            }
        }
        return moves;

    }

    private List<String> calculateKnightMoves(int x, int y, boolean isWhite, boolean edgesOnly) {
        ArrayList<String> moves = new ArrayList<>();

        int[] dx = {1, 2, 2, 1, -1, -2, -2, -1};
        int[] dy = {-2, -1, 1, 2, 2, 1, -1, -2};

        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (isValidMove(newX, newY) && !checkIfContains(newX, newY, isWhite)) {
                if (edgesOnly) {
                    // only add enemy endpoints
                    if (checkIfContains(newX, newY, !isWhite)) {
                        moves.add(newX + "," + newY);
                    }
                } else {
                    moves.add(newX + "," + newY);

                }
            }
        }

        return moves;
    }

    private List<String> calculateBishopMoves(int x, int y, boolean isWhite, boolean edgesOnly, boolean directionCheck) {
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
                    boolean result = checkIfContains(newX, newY, isWhite);
                    boolean result2 = checkIfContains(newX, newY, !isWhite);
                    String response = directionCheck ? newX + "," + newY + "," + i : newX + "," + newY;
                    if (result2) {
                        moves.add(response);
                        break;
                    }
                    if (!result) {
                        if (!edgesOnly) {
                            moves.add(response);
                        }
                    } else {
                        break;
                    }

                } else {
                    break;
                }
            }
        }

        return moves;
    }

    private List<String> calculateRookMoves(int x, int y, boolean isWhite, boolean edgesOnly, boolean directionCheck) {
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
                    boolean result = checkIfContains(newX, newY, isWhite);
                    boolean result2 = checkIfContains(newX, newY, !isWhite);
                    String response = directionCheck ? newX + "," + newY + "," + i : newX + "," + newY;
                    if (result2) {
                        moves.add(response);
                        break;
                    }
                    if (!result) {
                        if (!edgesOnly) {
                            moves.add(response);
                        }
                    } else {
                        break;
                    }


                } else {
                    break;
                }
            }
        }

        return moves;
    }

    private List<String> calculateQueenMoves(int x, int y, boolean isWhite, boolean edgesOnly) {
        ArrayList<String> moves = new ArrayList<>();

        List<String> rookMoves = calculateRookMoves(x, y, isWhite, edgesOnly, false);
        List<String> bishopMoves = calculateBishopMoves(x, y, isWhite, edgesOnly, false);

        moves.addAll(rookMoves);
        moves.addAll(bishopMoves);

        return moves;
    }

    private List<String> basicKingMoveCalc(int x, int y, boolean isWhite) {
        // helper function  for ischecked (line 47)
        // because the full calculatekingmoves below relies on ischecked, and ischecked needs a basic king move calc, need separate simple function
        ArrayList<String> moves = new ArrayList<>();
        int[] dx = {1, -1, 0, 0, 1, -1, 1, -1};
        int[] dy = {0, 0, 1, -1, 1, -1, -1, 1};

        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (isValidMove(newX, newY) && !checkIfContains(newX, newY, isWhite)) {
                moves.add(newX + "," + newY);
            }
        }
        return moves;
    }

    private List<String> calculateKingMoves(int x, int y, boolean isWhite) {
        // returns a list of coordinates where a king can move from a certain coordinate
        ArrayList<String> moves = new ArrayList<>();
        boolean canCastle = isWhite ? kingRights[0] : kingRights[1];
        boolean shortRook = isWhite ? rookRights[0] : rookRights[2];
        boolean longRook = isWhite ? rookRights[1] : rookRights[3];

        if (canCastle) {
            // short castle
            if (!checkIfContains(x + 1, y, isWhite) && !checkIfContains(x + 2, y, isWhite) && shortRook && !isChecked(x + 1, y, isWhite) && !isChecked(x + 2, y, isWhite)) {
                moves.add((x + 2) + "," + y + ",9");
            }
            if (!checkIfContains(x - 1, y, isWhite) && !checkIfContains(x - 2, y, isWhite) && !checkIfContains(x - 3, y, isWhite) && !isChecked(x - 1, y, isWhite) && !isChecked(x - 2, y, isWhite) && !isChecked(x - 3, y, isWhite) && longRook) {
                moves.add((x - 3) + "," + y + ",9");

            }
        }
        // different directions a king could move
        int[] dx = {1, -1, 0, 0, 1, -1, 1, -1};
        int[] dy = {0, 0, 1, -1, 1, -1, -1, 1};

        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (isValidMove(newX, newY) && !checkIfContains(newX, newY, isWhite) && !isChecked(newX, newY, isWhite)) {
                moves.add(newX + "," + newY);
            }
        }
        return moves;
    }

    private boolean checkIfContains(int x, int y, boolean isWhite) {
        bitBoard[] peices = isWhite ? whitePeices : blackPeices;
        for (bitBoard b : peices) {
            if (b.checkIfContains(x, y)) {
                return true;
            }
        }
        return false;
    }

    public String getPieceType(int x, int y, Boolean isWhite) {
        int indx = getPeiceBoardIndex(x, y, isWhite);
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

    private int getPeiceBoardIndex(int x, int y, boolean isWhite) {
        if (isWhite) {
            for (int i = 0; i < whitePeices.length; i++) {
                if (whitePeices[i].checkIfContains(x, y)) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < blackPeices.length; i++) {
                if (blackPeices[i].checkIfContains(x, y)) {
                    return i;
                }
            }
        }
        return -100000;
    }

    // next few functions are for loading previous saved positions
    public int moveIndx = -1;
    public int maxIndex = -1;

    private ArrayList<bitBoard[][]> boardSave = new ArrayList<>();


    public void ChangeBoard(){
        // see above definiton of rook/kingrights to understand better
        kingRights[0] = moveIndx <= kingIndexes[0];
        kingRights[1] = moveIndx <= kingIndexes[1];
        rookRights[0] = moveIndx <= rookIndexes[0];
        rookRights[1] = moveIndx <= rookIndexes[1];
        rookRights[2] = moveIndx <= rookIndexes[2];
        rookRights[3] = moveIndx <= rookIndexes[3];



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
    private List<String>[] getChangesNeeded(){
       bitBoard whitePeicesOld;
       bitBoard blackPeicesOld;

        long[][] save = getPeicesFromSave();
        whitePeicesOld = save[0];
        blackPeicesOld = save[1];

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



    private long[][] getPeicesFromSave(){
        bitBoard[] whitePeicesOld;
        bitBoard[] blackPeicesOld;
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





    // make sure peices are within board bounds
    private boolean isValidMove(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    // turns a bitboard index into its correct x,y representation
    private int[] bitindexToXY(int bitIndex) {
        return new int[]{bitIndex % 8, bitIndex / 8};
    }

    // opposite of above method, turn x,y coords into bitindex representation
    private int xyToBitIndex(int x, int y) {
        return x + y * 8;
    }

    public int[] parseStrCoord(String s) {
        return Arrays.stream(s.split(",")).mapToInt(Integer::parseInt).toArray();
    }


}
