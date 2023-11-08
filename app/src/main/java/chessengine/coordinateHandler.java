package chessengine;

public class coordinateHandler {
    int[] coordsX = new int[8];
    int[] coordsY = new int[8];
    public coordinateHandler(int width, int height){
        System.out.println(width);
        System.out.println(height);
        int CellX = width/8;
        int CellY = height/8;
        int distX = CellX/2;
        int distY = CellY/2;
        int currentMidPointX = distX;
        int currentMidPointY = distY;
        for(int i = 0; i< 8;i++){
            System.out.println("x #" + i + ": " + currentMidPointX);
            coordsX[i] = currentMidPointX;
            System.out.println("y #" + i + ": " + currentMidPointY);

            coordsY[i] = currentMidPointY;
            currentMidPointX+=CellX;
            currentMidPointY+=CellY;


        }
    }

    public int[] getPreciseCoordinates(int x, int y){
        return new int[]{coordsX[x],coordsY[y]};
    }


}
