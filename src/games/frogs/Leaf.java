
package games.frogs;

import java.io.InputStream;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Leaf extends ImageView{
    
    
    public Leaf(double leafWidth){
        InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream("games/leaf.png");
        this.setImage(new Image(imageStream));
        this.setPreserveRatio(true);
        this.setFitWidth(leafWidth);
    }
    
    public double getCenterX(){
        return this.getTranslateX() + this.getFitWidth()/2.0;
    }
    
    public double getCenterY(){
        return this.getTranslateY() + getImgHeight()/2.0;
    }
    
    
    public void setCenterX(double centerX){
        this.setTranslateX(centerX - (this.getFitWidth()/2.0));
    }
    
    public void setCenterY(double centerY){
        this.setTranslateY(centerY - (getImgHeight()/2.0));
    }
    
    public double getImgHeight(){
        double h = this.getImage().getHeight()/this.getImage().getWidth()*this.getFitWidth();
        return h;
    }
    
}
