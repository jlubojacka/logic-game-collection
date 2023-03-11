
package games.rotable.memory;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class Token extends Group{
    private String parentCircle = "";
    private String tokenId = "";
    private Rotate rotation;
    private Timeline timeline1;
    private Timeline timeline2;
    private boolean flipped = false;
    
    public Token(double radius, int depth, Image img){
        PhongMaterial mat = new PhongMaterial(Color.DARKSLATEBLUE);
        PhongMaterial mat2 = new PhongMaterial(Color.ANTIQUEWHITE);
        mat2.setDiffuseMap(img);
        
        Cylinder face =new Cylinder(radius,2);
        face.setMaterial(mat2);
        face.setTranslateY(6);
        Cylinder back = new Cylinder(radius,depth);
        back.setMaterial(mat);
        this.getChildren().addAll(face,back);
        
        rotation = new Rotate(0);
        Bounds b = this.getBoundsInParent();
        double centerX = b.getMinX() + b.getWidth()/2;
        double centerY = b.getMinY() + b.getHeight()/2 ;
        rotation.setAxis(Rotate.Z_AXIS);
        rotation.setPivotX(centerX);
        rotation.setPivotY(centerY);
        this.getTransforms().add(rotation);
        timeline1 = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(rotation.angleProperty(), 0)),
                new KeyFrame(Duration.seconds(1), new KeyValue(rotation.angleProperty(), 180))
        );
        
        timeline2 = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(rotation.angleProperty(), 180)),
                new KeyFrame(Duration.seconds(1), new KeyValue(rotation.angleProperty(), 0))
        );
        flipped = false;
    }
    
    public void playRotate(){
        timeline1.play();
        
    }
    
    public void setFlipped(boolean flipped){
        this.flipped = flipped;
    }
    
    public boolean isFlipped(){
        return flipped;
    }
    
    public void resetRotate(){
        rotation.setAngle(0);
    }
    
    public Timeline getFlipUp(){
        return timeline1;
    }
    
    public Timeline getFlipDown(){
        return timeline2;
    }
    
    /**
     * @return the parentCircle - two possibilities: INNER or OUTER circle
     */
    public String getParentCircle() {
        return parentCircle;
    }

    /**
     * @param parentCircle the parentCircle to set
     */
    public void setParentCircle(String parentCircle) {
        this.parentCircle = parentCircle;
    }

    /**
     * @return the id
     */
    public String getTokenId() {
        return tokenId;
    }

    /**
     * @param id the id to set
     */
    public void setTokenId(String id) {
        this.tokenId = id;
    }
}
