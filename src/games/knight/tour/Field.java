
package games.knight.tour;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

public class Field extends Box{
    private PhongMaterial material;
    private boolean isLight;
    private int posX;
    private int posY;
    
    public Field(Color c, boolean isLight, double width, double height, double depth){
        super(width, height, depth);
        material = new PhongMaterial(c);
        this.setMaterial(material);
        this.isLight = isLight;
    }
    
    public Field(double width, double height, double depth){
        super(width, height, depth);
    }
    
    public void setMaterial(Color c, boolean isLight){
        material = new PhongMaterial(c);
        this.setMaterial(material);
        this.isLight = isLight;
    }
    
    public void setMaterial(Color c){
        material = new PhongMaterial(c);
        this.setMaterial(material);
    }
    
    public void setPosX(int j){
        posX = j;
    }
    
    public void setPosY(int i){
        posY = i;
    }

    /**
     * @return the isLight
     */
    public boolean isLight() {
        return isLight;
    }

    /**
     * @return the posX
     */
    public int getPosX() {
        return posX;
    }

    /**
     * @return the posY
     */
    public int getPosY() {
        return posY;
    }
    
}
