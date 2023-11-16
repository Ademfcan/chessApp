package chessengine;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;

public class mainScreenController implements Initializable {

    public boolean isWhiteTurn = true;
    Boolean peiceSelected = false;
    // [0] = x ,[1] =  y coords [2] =( 1 = white, -1 = black)
    int[] selectedPeiceInfo = {0, 0, 0};

    List<String> oldHighights = null;
    ImageView selectedPeice;

    String highlightColor = "";
    StackPane[][] Bgpanes = new StackPane[8][8];
    public ImageView[][] peicesAtLocations = new ImageView[8][8];

    @FXML
    Button LeftButton;

    @FXML
    Button RightButton;



    @FXML
    ComboBox bgColorSelector;

    @FXML
    GridPane chessBoard;

    @FXML
    GridPane chessBgBoard;

    @FXML
    public GridPane chessPieceBoard;





    @FXML
    public Label saveIndicator;

    @FXML
    public Button reset;

    @FXML
    Rectangle blackadvantage;

    @FXML
    Label blackEval;

    @FXML
    Rectangle whiteadvantage;

    @FXML
    Label whiteEval;

    @FXML
    StackPane barContainer;

    @FXML
    Label victoryLabel;

    pieceLocationHandler peiceHandler;

    private boolean GameOver = false;

    private int gameEndIndx = 1000000;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chessPieceBoard.setMouseTransparent(true);
        peiceHandler = new pieceLocationHandler(GameOver);
        barContainer.prefHeightProperty().bind(chessPieceBoard.heightProperty());
        whiteadvantage.heightProperty().bind(chessPieceBoard.heightProperty().divide(2));
        blackadvantage.heightProperty().bind(chessPieceBoard.heightProperty().divide(2));
        bgColorSelector.getItems().addAll(
                "Traditional",
                "Ice", "Halloween", "Summer","Cherry"
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

        LeftButton.setOnMouseClicked(e -> {
            if(peiceHandler.moveIndx >= 0){
                peiceHandler.updateMoveIndex(-1);
                GameOver = peiceHandler.moveIndx >= gameEndIndx;
                peiceHandler.ChangeBoard(chessPieceBoard,peicesAtLocations,isWhiteTurn);
                saveIndicator.setText((peiceHandler.moveIndx + 1) + "/" + (peiceHandler.maxIndex+1));
                setEvalBar(whiteEval,blackEval,whiteadvantage,blackadvantage,peiceHandler.getSimpleEval());
                unselectEveryThing();

            }


        });

        RightButton.setOnMouseClicked(e -> {
            if(peiceHandler.moveIndx < peiceHandler.maxIndex){
                peiceHandler.updateMoveIndex(1);
                GameOver = peiceHandler.moveIndx >= gameEndIndx;
                peiceHandler.ChangeBoard(chessPieceBoard,peicesAtLocations,isWhiteTurn);
                saveIndicator.setText((peiceHandler.moveIndx + 1) + "/" + (peiceHandler.maxIndex+1));
                setEvalBar(whiteEval,blackEval,whiteadvantage,blackadvantage,peiceHandler.getSimpleEval());
                unselectEveryThing();


            }
        });

        reset.setOnMouseClicked(e ->{
            if(peiceHandler.moveIndx != -1){
                peiceHandler.moveIndx = -1;
                GameOver = false;
                peiceHandler.ChangeBoard(chessPieceBoard,peicesAtLocations,isWhiteTurn);
                saveIndicator.setText("0/0");
                peiceHandler.clearIndx();
                setEvalBar(whiteEval,blackEval,whiteadvantage,blackadvantage,peiceHandler.getSimpleEval());
                unselectEveryThing();

            }
        });


    }

    private void setEvalBar(Label whiteEval, Label blackEval, Rectangle whiteBar, Rectangle blackBar, double advantage){
        double barModPercent = passThroughAsymptote(Math.abs(advantage))/5;
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        System.out.println(advantage);
        if(advantage >= 0){
            // white advantage or equal position
            if(advantage < 1000000){
                whiteEval.setText(decimalFormat.format(advantage));
            }
            else{
                whiteEval.setText("M");

            }
            blackEval.setText("");
            whiteBar.heightProperty().bind(chessPieceBoard.heightProperty().divide(2).add(chessPieceBoard.heightProperty().divide(2).multiply(barModPercent)));
            blackBar.heightProperty().bind(chessPieceBoard.heightProperty().divide(2).multiply(1-barModPercent));
            
        }
        else{
            System.out.println("here");
            if(advantage > -1000000){
                blackEval.setText(decimalFormat.format(advantage));
            }
            else{
                blackEval.setText("M");

            }
            whiteEval.setText("");
            blackBar.heightProperty().bind(chessPieceBoard.heightProperty().divide(2).add(chessPieceBoard.heightProperty().divide(2).multiply(barModPercent)));
            whiteBar.heightProperty().bind(chessPieceBoard.heightProperty().divide(2).multiply(1-barModPercent));


        }


    }

    private double passThroughAsymptote(double advantage){
        return (5 * Math.pow(advantage,2))/(Math.pow(advantage,2)+0.5*advantage + 10);
    }

    public void unselectEveryThing()
    {
        if(peiceSelected){
            removeHiglight(selectedPeiceInfo[0],selectedPeiceInfo[1]);
        }
        peiceSelected = false;
        if(oldHighights != null){
            for(String s : oldHighights){
                String[] coords = s.split(",");
                int a = Integer.parseInt(coords[0]);
                int b = Integer.parseInt(coords[1]);

                removeHiglight(a,b);
            }
        }

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
            case "Cherry" -> new String[]{"#f7b2ad", "#8c2155"};
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
                    piece.fitHeightProperty().bind(chessPieceBoard.heightProperty().divide(9));
                    piece.fitWidthProperty().bind(chessPieceBoard.widthProperty().divide(8));
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



    private void setUpSquareClickEvent(StackPane square) {
        square.setOnMouseClicked(event -> {

            StackPane pane = (StackPane) event.getSource();
            String[] xy = pane.getUserData().toString().split(",");
            int x = Integer.parseInt(xy[0]);
            int y = Integer.parseInt(xy[1]);
            //System.out.println("X: " + x);
            //System.out.println("Y: " + y);
            if (event.getButton() == MouseButton.PRIMARY && !GameOver) {

                boolean[] boardInfo = peiceHandler.checkIfContains(x, y);
                //System.out.println("IsHit:" + boardInfo[0] + " isWhite: " + boardInfo[1]);
                // possible move
                //System.out.println("PrevPeice Selected: " + peiceSelected);
                System.out.println(GameOver);
                if (peiceSelected) {
                    boolean prevPeiceClr = (selectedPeiceInfo[2] > 0);
                    int oldX = selectedPeiceInfo[0];
                    int oldY = selectedPeiceInfo[1];
                    //System.out.println(prevPeiceClr);
                    //System.out.println(boardInfo[1]);
                    if (!boardInfo[0] || prevPeiceClr != boardInfo[1]) {
                        // enemy colors or empty square

                        //System.out.println("Moving " + prevPeiceClr + " peice from " + oldX + "," + oldY + " to " + x + "," + y);
                        boolean[] moveInfo = checkIfMovePossible(oldHighights,x,y);
                        if(moveInfo[0]){
                            for(String s : oldHighights){
                                String[] coords = s.split(",");
                                int a = Integer.parseInt(coords[0]);
                                int b = Integer.parseInt(coords[1]);
                                removeHiglight(a,b);
                            }
                            // check if rook move off starting square
                            if(oldX == 7 && oldY == 7){
                                peiceHandler.removeRookMoveRight(7,7);
                            }
                            else if(oldX == 0 && oldY == 7){
                                peiceHandler.removeRookMoveRight(0,7);
                            }
                            else if(oldX == 0 && oldY == 0){
                                peiceHandler.removeRookMoveRight(0,0);
                            }
                            else if(oldX == 7 && oldY == 0){
                                peiceHandler.removeRookMoveRight(7,0);
                            }
                            if(moveInfo[1]){
                                // performing castling move
                                int jump = x == 6? 1 : -1;
                                peiceHandler.removeRookMoveRight(x+jump, y);
                                peiceHandler.movePiece(prevPeiceClr,x+jump,y,x-jump,y,true);
                                peicesAtLocations[x-jump][y] = peicesAtLocations[x+jump][y];
                                GridPane.setRowIndex(peicesAtLocations[x-jump][y],y);
                                GridPane.setColumnIndex(peicesAtLocations[x-jump][y],x-jump);
                                peicesAtLocations[x+jump][y] = null;
                                peiceHandler.removeCastlingRight(prevPeiceClr);
                                System.out.println("Castle right black: " + peiceHandler.blackCastleRight);
                                System.out.println("Short rook black: " + peiceHandler.blackShortRookMove);

                            }
                            if(peiceHandler.getPieceType(oldX,oldY,prevPeiceClr).equals("King")){
                                peiceHandler.removeCastlingRight(prevPeiceClr);

                            }





                            peiceHandler.movePiece(prevPeiceClr,oldX,oldY,x,y,boardInfo[0]);
                            if(boardInfo[0]){
                                // remove enemy peice
                                peiceHandler.removePeice(boardInfo[1],x,y);
                                chessPieceBoard.getChildren().remove(peicesAtLocations[x][y]);

                            }
                            removeHiglight(oldX, oldY);
                            peicesAtLocations[oldX][oldY] = null;



                            GridPane.setRowIndex(selectedPeice,y);
                            GridPane.setColumnIndex(selectedPeice,x);
                            peicesAtLocations[x][y] = selectedPeice;
                            peiceSelected = false;

                            isWhiteTurn = !isWhiteTurn;
                            saveIndicator.setText((peiceHandler.moveIndx + 1) + "/" + (peiceHandler.maxIndex+1));

                            oldHighights = null;
                            setEvalBar(whiteEval,blackEval,whiteadvantage,blackadvantage,peiceHandler.getSimpleEval());
                            System.out.println(peiceHandler.isChecked(!prevPeiceClr));
                            if(peiceHandler.isChecked(!prevPeiceClr) && !peiceHandler.isKingMovePossible(!prevPeiceClr)){
                                victoryLabel.setText("Winner : " + (prevPeiceClr ? "White" : "Black"));
                                setEvalBar(whiteEval,blackEval,whiteadvantage,blackadvantage,prevPeiceClr ? 1000000 : -1000000);
                                GameOver = true;
                                gameEndIndx = peiceHandler.moveIndx;

                            }


                        }


                    }
                    else {
                        // your own peice color
                        //System.out.println("Own color");
                        //System.out.println(peiceHandler.getPieceType(x,y,boardInfo[1]));

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
                    if(boardInfo[1] == isWhiteTurn){
                        //System.out.println(peiceHandler.getPieceType(x,y,boardInfo[1]));

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

    private boolean[] checkIfMovePossible(List<String> moves, int x, int y){
        // todo: change this to bitboard logic
        for(String s : moves){
            String[] coords = s.split(",");
            int a = Integer.parseInt(coords[0]);
            int b = Integer.parseInt(coords[1]);
            boolean isCastle =false;
            if(coords.length > 2){
                 isCastle = coords[2].equals("c");
            }


            if(a == x && b == y && isCastle){
                return new boolean[]{true,true};
            }
            else if(a == x && b == y){
                return new boolean[]{true,false};
            }
        }
        return new boolean[]{false,false};
    }
}
