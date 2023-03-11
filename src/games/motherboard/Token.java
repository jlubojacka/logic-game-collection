
package games.motherboard;

import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.shape.Circle;

public class Token extends Circle implements GameElement{
    private int row;
    private int column;
    
    public Token(double diameter, int row, int column){
        this.row = row;
        this.column = column;
        this.setRadius(diameter/2.0);
        
        Light.Distant light = new Light.Distant();
        light.setAzimuth(-100.0f);
        light.setElevation(60);
        Lighting l = new Lighting();
        l.setLight(light);
        l.setSurfaceScale(3.0f);
        this.setEffect(l);
    }

    /**
     * @return the row
     */
    @Override
    public int getRow() {
        return row;
    }

    /**
     * @return the column
     */
    @Override
    public int getColumn() {
        return column;
    }
    
}
