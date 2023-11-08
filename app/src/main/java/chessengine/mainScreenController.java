package chessengine;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class mainScreenController implements Initializable {

    Boolean peiceSelected = false;
    // 1 = white, -1 = black x , y coords
    int[] selectedPeiceInfo = {0, 0, 0};

    List<String> oldHighights = null;
    ImageView selectedPeice;

    String highlightColor = "";
    StackPane[][] Bgpanes = new StackPane[8][8];
    ImageView[][] peicesAtLocations = new ImageView[8][8];

    @FXML
    ComboBox bgColorSelector;

    @FXML
    GridPane chessBoard;

    @FXML
    GridPane chessBgBoard;

    @FXML
    GridPane chessPieceBoard;
    coordinateHandler handler;

    pieceLocationHandler peiceHandler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chessPieceBoard.setMouseTransparent(true);
        peiceHandler = new pieceLocationHandler();

        bgColorSelector.getItems().addAll(
                "Traditional",
                "Ice", "Halloween", "Summer"
        );
        bgColorSelector.setOnAction(e -> {
            changeChessBg(bgColorSelector.getValue().toString());
        });
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                StackPane stackpane = new StackPane();
                StackPane Bgstackpane = new StackPane();

                Bgstackpane.setUserData(i + "," + j);
                chessBoard.add(stackpane, i, j);
                chessBgBoard.add(Bgstackpane, i, j);
                setUpSquareClickEvent(Bgstackpane);
                Bgpanes[i][j] = Bgstackpane;

            }
        }
        changeChessBg("Traditional");
        setUpChessPieces(chessPieceBoard);

    }


    private void changeChessBg(String colorType) {
        boolean isLight = true;
        highlightColor = colorType;
        String[] clrStr = getColorStr(colorType);
        String curr = "";
        int count = 0;

        for (Node n : chessBoard.getChildren()) {
            if (isLight) {
                curr = clrStr[0];
                isLight = false;
            } else {
                curr = clrStr[1];
                isLight = true;
            }
            // System.out.println(count);
            if (count < 63) {
                count++;
            }
            int x = count / 8;
            int y = count % 8;
            //System.out.println("x: " + y);
            //System.out.println("y: " + x);


            // currBgColors[count] = curr;
            n.setStyle("-fx-background-color: " + curr);
            if (count % 8 == 0) {
                // offset every row for checkerboard
                isLight = !isLight;
            }


        }
    }


    private String[] getColorStr(String colortype) {
        return switch (colortype) {
            case "Ice" -> new String[]{"#7FDEFF", "#4F518C"};
            case "Traditional" -> new String[]{"#9e7a3a", "#2e120b"};
            case "Halloween" -> new String[]{"#ff6619", "#241711"};
            case "Summer" -> new String[]{"#f7cc0a", "#22668D"};
            default -> null;
        };
    }

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

                    piece.setFitHeight(32);
                    piece.setFitWidth(32);


                    board.setHalignment(piece, HPos.CENTER);
                    board.setValignment(piece, VPos.CENTER);


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


    private void setUpSquareClickEvent(StackPane square) {
        square.setOnMouseClicked(event -> {
            StackPane pane = (StackPane) event.getSource();
            String[] xy = pane.getUserData().toString().split(",");
            int x = Integer.parseInt(xy[0]);
            int y = Integer.parseInt(xy[1]);
            System.out.println("X: " + x);
            System.out.println("Y: " + y);
            if (event.getButton() == MouseButton.PRIMARY) {

                boolean[] boardInfo = peiceHandler.checkIfContains(x, y);
                //System.out.println("IsHit:" + boardInfo[0] + " isWhite: " + boardInfo[1]);
                // possible move
                //System.out.println("PrevPeice Selected: " + peiceSelected);

                if (peiceSelected) {
                    boolean prevPeiceClr = (selectedPeiceInfo[2] > 0);
                    int oldX = selectedPeiceInfo[0];
                    int oldY = selectedPeiceInfo[1];
                    System.out.println(prevPeiceClr);
                    System.out.println(boardInfo[1]);
                    if (!boardInfo[0] || prevPeiceClr != boardInfo[1]) {
                        // enemy colors or empty square

                        //System.out.println("Moving " + prevPeiceClr + " peice from " + oldX + "," + oldY + " to " + x + "," + y);
                        if(checkIfMovePossible(oldHighights,x,y)){
                            for(String s : oldHighights){
                                String[] coords = s.split(",");
                                int a = Integer.parseInt(coords[0]);
                                int b = Integer.parseInt(coords[1]);
                                removeHiglight(a,b);
                            }


                            if(boardInfo[0]){
                                // remove enemy peice
                                peiceHandler.removePeice(boardInfo[1],x,y);
                                chessPieceBoard.getChildren().remove(peicesAtLocations[x][y]);

                            }
                            peiceHandler.movePiece(prevPeiceClr,oldX,oldY,x,y);
                            removeHiglight(oldX, oldY);

                            GridPane.setRowIndex(selectedPeice,y);
                            GridPane.setColumnIndex(selectedPeice,x);
                            peicesAtLocations[x][y] = selectedPeice;
                            peiceSelected = false;


                            oldHighights = null;
                        }


                    }
                    else {
                        // your own peice color
                        //System.out.println("Own color");
                        System.out.println(peiceHandler.getPieceType(x,y,boardInfo[1]));

                        int clr = (boardInfo[1]) ? 1 : -1;
                        selectedPeiceInfo[0] = x;
                        selectedPeiceInfo[1] = y;
                        selectedPeiceInfo[2] = clr;
                        selectedPeice = peicesAtLocations[x][y];

                        removeHiglight(oldX, oldY);
                        if(oldX != x || oldY != y){
                            highlightSquare(x, y, true);
                            if(oldHighights != null){
                                for(String s : oldHighights){
                                    String[] coords = s.split(",");
                                    int a = Integer.parseInt(coords[0]);
                                    int b = Integer.parseInt(coords[1]);
                                    removeHiglight(a,b);
                                }
                            }

                            List<String> highlightLocations = peiceHandler.getPossibleMoves(x,y,boardInfo[1]);
                            oldHighights = highlightLocations;
                            for(String s : highlightLocations){
                                String[] coords = s.split(",");
                                int a = Integer.parseInt(coords[0]);
                                int b = Integer.parseInt(coords[1]);
                                highlightSquare(a,b,false);
                            }


                        }
                        else {
                            peiceSelected = false;

                            for(String s : oldHighights){
                                String[] coords = s.split(",");
                                int a = Integer.parseInt(coords[0]);
                                int b = Integer.parseInt(coords[1]);
                                removeHiglight(a,b);
                            }

                            oldHighights = null;
                        }





                        }
                    }
                else if(boardInfo[0]){
                    // no prev selection
                    System.out.println(peiceHandler.getPieceType(x,y,boardInfo[1]));

                    List<String> moves = peiceHandler.getPossibleMoves(x,y,boardInfo[1]);
                    oldHighights = moves;
                    for(String s : moves){
                        String[] coords = s.split(",");
                        int a = Integer.parseInt(coords[0]);
                        int b = Integer.parseInt(coords[1]);
                        highlightSquare(a,b,false);
                    }



                    peiceSelected = true;

                    int clr = (boardInfo[1]) ? 1 : -1;
                    selectedPeiceInfo[0] = x;
                    selectedPeiceInfo[1] = y;
                    selectedPeiceInfo[2] = clr;
                    selectedPeice = peicesAtLocations[x][y];




                    highlightSquare(x, y, true);

                }
                if(peiceSelected){
                    //System.out.println("PrevPeice Selected x: " + selectedPeiceInfo[0]);
                    //System.out.println("PrevPeice Selected y: " + selectedPeiceInfo[1]);
                    //System.out.println("PrevPeice Selected isWhite: " + selectedPeiceInfo[2]);
                }





            } else if (event.getButton() == MouseButton.SECONDARY) {
                highlightSquare(x, y, false);
            }

        });
    }

    private void highlightSquare(int x, int y, boolean isPieceSelection) {
        //System.out.println(Bgpanes[x][y].getStyle());;
        if (isPieceSelection) {
            highlightColor = "rgba(44, 212, 255, 0.25)";
        } else {
            highlightColor = "rgba(223, 90, 37, 0.4)";
        }
        if (!Bgpanes[x][y].getStyle().contains("rgba")) {
            Bgpanes[x][y].setStyle("-fx-background-color: " + highlightColor);
        } else {
            Bgpanes[x][y].setStyle("-fx-background-color: transparent");

        }
    }

    private void removeHiglight(int x, int y){
        Bgpanes[x][y].setStyle("-fx-background-color: transparent");

    }

    private boolean checkIfMovePossible(List<String> moves, int x, int y){
        // todo: change this to bitboard logic
        for(String s : moves){
            String[] coords = s.split(",");
            int a = Integer.parseInt(coords[0]);
            int b = Integer.parseInt(coords[1]);
            if(a == x && b == y){
                return true;
            }
        }
        return false;
    }
}
