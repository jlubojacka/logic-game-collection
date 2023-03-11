
package games.motherboard;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile extends Rectangle implements GameElement{
    private int row;
    private int column;
      
    public Tile(double size, int row, int column){
        this.setWidth(size);
        this.setHeight(size);
        this.row = row;
        this.column = column;
        this.setStroke(Color.GRAY);
        this.setOpacity(0.9);
    }
    
    public double getCenterX(){
        return this.getTranslateX() + this.getWidth()/2.0;
    }
    
    public double getCenterY(){
        return this.getTranslateY() + this.getHeight()/2.0;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getColumn() {
        return column;
    }
    
}
