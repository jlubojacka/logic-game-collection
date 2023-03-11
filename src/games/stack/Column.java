
package games.stack;

import java.util.List;
import java.util.ListIterator;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Column extends StackPane{
    private List<Integer> steps;
    private int iterator = 0;
    private IntegerProperty value;
    private Circle circle;
    private Text numberText;
    private double radius;
    private int row;
    private int column;
    
    public Column(List<Integer> steps, double size, int row, int column){
        this.steps = steps;
        this.column = column;
        this.row = row;
        value = new SimpleIntegerProperty(1);
        this.radius = size/2;
        circle = new Circle(radius,Color.BLUE);
        numberText = new Text();
        numberText.setFill(Color.WHITE);
        numberText.setFont(Font.font(20));
        numberText.textProperty().bind(value.asString());
        this.getChildren().addAll(circle, numberText);
    }
    
    public void setCenterX(double value){
        this.setTranslateX(value - radius);
    }
    
    public void setCenterY(double value){
        this.setTranslateY(value - radius);
    }
    
    public int getValue(){
        return value.getValue();
    }
    
    public void setValue(int number){
        value.setValue(number);
    }
    
    public void resetIterator(){
        iterator = 0;
    }
    
    public void nextStep(){
        iterator++;
    }
    
    public int getCurrentStep(){
        if (iterator >= steps.size()){
            return -1;
        }
        return steps.get(iterator);
    }
    

    /**
     * @return the row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return the column
     */
    public int getColumn() {
        return column;
    }
    
    public void setHighlight(boolean set){
        if (set){
            circle.setFill(Color.DARKBLUE);
        }else{
            circle.setFill(Color.BLUE);
        }
    }
        
}
