
package games.footpads;

import javafx.geometry.Point2D;

public class ItemDescription {
    private int weight;
    private int imgWidth;
    private String imgName;
    private Point2D position;
    
    public ItemDescription(String imgName,int imgWidth, int weight, double posX,
            double posY){
        position = new Point2D(posX, posY);
        this.imgName = imgName;
        if (imgWidth > 0){
            this.imgWidth = imgWidth;
        }else this.imgWidth = 10;
        this.weight  = weight;
    }

    /**
     * @return the weight
     */
    public int getWeight() {
        return weight;
    }

    /**
     * @return the imgWidth
     */
    public int getImgWidth() {
        return imgWidth;
    }

    /**
     * @return the imgPath
     */
    public String getImgName() {
        return imgName;
    }

    /**
     * @return the position
     */
    public Point2D getPosition() {
        return position;
    }
    
    @Override
    public boolean equals(Object o){
        if (o == this)
            return true;
        if (!(o instanceof ItemDescription))
            return false;
        ItemDescription i = (ItemDescription)o;
        return (i.imgName.equals(this.imgName)) 
                && (i.weight == this.weight);
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + imgName.hashCode();
        result = 31 * result + weight;
        return result;
    }
    
}
