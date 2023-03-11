
package games.stack;

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

@Deserialize(as = "Stack")
public class Stack implements GamePrototype{
    private StackView gui;
    private static final int INIT_SIZE = 5;
    private static final Category CATEGORY = Category.BOARD;
    private String title;
    private List<Point2D> barriers;
    private List<Integer> steps;
    private int rows;
    private int columns;
    private int level = 0;
    private int[][] boardMatrix;
    private static final Logger LOGGER = Logger.getLogger(LogicGameCollection.class.getName());
    
    public Stack(){
        barriers = new ArrayList<>();
        steps = new ArrayList<>();
        rows = INIT_SIZE;      
        columns = INIT_SIZE;
        boardMatrix = new int[rows][columns];
        Locale defaultLocale = Locale.getDefault();
        ResourceBundle texts = ResourceBundle.getBundle("games.bundles.StackBundle",defaultLocale); 
        title = texts.getString("title");
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
    
    public int getRows(){
        return rows;
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
            boardMatrix[row][column] = 0;  
            barriers.add(new Point2D(column, row));
        }else LOGGER.log(Level.WARNING, "Invalid arguments for method setBarrier:"
                + " {0}, {1}.", new Object[]{row,column});
    }
    
    @Deserialize(as="steps")
    public void setSteps(String numbers){
        String []parts = numbers.split("(\\s*),+(\\s*)");
        for (String p : parts){
            try {
                int number = Integer.parseInt(p);
                steps.add(number);
            }catch(NumberFormatException e){
                Logger.getLogger(LogicGameCollection.class.getName())
                        .log(Level.WARNING, "Wrong number format in steps' definition.", e);
            }
        }
    }
    
    public List<Integer> getSteps(){
        return steps;
    }
    
    public int[][] getBoardMatrix(){
        return boardMatrix;
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
                boardMatrix[i][j] = 1;
            }
        }
    }
    
    public void clearBoard(){
        fillBoardMatrix();
        int i,j;
        for (Point2D p : barriers){
            i = (int)p.getY();
            j = (int)p.getX();
            boardMatrix[i][j] = 0;
        }
    }
    
    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    @Deserialize(as = "level")
    public void setLevel(int level) {
         this.level = level;
    }

    @Override
    public Category getCategory() {
        return this.CATEGORY;
    }

    @Override
    public GameView createGui(SceneController sceneCntrl, ResourceBundle bundle) {
        gui = new StackView(this, sceneCntrl, bundle);
        return gui;
    }

    @Override
    public String getClassName() {
        return Stack.class.getSimpleName();
    }

    @Override
    public void start() {
        gui.start();
    }
    
    
}
