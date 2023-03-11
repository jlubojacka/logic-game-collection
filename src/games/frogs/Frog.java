
package games.frogs;

import java.io.InputStream;
import javafx.scene.effect.Bloom;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Frog extends ImageView{
    private int type; //specifies direction and color of frog :
                        //green pointing to right == 1,
                        //brown pointing to left == -1
    private Bloom bloom;
    
    public Frog(String path, int type, double size){
        InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream(path);
        this.setImage(new Image(imageStream));
        this.setPreserveRatio(true);
        this.setFitWidth(size);
        this.type = type;
        bloom = new Bloom();
        bloom.setThreshold(1.0);
        this.setEffect(bloom);
    }
    
    public int getType(){
        return type;
    }
    
    public void bloomOn(boolean on){
        if (on){
            if (type == 1){
                bloom.setThreshold(0.7);
            }else {
                bloom.setThreshold(0.5);
            }
        }else {
            bloom.setThreshold(1.0);
        }
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
