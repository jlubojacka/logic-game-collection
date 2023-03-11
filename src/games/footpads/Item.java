
package games.footpads;

import java.io.InputStream;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Item extends ImageView{
    private int weight;
    private Point2D initPosition;
    private String imgName;
    
    public Item(String imgName, int imgWidth, int weight){
        String path = "games/" + imgName;
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(path);
        if (stream != null) {
            Image img = new Image(stream);
            this.setImage(img);
        }
        this.setPreserveRatio(true);
        this.setFitWidth(imgWidth);
        this.weight = weight;
        initPosition = new Point2D(0,0);
        this.imgName = imgName;
        
    }
    
    public int getWeight(){
        return weight;
    }
    
    public void setInitPosition(Point2D position){
        this.initPosition = position;
    }
    
    public void setDefaultPosition(){
        this.setTranslateX(initPosition.getX());
        this.setTranslateY(initPosition.getY());
    }
    
    public double getDefaultXPos(){
        return initPosition.getX();
    }
    
    public double getDefaultYPos(){
        return initPosition.getY();
    }
    
    @Override
    public boolean equals(Object o){
        if (o == this)
            return true;
        if (!(o instanceof Item))
            return false;
        Item i = (Item)o;
        return (i.imgName.equals(this.imgName)) 
                && (i.weight == this.weight)
                && (i.initPosition.equals(this.initPosition));
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + imgName.hashCode();
        result = 31 * result + weight;
        result = 31 * result + initPosition.hashCode();
        return result;
    }
    
}
