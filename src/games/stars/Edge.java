
package games.stars;

import javafx.scene.effect.Bloom;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

public class Edge extends Path{
    private Star first;
    private Star second;
    private Bloom bloom;
    private boolean visited;
    
    public Edge(Star first, Star second){
        this.first = first;
        this.second = second;
        visited = false;
        createPath();
        bloom = new Bloom();
        bloom.setThreshold(1.0);
        this.setEffect(bloom);
    }

    private void createPath(){
        PathElement[] elements = {
            new MoveTo(first.getCenterX(), first.getCenterY()),
            new LineTo(second.getCenterX(), second.getCenterY()),
            new ClosePath()
        };
        this.setStroke(Color.LIGHTGRAY);
        this.getElements().addAll(elements);
    }
    
    public Star getFirstStar() {
        return first;
    }

    public Star getSecondStar() {
        return second;
    }
    
    public boolean equals(Star star1, Star star2){
        if (((star1.getID() == first.getID()) && (star2.getID() == second.getID())) ||
                ((star1.getID() == second.getID()) && (star2.getID() == first.getID()))){
                return true;
        }else return false;
    }
    
    public boolean matches(int firstID, int secondID){
        if (((firstID == first.getID()) && (secondID == second.getID())) ||
                ((firstID == second.getID()) && (secondID == first.getID()))){
                return true;
        }else return false;
    }
    
    public boolean isVisited(){
        return visited;
    }
    
    public void setVisited(boolean visited){
        this.visited = visited;
        highlight(visited);
        bloomOn(visited);
    }
    
    private void bloomOn(boolean on){
        if (on){
            bloom.setThreshold(0.6);
        }else {
            bloom.setThreshold(1.0);
        }
    }
    
    private void highlight(boolean on){
        if (on){
            this.setStroke(Color.KHAKI);
            this.setStrokeWidth(3);   
        }else{
            this.setStroke(Color.LIGHTGRAY);
            this.setStrokeWidth(1.0);
            this.setStrokeDashOffset(20);
        }
        
    }
    
}
