
package games.magic.triangle;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;


public class Triangle extends Group{
    private Polygon tShape;
    private Text numberText;
    private int number;
    private double height = 0;
    private List<Token> tokens;
    private boolean rotated = false;
    
    public Triangle(double height, int number){
        this.height = height;
        this.number = number;
        tokens = new ArrayList<>();
        createShape();
        createText();
        this.getChildren().addAll(tShape, numberText);
    }
    
    private void createShape(){  //equilateral triangle
        tShape = new Polygon(new double[]{0.0,height, //A
                                         getSideLength(), height, //B
                                         getSideLength()/2, 0}); //C
        tShape.setFill(Color.PEACHPUFF);
        tShape.setStroke(Color.BLACK);
        tShape.setStrokeLineJoin(StrokeLineJoin.ROUND);
    }
    
    private void createText(){
        numberText = new Text(String.valueOf(number));
        numberText.setFont(Font.font("", FontWeight.BOLD, height/3.7 * 0.73));
        numberText.setTranslateX(getSideLength()/2 - getTextWidth(numberText)/2);
        numberText.setTranslateY(2*height/3);
    }
    
    public double getSideLength(){
        return 2 * height / Math.sqrt(3);          
    }
    
    public int getNumber(){
        return number;
    }
    
    public double getCenterX(){
        return this.getTranslateX() + getSideLength()/2;
    }
    
    public double getCenterY(){
        if (rotated){
            return this.getTranslateY() + height/3;
        }else return this.getTranslateY() + 2*height/3;
    }
    
    public void setCenterX(double x){
        this.setTranslateX(x - getSideLength()/2);
    }
    
    public void setCenterY(double y){
        this.setTranslateY(y - 2*height/3);
    }
    
    public Point2D getA(){
        double x;
        double y;
        if (rotated){
           x = tShape.getTranslateX() + getSideLength();
           y = tShape.getBoundsInParent().getMinY();
        }else {
            x = tShape.getTranslateX();
            y = tShape.getTranslateY() + height;
        }
        Point2D p = new Point2D(x,y);
        return this.localToParent(p);
    }
    
    public Point2D getB(){
        double x;
        double y;
        if (rotated){
           x = tShape.getTranslateX();
           y = tShape.getBoundsInParent().getMinY();
        }else {
            x = tShape.getTranslateX() + getSideLength();
            y = tShape.getTranslateY() + height;
        }
        Point2D p = new Point2D(x,y);
        return this.localToParent(p);
    }
    
    public Point2D getC(){
        double x;
        double y;
        if (rotated){
           x = tShape.getTranslateX() + (getSideLength()/2.0);
           y = tShape.getBoundsInParent().getMinY() + height;
        }else {
            x = tShape.getTranslateX() + getSideLength()/2.0;
            y = tShape.getTranslateY();
        }
        Point2D p = new Point2D(x,y);
        return this.localToParent(p);
    }
    
    public void rotate(){
        //pivot == center of triangle
        tShape.getTransforms().add(
                new Rotate(180, height/Math.sqrt(3), 2*height/3));
        numberText.setTranslateY(numberText.getTranslateY() + height/14);
        rotated = true;
    }   
    
    public void addToken(Token t){
        tokens.add(t);
    }
    
    public int getSum(){
        int sum = 0;
        for(Token t:tokens){
            sum += t.getNumber();
        }
        return sum;
    }
    
    public boolean isSumCorrect(){
        return (number == getSum());
    }
    
    public List<Token> getTokens(){
        return tokens;
    }
    
    private double getTextWidth(Text txt){
        new Scene(new Group(txt));  //auxiliary scene, computing css needs a real node in real scene
        txt.applyCss();
        double textWidth = txt.getLayoutBounds().getWidth();
        return textWidth;
    }
}
