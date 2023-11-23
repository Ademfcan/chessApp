package chessengine;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class chessBoardGUIHandler {
    private GridPane chessBoard;
    private HBox eatenWhites;
    private HBox eatenBlacks;

    private ImageView[][] peicesAtLocations = new ImageView[8][8];

    public chessBoardGUIHandler(GridPane chessBoard, HBox eatenWhites, HBox eatenBlacks){
        this.chessBoard = chessBoard;
        this.eatenWhites = eatenWhites;
        this.eatenBlacks = eatenBlacks;
        setUpChessPieces(chessBoard);
    }
    // set up chess peices in starting position
    private void setUpChessPieces(GridPane board) {
        int pieceX = 0;
        int pieceY = 6;
        String pathStart = "w_";
        String restOfPath = "";
        boolean isWhite = true;
        boolean isPawn = true;
        for (int i = 0; i < 2; i++) {
            // colors
            for (int j = 0; j < 2; j++) {
                // pawns vs normal pieces
                for (int z = 0; z < 8; z++) {
                    if (!isWhite) {
                        pathStart = "b_";
                    }
                    if (isPawn) {
                        restOfPath = "pawn";
                    } else {
                        switch (z) {
                            case 0:
                            case 7:
                                restOfPath = "rook";
                                break;
                            case 1:
                            case 6:
                                restOfPath = "knight";
                                break;

                            case 2:
                            case 5:
                                restOfPath = "bishop";
                                break;

                            case 3:
                                restOfPath = "queen";
                                break;

                            case 4:
                                restOfPath = "king";
                                break;


                        }
                    }
                    ImageView piece = new ImageView("/ChessAssets/ChessPieces/" + pathStart + restOfPath + "_1x_ns.png");
                    piece.fitHeightProperty().bind(board.heightProperty().divide(9));
                    piece.fitWidthProperty().bind(board.widthProperty().divide(8));
                    piece.setPreserveRatio(true);



                    GridPane.setHalignment(piece, HPos.CENTER);
                    GridPane.setValignment(piece, VPos.CENTER);


                    board.add(piece, pieceX, pieceY);
                    peicesAtLocations[pieceX][pieceY] = piece;

                    pieceX++;

                }
                pieceX = 0;
                if (isWhite) {
                    pieceY++;
                } else {
                    pieceY--;
                }
                isPawn = false;
            }
            pieceY = 1;
            isPawn = true;
            isWhite = false;

        }
    }

    public void movePeice(int OldX, int OldY, int NewX, int NewY){
        if(peicesAtLocations[NewX][NewY] != null){
            // means enemy peice is being eaten
            removeFromGridPane(NewX,NewY);
        }
        peicesAtLocations[NewX][NewY] = peicesAtLocations[OldX][OldY];
        peicesAtLocations[OldX][OldY] = null;
        GridPane.setColumnIndex(peicesAtLocations[NewX][NewY],NewX);
        GridPane.setRowIndex(peicesAtLocations[NewX][NewY],NewY);
    }

    private void removeFromGridPane(int x, int y){
        chessBoard.getChildren().removeIf(n -> GridPane.getColumnIndex(n) == x && GridPane.getRowIndex(n) == y);
    }

    private ImageView createNewPeice(int brdIndex, boolean isWhite, GridPane chessPeiceBoard, boolean isEaten){
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
}
