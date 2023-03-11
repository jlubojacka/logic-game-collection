
package games.stars;

import javafx.geometry.Point2D;
import javafx.scene.effect.Bloom;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/*
* Vertice representation
*/
public class Star extends ImageView{
    private static final int STAR_SIZE = 45;
    private int id;
    private Image image;
    private Point2D center;
    private Bloom bloom;
    
    public Star(Image img, int id, Point2D coordinates){
        this.setImage(img);
        this.setPreserveRatio(true);
        this.setFitWidth(STAR_SIZE);
        this.image = img;
        this.id = id;
        center = coordinates;
        setCenterX(coordinates.getX());
        setCenterY(coordinates.getY());
        bloom = new Bloom();
        bloom.setThreshold(1.0);
        this.setEffect(bloom);
    }
    
    public int getID(){
        return id;
    }
    
    private void setCenterX(double x){
        this.setTranslateX(x - (STAR_SIZE/2.0));
    }
    
    private void setCenterY(double y){
        double proportion = STAR_SIZE/image.getWidth();
        double fitHeight = proportion * image.getHeight();
        this.setTranslateY(y - (fitHeight/2));
    }
    
    public double getCenterX(){
        return center.getX();
    }
    
    public double getCenterY(){
        return center.getY();
    }
    
    public void bloomOn(boolean on){
        if (on){
            bloom.setThreshold(0.8);
        }else {
            bloom.setThreshold(1.0);
        }
    }
}
