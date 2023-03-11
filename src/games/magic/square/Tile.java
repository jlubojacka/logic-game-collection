
package games.magic.square;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.effect.Light.Distant;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Tile extends StackPane{
    private int size;
    private Rectangle r;
    private Text numberText;
    private int number;
    private SimpleIntegerProperty xPos;
    private SimpleIntegerProperty yPos;
    
    public Tile(int size, int number){
        this.size = size;
        this.number = number;
        xPos = new SimpleIntegerProperty(-1);
        yPos = new SimpleIntegerProperty(-1);
        createGroup();
        setupEffects();
    }
    
    private void createGroup(){
        r = new Rectangle(size, size, Color.ALICEBLUE);
        numberText = new Text(String.valueOf(number));
        numberText.setFont(new Font(size / 2.4));
        this.getChildren().addAll(r,numberText);
    }
    
    public int getNmuber(){
        return number;
    }
    
    public void setColor(Color c){
        r.setFill(c);
    }
    
    public SimpleIntegerProperty xPosProperty(){
        return xPos;
    }
    
    public SimpleIntegerProperty yPosProperty(){
        return yPos;
    }
    
    public void setXPos(int value){
        this.xPos.setValue(value);
    }
    
    public void setYPos(int value){
        this.yPos.setValue(value);
    }
    
    public int getXPos(){
        return xPos.getValue();
    }
    
    public int getYPos(){
        return yPos.getValue();
    }
    
    private void setupEffects(){
        Distant light = new Distant();
        light.setAzimuth(-100.0f);
        light.setElevation(60);
        Lighting l = new Lighting();
        l.setLight(light);
        l.setSurfaceScale(3.0f);
        r.setEffect(l);
    }
    
}
