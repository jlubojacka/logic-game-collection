
package games.knight.tour;

import deserializer.Deserialize;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point2D;
import logic.game.collection.Category;
import logic.game.collection.GamePrototype;
import logic.game.collection.GameView;
import logic.game.collection.LogicGameCollection;
import scenecontroller.SceneController;

@Deserialize(as = "KnightTour")
public class KnightTour implements GamePrototype{
    
    private static final Category CATEGORY = Category.IT;
    private static final int INIT_SIZE = 5;
    private int level = 0;
    private String title;
    private int rows;
    private int columns;
    private int [][] boardMatrix;
    private int knightRowPos = -1;
    private int knightColPos = -1;
    private int initKnightRowPos = 0;
    private int initKnightColPos = 0;
    private List<Point2D> barriers;
    private KnightTourView gui;
    private int counter;
    private static final Logger LOGGER = Logger.getLogger(LogicGameCollection.class.getName());
    
    public KnightTour(){
        rows = INIT_SIZE;      
        columns = INIT_SIZE;
        boardMatrix = new int[rows][columns];
        fillBoardMatrix();
        Locale defaultLocale = Locale.getDefault();
        ResourceBundle texts = ResourceBundle.getBundle("games.bundles.KnightTourBundle",defaultLocale); 
        title = texts.getString("title");
        barriers = new ArrayList<>(); 
        counter = 0;
    }
    
    public int getRows() {
        return rows;
    }

    @Deserialize(as = "rows")
    public void setRows(int rows) {
        if (rows > 0){
            this.rows = rows;
        }else {
            LOGGER.log(Level.WARNING, "Invalid argument for method setRows: {0}. "
                    + "Value ignored.", rows);
        }
    }

    public int getColumns() {
        return columns;
    }

    @Deserialize(as = "columns")
    public void setColumns(int columns) {
        if (columns > 0){
            this.columns = columns;
            changeBoardSize();
        }else {
            LOGGER.log(Level.WARNING, "Invalid argument for method setColums: {0}. "
                    + "Value ignored.", columns);
        }
    }

    private void changeBoardSize(){
        if ((rows != INIT_SIZE)||(columns != INIT_SIZE)){
            boardMatrix = new int[rows][columns];
            fillBoardMatrix();
        }
    }
    
    private void fillBoardMatrix(){
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < columns; j++){
                boardMatrix[i][j] = 0;
            }
        }
    }
    
    @Override
    public int getLevel() {
        return level;
    }

    @Deserialize(as = "level")
    @Override
    public void setLevel(int level) {
        this.level = level;
    }
    
    @Deserialize(as = "barrier")
    public void setBarrier(int row, int column){
        if (inBoard(column,row)){
            barriers.add(new Point2D(column, row));
            boardMatrix[row][column] = 2;  
        }else {
            LOGGER.log(Level.WARNING, "Invalid arguments for method setBarrier: {0}, {1} "
                    + "Values ignored.", new Object[]{row, column});
        }
    }
    
    
    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Category getCategory() {
        return CATEGORY;
    }
    
    @Deserialize(as = "knight")
    public void setKnightPosition(int row, int column){
        if (inBoard(column, row)){
            initKnightRowPos = row;
            initKnightColPos = column;
            boardMatrix[row][column] = 1;
        }else {
            LOGGER.log(Level.WARNING, "Invalid arguments for method setKnightPosition: "
                    + "{0}, {1}. Values ignored.", new Object[]{row, column});
        }
    }
    
    public int[][] getBoardMatrix(){
        return boardMatrix;
    }
    
    public Point2D getKnightPosition(){
        return new Point2D(knightColPos, knightRowPos);
    }
    
    public Point2D getInitKnightPosition(){
        return new Point2D(initKnightColPos, initKnightRowPos);
    }
    
    public void restart(){
        counter = 1;
        knightRowPos = initKnightRowPos;
        knightColPos = initKnightColPos;
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < columns; j++){
                boardMatrix[i][j] = 0;
            }
        }
        boardMatrix[knightRowPos][knightColPos] = 1;
        for (Point2D p : barriers){
            boardMatrix[(int)p.getY()][(int)p.getX()] = 2;
        }
    }
    
    public List<Point2D> getPossibleMoves(){
        List<Point2D> moves = new ArrayList<>();
        //get 8 possible positions if not out of board (4+4)
        getMoves(moves,knightColPos,knightRowPos,2,1); //horizontal half
        getMoves(moves,knightColPos,knightRowPos,1,2); //vertical half
        return moves;
    }
    
    private void getMoves(List<Point2D> moves, int x, int y, int hDiff, int vDiff){
        int coeff = -1;
        int posX;
        int posY;
        for (int i =0; i < 4; i++){
            coeff *= -1;
            if ( i < 2){
                posX = x + hDiff;
            }else {
                posX = x - hDiff;
            }
            posY = y + (vDiff * coeff);
            if (inBoard(posX,posY)){
                if (boardMatrix[posY][posX] == 0){ //free space
                    Point2D p = new Point2D(posX,posY);
                    moves.add(p);
                }
            }
        }
    }
    
    private boolean inBoard(int posX, int posY){
        if ((posY < 0)||(posY >= rows))
            return false;
        if ((posX >= 0) && (posX < columns))
            return true;
        return false;
    }
    
    public void incCounter(){
        counter++;
    }
    
    public boolean isFilled(){
        if (counter == ((rows * columns) - barriers.size())){
            return true;
        }
        return false;
    }
    
    public void moveKnight(int row, int column){
        if (inBoard(column, row)){
            knightRowPos = row;
            knightColPos = column;
            boardMatrix[row][column] = 1;
        }
    }
    
    @Override
    public GameView createGui(SceneController sceneCntrl, ResourceBundle bundle) {
        gui = new KnightTourView(this, sceneCntrl, bundle);
        return gui;
    }

    @Override
    public String getClassName() {
        return KnightTour.class.getSimpleName();
    }

    @Override
    public void start() {
        gui.start();
    }
    
}
