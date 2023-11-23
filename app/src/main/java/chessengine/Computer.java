//package chessengine;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//
//public class Computer {
//    public Computer(){
//
//    }
//
//    public String getBestComputerMove(boolean isWhite){
//        // minimax and everything
//        HashMap<long[][],String> allPossibleCurrentMoves = getAllPossibleMovesFromPositonWCoords(whitePiecesC,blackPiecesC,isWhite);
//        long[][] bestPosition = null;
//        double bestEval = isWhite ? Double.MIN_VALUE : Double.MAX_VALUE;
//        double currEval;
//        for(long[][] l : allPossibleCurrentMoves.keySet()){
//            currEval = miniMax(l[0],l[1],3,Double.MIN_VALUE,Double.MAX_VALUE,isWhite);
//            if(isWhite){
//                if(currEval > bestEval){
//                    bestPosition = l;
//                }
//            }
//            else{
//                if(currEval < bestEval){
//                    bestPosition = l;
//                }
//            }
//        }
//        return allPossibleCurrentMoves.get(bestPosition);
//
//
//    }
//
//    private HashMap<long[][],String> getAllPossibleMovesFromPositonWCoords(long[] positionWhitePeices, long[] positionBlackPeices, boolean isWhite){
//        HashMap<long[][],String> childPositions = new HashMap<>();
//        List<String> peiceCoords = getPieceCoords(isWhite,positionWhitePeices,positionBlackPeices);
//        //peiceCoords.forEach(c -> System.out.println("PeiceCoord: " + c));
//        List<String> possibleMoves = new ArrayList<>();
//        for(String s : peiceCoords){
//            int[] coords = parseStrCoord(s);
//            // first two coordinates _,_ are old coordinates, and last two are new move
//            possibleMoves.addAll(getPossibleMoves(coords[0],coords[1],isWhite,positionWhitePeices,positionBlackPeices).stream().map(m -> s + "," + m).toList());
//        }
//        for(String s : possibleMoves){
//            childPositions.put(turnCoordinateModIntoPosition(Arrays.copyOf(positionWhitePeices,positionWhitePeices.length),Arrays.copyOf(positionBlackPeices,positionWhitePeices.length),s,isWhite),s);
//        }
//
//
//        return childPositions;
//    }
//
//    private List<long[][]> getAllPossibleMovesFromPositon(long[] positionWhitePeices, long[] positionBlackPeices, boolean isWhite){
//        List<long[][]> childPositions = new ArrayList<>();
//        List<String> peiceCoords = getPieceCoords(isWhite,positionWhitePeices,positionBlackPeices);
//        //peiceCoords.forEach(c -> System.out.println("PeiceCoord: " + c));
//        List<String> possibleMoves = new ArrayList<>();
//        for(String s : peiceCoords){
//            int[] coords = parseStrCoord(s);
//            // first two coordinates _,_ are old coordinates, and last two are new move
//            possibleMoves.addAll(getPossibleMoves(coords[0],coords[1],isWhite,positionWhitePeices,positionBlackPeices).stream().map(m -> s + "," + m).toList());
//        }
//        for(String s : possibleMoves){
//            childPositions.add(turnCoordinateModIntoPosition(Arrays.copyOf(positionWhitePeices,positionWhitePeices.length),Arrays.copyOf(positionBlackPeices,positionWhitePeices.length),s,isWhite));
//        }
//
//
//        return childPositions;
//    }
//
//    public double miniMax(long[] whitePieces, long[] blackPieces, int depth,double alpha, double beta, boolean isMaximizingPlayer){
//        int count = 0;
//        if(depth == 0 || isCheckmated(whitePieces,blackPieces)){
//            return getFullEval(whitePieces,blackPieces);
//        }
//        if(isMaximizingPlayer){
//            double MaxEval = Double.MIN_VALUE;
//            for(long[][] peices : getAllPossibleMovesFromPositon(whitePieces,blackPieces,true)){
//                //System.out.println(count++);
//                //ChangeBoard(peices[0],peices[1]);
//                double eval = miniMax(peices[0],peices[1],depth-1,alpha,beta,false);
//                MaxEval = Math.max(eval,MaxEval);
//                alpha = Math.max(alpha,eval);
//                if(beta <= alpha){
//                    break;
//                }
//            }
//            return MaxEval;
//        }
//        else{
//            double MinEval = Double.MAX_VALUE;
//            for(long[][] peices : getAllPossibleMovesFromPositon(whitePieces,blackPieces,false)){
//                //System.out.println(count++);
//                //ChangeBoard(peices[0],peices[1]);
//                double eval = miniMax(peices[0],peices[1],depth-1,alpha,beta,true);
//                MinEval = Math.min(eval,MinEval);
//                beta = Math.min(beta,eval);
//                if(beta <= alpha){
//                    break;
//                }
//            }
//            return MinEval;
//        }
//    }
//
//
//    private long[][] turnCoordinateModIntoPosition(long[] whitePieceBase,long[] blackPieceBase, String coordInfo,boolean isWhite){
//        long[] currPeices = isWhite ? whitePieceBase : blackPieceBase;
//        long[] removingPeices = isWhite ? blackPieceBase : whitePieceBase;
//        int[] coords = parseStrCoord(coordInfo);
//        int boardModIndex = coords[2];
//        int x = coords[3];
//        int y = coords[4];
//        int oldX = coords[0];
//        int oldY = coords[1];
//        int oldBit = positionToBitIndex(oldX,oldY);
//        int newBit = positionToBitIndex(x,y);
//        //System.out.println("OLDX: " + oldX + " OLDY: " +oldY + " NEWX: " + x + " NEWY: " + y + " Peice index: " + boardModIndex);
//        boolean isCastleMove = coords.length > 5;
//        if(checkIfContains(x,y,!isWhite,whitePieceBase,blackPieceBase)){
//            //System.out.println("Eating");
//            removingPeices[boardModIndex] = removePieceAtBitIndex(removingPeices[boardModIndex],newBit);
//            currPeices[boardModIndex] = addPieceAtBitIndex(currPeices[boardModIndex],newBit);
//            currPeices[boardModIndex] = removePieceAtBitIndex(currPeices[boardModIndex],oldBit);
//
//
//        }
//        else{
//            currPeices[boardModIndex] = addPieceAtBitIndex(currPeices[boardModIndex],newBit);
//            currPeices[boardModIndex] = removePieceAtBitIndex(currPeices[boardModIndex],oldBit);
//
//        }
//
//        if(isCastleMove){
//            int jump = x == 6? 1 : -1;
//            movePiece(isWhite,x+jump,y,x-jump,y,3,false,true, whitePieceBase, blackPieceBase);
//        }
//
//
//
//        return new long[][]{whitePieceBase,blackPieceBase};
//    }
//}
