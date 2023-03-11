
package games.stack;

import games.motherboard.Tile;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Effect;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;
import logic.game.collection.GameView;
import logic.game.collection.LogicGameCollection;
import scenecontroller.SceneController;

public class StackView extends GameView{
    private Stack game;
    private static final int WORLD_WIDTH = LogicGameCollection.WORLD_WIDTH;
    private static final int WORLD_HEIGHT = LogicGameCollection.WORLD_HEIGHT;
    private int unitSize;
    private ResourceBundle texts;
    private Pane gameBoard;
    private List<Column> columns;
    private List<Column> selected;
    private int sum;
    private List<Point2D> hints;
    private List<Rectangle> rectangs;
    private Bloom glow;
    
    public StackView(Stack game, SceneController sc, ResourceBundle bundle) {
        super(sc, bundle);
        this.game = game;
        unitSize = (WORLD_HEIGHT - BOTTOM_PANE_HEIGHT - 30)/ game.getRows();
        Locale defaultLocale = Locale.getDefault();
        texts = ResourceBundle.getBundle("games.bundles.StackBundle",defaultLocale);
        init();
    }

    private void init(){
        columns = new ArrayList<>();
        selected = new ArrayList<>();
        hints = new ArrayList<>();
        rectangs = new ArrayList<>();
        glow = new Bloom();
        createGui();
        
        this.addGameGui(gameBoard);
        this.setInstructionsText(prepareInstructions());
    }
    
    private String prepareInstructions(){
        List<Integer> steps = game.getSteps();
        
        StringBuilder sb = new StringBuilder();
        sb.append(steps.get(0).toString());
        int accum = steps.get(0);
        for (int i=1; i < (steps.size() - 1); i++){
            sb.append(", ");
            accum = accum * steps.get(i);
            sb.append(accum);
        }
        sum = accum * steps.get(steps.size() - 1);

        Object[] messageArguments = {
            sb.toString(),
            sum,
        };
        MessageFormat formatter = new MessageFormat("");
        formatter.setLocale(Locale.getDefault());
        formatter.applyPattern(texts.getString("instructions"));
        String output = formatter.format(messageArguments);
        return output;
    }
    
    private void createGui(){
        createGameBoard();
    }
    
    private void createGameBoard(){
        gameBoard = new Pane();
        gameBoard.setMaxHeight(WORLD_HEIGHT - BOTTOM_PANE_HEIGHT - 30);
        
        int [][]boardMatrix = game.getBoardMatrix();
        
        for (int i = 0; i<game.getRows(); i++){
            for (int j = 0; j<game.getColumns(); j++){
                if (boardMatrix[i][j] != 0){
                    Rectangle tile = new Rectangle(j*unitSize, i*unitSize, unitSize, unitSize);
                    tile.setFill(Color.BISQUE);
                    tile.setStroke(Color.TAN);
                    gameBoard.getChildren().add(tile);
                    rectangs.add(tile);
                    
                    Column token = new Column(game.getSteps(), unitSize - 10, i, j);
                    token.setCenterX((j * unitSize) + unitSize/2.0);
                    token.setCenterY((i * unitSize) + unitSize/2.0);
                    addMouseHandler(token);
                    columns.add(token);
                    gameBoard.getChildren().add(token);
                }
            }
        }
        
        gameBoard.setTranslateX(WORLD_WIDTH/2.0 - (game.getColumns()*unitSize)/2.0);
        gameBoard.setTranslateY(30);
        
    }
    
    private void addMouseHandler(Column c){
        c.setOnMouseClicked((MouseEvent e)->{
            c.setHighlight(true);
            if (selected.isEmpty()){
                selected.add(c);
                showAdjacencyHint(c);
                return;
            }
            //if at least one already selected
            Column last = selected.get(selected.size() - 1);
            if (selected.contains(c)){
                //System.out.println("present");
                return;
            }
            //check same value and adjacency
            boolean sameValue = last.getValue() == c.getValue();
            boolean adjacent = areAdjacent(last,c);
            if ((!sameValue) || (!adjacent)){
                highlight(false);
                selected.clear();
                selected.add(c);
                showAdjacencyHint(c);
                return;
            }
            
            //check if number of clicks is same as current step
            int step = last.getCurrentStep();
            int clicks = selected.size() + 1;
            //System.out.println("Should x Have: " + step + " x " + clicks);
            if (clicks == step){
                //update value of last
                int newValue = last.getValue() * step;
                //System.out.println("New value: " + newValue);
                c.setValue(newValue);
                c.nextStep();
                //set invisible others
                for (int i=0; i < selected.size(); i++){
                    selected.get(i).setVisible(false);
                }
                highlight(false);
                c.setHighlight(false);
                resetAdjacencyHint();
                selected.clear();
                if (newValue == sum){
                    this.setEndGameInfo(true);
                    this.showEndGamePane(adjacent);
                }
                
            }else {
                //not enough, add token to queue
                selected.add(c);
                showAdjacencyHint(c);
                //System.out.println("not enough");
            }  
        });
    }
    
    private void highlight(boolean set){
        for (Column col : selected){
            col.setHighlight(set);
        }
    }
    
    private boolean areAdjacent(Column last, Column test){
        int row1 = last.getRow();
        int col1 = last.getColumn();
        int row2 = test.getRow();
        int col2 = test.getColumn();
        
        if (row1 == row2){
            if (((col1 + 1) == col2) || ((col1 - 1) == col2)){
                return true;
            }
        }
        if (col1 == col2){
            if (((row1 - 1) == row2) || ((row1 + 1) == row2)){
                return true;
            }
        }
        return false;
    }
    
    private void showAdjacencyHint(Column c){
        resetAdjacencyHint();
        int[][] matrix = game.getBoardMatrix();
        int row = c.getRow();
        int column = c.getColumn();
        
        if ((row - 1 >= 0) && (matrix[row - 1][column] == 1)){
            //test if this token has same value
            hints.add(new Point2D(column*unitSize,(row - 1)*unitSize));
        }
        if ((row + 1 < matrix.length) && (matrix[row + 1][column] == 1)){
            hints.add(new Point2D(column*unitSize, (row + 1)*unitSize));
        }
        if ((column - 1 >= 0) && (matrix[row][column - 1] == 1)){
            hints.add(new Point2D((column - 1)*unitSize, row*unitSize));
        }
        if ((column + 1 < matrix[0].length) && (matrix[row][column + 1] == 1)){
            hints.add(new Point2D((column + 1)*unitSize, row*unitSize));
        }
        
        for(Point2D p : hints){
            setEffect(glow, p, c);
        }
        
    }
    
    private void resetAdjacencyHint(){
        if (!hints.isEmpty()){
            for(Point2D p : hints){
                setEffect(null,p,null);
            }
            hints.clear();
        }
    }
    
    private void setEffect(Bloom b, Point2D p, Column clicked){
        Rectangle matchTile = rectangs
                .stream()
                .filter(s-> ((s.getX() == p.getX()) && (s.getY() == p.getY())))
                .findFirst()
                .orElse(null);
        Column matchColumn = columns
                .stream()
                .filter(col-> (col.getRow() == (p.getY()/unitSize)) && (col.getColumn() == (p.getX()/unitSize)))
                .findFirst()
                .orElse(null);
        if ((matchTile == null) || (matchColumn == null)){
            return;
        }
        if (clicked == null){
            matchTile.setEffect(b);
            return;
        }
        if ((matchColumn.getValue() == clicked.getValue()) && matchColumn.isVisible()){
            if (!this.selected.contains(matchColumn)){
                matchTile.setEffect(b);
            }
        }
    }
    
    @Override
    public void start() {
        highlight(false);
        selected.clear();
        for (Column c : columns){
            c.setValue(1);
            c.setVisible(true);
            c.resetIterator();
        }
        
    }
    
}
