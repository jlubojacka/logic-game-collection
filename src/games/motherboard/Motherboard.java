
package games.motherboard;

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

@Deserialize(as = "Motherboard")
public class Motherboard implements GamePrototype{
    private MotherboardView gui;
    private static final int INIT_SIZE = 6;
    private static final Category CATEGORY = Category.BOARD;
    private String title;
    private List<Point2D> barriers;
    private List<Connection> connections;
    private int rows;
    private int columns;
    private int level = 0;
    private int[][] boardMatrix;
    private static final Logger LOGGER = Logger.getLogger(LogicGameCollection.class.getName());

    
    public Motherboard(){
        barriers = new ArrayList<>();
        rows = INIT_SIZE;      
        columns = INIT_SIZE;
        boardMatrix = new int[rows][columns];
        connections = new ArrayList<>();
        Locale defaultLocale = Locale.getDefault();
        ResourceBundle texts = ResourceBundle.getBundle("games.bundles.MotherboardBundle",defaultLocale); 
        title = texts.getString("title");
    }
    
    public int[][] getBoardMatrix(){
        return boardMatrix;
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
    
    @Deserialize(as = "barrier")
    public void setBarrier(int row, int column){
        if (((row >= 0) && (row < rows)) && ((column >= 0) && (column < columns))){
            boardMatrix[row][column] = 1;  
            barriers.add(new Point2D(column, row));
        }else LOGGER.log(Level.WARNING, "Invalid arguments for method setBarrier:"
                + " {0}, {1}.", new Object[]{row,column});
    }
    
    @Deserialize(as = "connection")
    public void addConnection(int firstRow, int firstCol, int secondRow, int secondCol){
        testNset(firstRow, firstCol, secondRow, secondCol);
    }
    
    public List<Connection> getConnections(){
        return connections;
    }
    
    private void testNset(int firstRow, int firstCol, int secondRow, int secondCol){
        int fRow = 0;
        int fCol = 0;
        int sRow = 0; 
        int sCol = 0;
        if ((firstRow >= 0) && (firstRow < rows))
            fRow = firstRow;
        if ((secondRow >= 0) && (secondRow < rows))
            sRow = secondRow;
        if ((firstCol >= 0) && (firstCol < columns))
            fCol = firstCol;
        if ((secondCol >= 0) && (secondCol < columns))
            sCol = secondCol;
        
        boardMatrix[fRow][fCol] = 2;
        boardMatrix[sRow][sCol] = 2;
        connections.add(new Connection(fRow, fCol, sRow, sCol));
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
    
    public void clearBoard(){
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < columns; j++){
                boardMatrix[i][j] = 0;
            }
        }
        
        int i,j;
        for (Point2D p : barriers){
            i = (int)p.getY();
            j = (int)p.getX();
            boardMatrix[i][j] = 1;
        }
    }
    
    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    @Deserialize(as = "level")
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public Category getCategory() {
        return CATEGORY;
    }

    @Override
    public GameView createGui(SceneController sceneCntrl, ResourceBundle bundle) {
        gui = new MotherboardView(this,sceneCntrl, bundle);
        return gui;
    }

    @Override
    public String getClassName() {
        return Motherboard.class.getSimpleName();
    }

    @Override
    public void start() {
        gui.start();
    }
    
}
