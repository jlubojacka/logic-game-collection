
package games.motherboard;

import java.util.List;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Line;
/*
* Group of Lines between two tokens
*/
public class TracesManager extends Group{
    private Trace currentTrace;
    private Token selectedToken;
    private boolean moveEnabled = false;
    private int[][] board;
    private double unitSize;
    
    public TracesManager(double unitSize, int[][] board){
        this.unitSize = unitSize;
        this.board = board;
        this.setMouseTransparent(true);
    }
    
    public void addTrace(Trace t){
        this.getChildren().add(t);
    }
    
    public void tokenClicked(Token t){
        if (selectedToken == null){
            selectedToken = t;
            currentTrace = findTrace(t);
            currentTrace.setStart(t.getCenterX(), t.getCenterY());
            currentTrace.addLine(t.getCenterX(), t.getCenterY());
            moveEnabled = true;
        }else {
            if (t.equals(selectedToken)){
                //no action
            }else if (currentTrace.consistsOf(t)){
                //check adjacency in matrix , eventually close trace
                if (testClick(t)){
                    currentTrace.moveLine(t.getCenterX(), t.getCenterY());
                    currentTrace.close(true);
                    moveEnabled = false;
                }
                
            }else { 
                //different token
                if (!currentTrace.isClosed()){
                    clearTrace(currentTrace);
                    currentTrace.removeAll();
                }
                
                Trace possible = findTrace(t);
                if (!possible.isClosed()){
                    selectedToken = t;
                    currentTrace = possible;
                    currentTrace.setStart(t.getCenterX(), t.getCenterY());
                    currentTrace.addLine(t.getCenterX(), t.getCenterY());
                    moveEnabled = true;
                }
            }
        }
    }
    
    public void removeTrace(Token t){
        Trace requested = findTrace(t);
        if ((currentTrace != null) && (currentTrace.consistsOf(t))){
            selectedToken = null;
            currentTrace = null;
            moveEnabled = false;
        }
        requested.close(false);
        clearTrace(requested);
        requested.removeAll();
        
        
    }
    
    public void tileClicked(Tile t){
        if (selectedToken != null){
            boolean valid = testClick(t);
            if (valid){
                moveEnabled = false;
                currentTrace.addLine(t.getCenterX(), t.getCenterY());
                moveEnabled = true;
            }
        }
    }
    
    public void moveInBoard(double localX, double localY){
        currentTrace.moveLine(localX, localY);
    }
    
    private boolean testClick(GameElement t){
        int rowLast = (int) (currentTrace.getLastLine().getStartY()/unitSize);
        int columnLast = (int) (currentTrace.getLastLine().getStartX()/unitSize);
        int rowNew = t.getRow();
        int columnNew = t.getColumn();
        
        boolean result;
        switch(board[rowNew][columnNew]){
            case 1: result = false; break; //barriew
            case 3: result = false; break; //filled, another line
            case 2:                  //circle, possible close
            case 0: result = testAdjacency(rowLast, columnLast, rowNew, columnNew); //free space
                    break;
            default: result = false; break;
        }
        
        return result;
    }
   
    private boolean testAdjacency(int fRow, int fCol, int sRow, int sCol){
        if (fRow == sRow){ //same rows, different columns
            if (fCol == sCol)
                return false;
            if (fCol < sCol){
                return testRow(fRow, fCol, sCol, 1);
            }else return testRow(fRow, fCol, sCol, -1);
            
        }else if (fCol == sCol){// same columns
            if (fRow == sRow)
                return false;
            if (fRow < sRow){
                return testColumn(fCol, fRow, sRow, 1);
            }else return testColumn(fCol, fRow, sRow, -1);
            
        } else return false; //diagonals not allowed
    }
    
    private boolean testRow(int row, int fromCol, int toCol, int sign){
        for (int i = fromCol + sign; i != toCol + sign; i = i + sign){
            if (i == toCol){
                if (board[row][i] != 0){
                    if (board[row][i] != 2)
                        return false;
                }
            }else if (board[row][i] != 0)
                return false;
        }
        
        //free space, valid input -> update matrix
        writeRow(row, fromCol, toCol, sign, 3);
        
        return true;
    }
    
    
    private boolean testColumn(int column, int fromRow, int toRow, int sign){
        for (int i = fromRow + sign; i != toRow + sign; i = i + sign){
            if (i == toRow){
                if (board[i][column] != 0){
                    if (board[i][column] != 2)
                        return false;
                }
            }else if (board[i][column] != 0)
                return false;
        }
        //valid input
        writeColumn(column, fromRow, toRow, sign, 3);
        return true;
    }
    
    private void writeRow(int row, int fromCol, int toCol, int sign, int number){
        for (int i = fromCol + sign; i != toCol + sign; i = i + sign){
            if ((i == toCol) && (board[row][i] == 2)){
                //save number 2
            }else {
                board[row][i] = number;
            }
        }
    }
    
    private void writeColumn(int column, int fromRow, int toRow, int sign, int number){
        for (int i = fromRow + sign; i != toRow + sign; i = i + sign){
            if ((i == toRow) && (board[i][column] == 2)){
                //no change
            }else {
                board[i][column] = number;
            }
        }
    }
    
    //remove trace from matrix
    private void clearTrace(Trace t){
        List<Line> lines = t.getLines();
        for (Line l : lines){
            int startRow = (int) (l.getStartY()/unitSize);
            int startCol = (int) (l.getStartX()/unitSize);
            int endRow = (int) (l.getEndY()/unitSize);
            int endCol =(int) (l.getEndX()/unitSize);
                    
            if (startRow == endRow){ //same rows
                if (startCol < endCol){
                    writeRow(startRow, startCol, endCol, 1, 0);
                }else writeRow(startRow, startCol, endCol, -1, 0);
            
            
            }else if (startCol == endCol){// same columns
                if (startRow < endRow){
                    writeColumn(startCol, startRow, endRow, 1, 0);
                }else writeColumn(startCol, startRow, endRow, -1, 0);
            }
        }
        t.removeAll();
        t.close(false);
        
    }
    
    private Trace findTrace(Token t){
        for (Node n : this.getChildren()){
            Trace trace = (Trace) n;
            if (trace.consistsOf(t))
                return trace;
        }
        return null;
    }
    
    public boolean isMoveEnabled(){
        return moveEnabled;
    }
    
    public void reset(){
        moveEnabled = false;
        for (Node n : this.getChildren()){
            Trace trace = (Trace) n;
            trace.close(false);
            trace.removeAll();
        }
        selectedToken = null;
        currentTrace = null;
        resetMatrix();
    }
    
    private void resetMatrix(){
        for (int i =0; i < board.length; i++){
            for (int j=0; j < board[i].length; j++){
                if (board[i][j] == 3){
                    board[i][j] = 0;
                }
            }
        }
    }
    
    public boolean won(){
        for (Node n: this.getChildren()){
            Trace t = (Trace)n;
            if (!t.isClosed())
                return false;
        }
        return true;
        
    }
    
}
