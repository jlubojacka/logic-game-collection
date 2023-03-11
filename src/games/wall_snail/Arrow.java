
package games.wall_snail;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Arrow extends Polygon{
    
    private Direction direction;
    private static double[] points = new double[]{20.0,0.0, 0.0,20.0, 20.0,20.0, 40.0,20.0};
    private Color color;
    
    public Arrow(Direction d){
        super(points);
        switch(d.name()){
            case "WEST": this.setRotate(-90); break;
            case "EAST": this.setRotate(90); break;
            case "SOUTH": this.setRotate(180); break;
        }
        direction = d;
        
    }
    
    public Direction getDirection(){
        return direction;
    }
    
}
