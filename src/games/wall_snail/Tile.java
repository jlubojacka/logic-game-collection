
package games.wall_snail;

import javafx.scene.effect.Light.Distant;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile extends Rectangle{
    
    private int size;
    
    public Tile(Color color, int size){
        super(size, size, color);
        this.size = size;
        this.setStroke(Color.BLACK);
        setupEffects();
    }
    
    private void setupEffects(){
        Distant light = new Distant();
        light.setAzimuth(-100.0f);
        light.setElevation(60);
        Lighting l = new Lighting();
        l.setLight(light);
        l.setSurfaceScale(3.0f);
        this.setEffect(l);
    }
    
}
