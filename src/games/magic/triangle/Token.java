
package games.magic.triangle;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Token extends StackPane {
    private Circle circle;
    private Text numberText;
    private IntegerProperty number;
    private double diametr;
    
    public Token(double diametr, double centerX, double centerY){
        this.diametr = diametr;
        number = new SimpleIntegerProperty(0);
        circle = new Circle(diametr/2, Color.PALETURQUOISE);
        this.setTranslateX(centerX - diametr/2);
        this.setTranslateY(centerY - diametr/2);
        numberText = new Text();
        numberText.setFont(Font.font("", FontWeight.BOLD, diametr/2 * 0.73));
        numberText.textProperty().bind(number.asString());
        this.getChildren().addAll(circle, numberText);
        Light.Distant light = new Light.Distant();
        light.setAzimuth(-100.0f);
        light.setElevation(60);
        Lighting l = new Lighting();
        l.setLight(light);
        l.setSurfaceScale(3.0f);
        circle.setEffect(l);
    }
    
    public void setNumber(int n){
        number.setValue(n);
    }
    
    public int getNumber(){
        return number.getValue();
    }
    
    public double getCenterX(){
        return this.getTranslateX() + diametr/2;
    }
    
    public double getCenterY(){
        return this.getTranslateY() + diametr/2;
    }
    
    public void highlightOn(boolean on){
        if (on){
            circle.setStroke(Color.CORNFLOWERBLUE);
            circle.setStrokeWidth(2.5);
        }else {
            circle.setStroke(Color.AQUAMARINE);
            circle.setStrokeWidth(1.0);
        }
    }
    
    
}
