
package games.wall_snail;

import java.util.logging.*;
import deserializer.Deserialize;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import logic.game.collection.Category;
import logic.game.collection.GamePrototype;
import logic.game.collection.GameView;
import logic.game.collection.LogicGameCollection;
import scenecontroller.SceneController;


@Deserialize(as = "WallSnail")
public class WallSnail implements GamePrototype{
    private WallSnailView gui;
    private static final int INIT_SIZE = 5;
    private static final Category CATEGORY = Category.BOARD;
    private String title;
    private List<Direction> possibleDirections = new ArrayList<>();
    private List<Point2D> barriers;
    private int rows;
    private int columns;
    private int level = 0;
    private IntegerProperty[][] boardMatrix;
    private int tokenRowPosition = -1;
    private int tokenColPosition = -1;
    private static final Logger LOGGER = Logger.getLogger(LogicGameCollection.class.getName());
    
    public WallSnail(){
        rows = INIT_SIZE;      
        columns = INIT_SIZE;
        boardMatrix = new SimpleIntegerProperty[rows][columns];
        fillBoardMatrix();
        Locale defaultLocale = Locale.getDefault();
        ResourceBundle texts = ResourceBundle.getBundle("games.bundles.WallSnailBundle",defaultLocale); 
        title = texts.getString("title");
        barriers = new ArrayList<>();
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
        boolean min = ((row >= 0) && (column >= 0));
        boolean max = ((row < this.rows) && (column < this.columns));
        if (min && max){
            boardMatrix[row][column].setValue(1);  
            barriers.add(new Point2D(column, row));
        }else {
            LOGGER.log(Level.WARNING, "Invalid arguments for method setBarrier: {0}, {1} "
                    + "Values ignored.", new Object[]{row, column});
        }
    }
    
    private void changeBoardSize(){
        if ((rows != INIT_SIZE)||(columns != INIT_SIZE)){
            boardMatrix = new SimpleIntegerProperty[rows][columns];
            fillBoardMatrix();
        }
    }
    
    private void fillBoardMatrix(){
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < columns; j++){
                boardMatrix[i][j] = new SimpleIntegerProperty(0);
            }
        }
    }
    
    public int move(Direction direction){
        int count = 0;
        if ((direction == Direction.WEST)||(direction == Direction.EAST)){
            while(true){
                int newPosition = tokenColPosition + direction.value();
                if ((newPosition < 0)||(newPosition >= boardMatrix.length)){
                    break;
                }else if(boardMatrix[tokenRowPosition][newPosition].getValue() == 0){
                    tokenColPosition = newPosition;
                    boardMatrix[tokenRowPosition][newPosition].setValue(2);
                    count ++;
                }else {
                    break;
                }
            }
        }else {
            while(true){
                int newPosition = tokenRowPosition + direction.value();
                if ((newPosition < 0)||(newPosition >= boardMatrix[0].length)){
                    break;
                }else if(boardMatrix[newPosition][tokenColPosition].getValue() == 0){
                    tokenRowPosition = newPosition;
                    boardMatrix[newPosition][tokenColPosition].setValue(2);
                    count++;
                }else{
                    break;
                }
            }
        }
        return count;
    }
    
    public boolean isFilled(){
        boolean full = true;
        for (int i = 0; i < rows; i++){
            for (int j=0; j < columns; j++){
                if (boardMatrix[i][j].getValue() == 0){
                    full = false;
                    break;
                }
            }
        }
        return full;
    }
    
    public void setTokenPosition(int row, int column){
        this.tokenRowPosition = row;
        this.tokenColPosition = column;
        boardMatrix[row][column].setValue(2);
        
    }
    
    public Point2D getTokenPosition(){
        return new Point2D(tokenColPosition, tokenRowPosition);
    }
    
    public List<Direction> getDirectionHint(){
        possibleDirections.clear();
        
        if (((tokenRowPosition + 1) < rows) &&
                (boardMatrix[tokenRowPosition + 1][tokenColPosition].getValue() == 0)){
            possibleDirections.add(Direction.SOUTH);
            
        }
        if (((tokenRowPosition - 1) >= 0) &&
                (boardMatrix[tokenRowPosition - 1][tokenColPosition].getValue() == 0)){
            possibleDirections.add(Direction.NORTH);
            
        }
        if (((tokenColPosition + 1) < columns) &&
                (boardMatrix[tokenRowPosition][tokenColPosition + 1].getValue() == 0)){
            possibleDirections.add(Direction.EAST);
            
        }
        if (((tokenColPosition - 1)>= 0)&&
                (boardMatrix[tokenRowPosition][tokenColPosition - 1].getValue() == 0)){
            possibleDirections.add(Direction.WEST);
            
        }
        
        return possibleDirections;
    }
    
    public boolean isBlocked(int row, int column){
        return (boardMatrix[row][column].getValue() == 1);
    }
    
    public IntegerProperty[][] getBoardMatrix(){
        return boardMatrix;
    }
    
    public void clearBoard(){
        tokenRowPosition = -1;
        tokenColPosition = -1;
        possibleDirections.clear();
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < columns; j++){
                boardMatrix[i][j].setValue(0);
            }
        }
        
        for (Point2D p : barriers){
            boardMatrix[(int)p.getY()][(int)p.getX()].setValue(1);
        }
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public Category getCategory() {
        return this.CATEGORY;
    }

    @Override
    public GameView createGui(SceneController sceneCntrl, ResourceBundle bundle) {
        gui = new WallSnailView(this, sceneCntrl, bundle);
        return gui;
    }
    
    public Parent getGui(){
        return this.gui;
    }
    
    @Override
    public String getClassName(){
        return WallSnail.class.getSimpleName();
    }
    
    @Override
    public void start(){
        gui.start();
    }

    
    
}
