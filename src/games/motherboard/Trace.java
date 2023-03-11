
package games.motherboard;

import java.util.List;
import java.util.stream.Collectors;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.shape.Line;


public class Trace extends Group{
    private Token t1;
    private Token t2;
    private Point2D startPos;
    private boolean closed = false;
    
    public Trace(Token t1, Token t2){
        this.t1 = t1;
        this.t2 = t2;
    }
    
    public void setStart(double posX, double posY){
        startPos = new Point2D(posX, posY);
    }
    
    public void addLine(double endX, double endY){
        if (this.getChildren().isEmpty()){
            if (startPos != null){
                Line l = new Line();
                l.setStrokeWidth(5.0);
                l.setStroke(t1.getFill());
                l.setStartX(startPos.getX());
                l.setStartY(startPos.getY());
                l.setEndX(endX);
                l.setEndY(endY);
                this.getChildren().add(l);
            }
        }else {
            Line l = (Line)this.getChildren().get(this.getChildren().size() - 1);
            l.setEndX(endX);
            l.setEndY(endY);
            Line newLine = new Line();
            newLine.setStrokeWidth(5.0);
            newLine.setStroke(t1.getFill());
            newLine.setStartX(endX);
            newLine.setStartY(endY);
            newLine.setEndX(endX);
            newLine.setEndY(endY);
            this.getChildren().add(newLine);
        }
    }
    
    public void moveLine(double endX, double endY){
        if (!this.getChildren().isEmpty()){
            Line l = (Line) this.getChildren().get(this.getChildren().size() - 1);
            l.setEndX(endX);
            l.setEndY(endY);
        }
    }
    
    public void removeAll(){
        this.getChildren().clear();
    }
    
    public void close(boolean close){
        closed = close;
    }
    
    public boolean isClosed(){
        return closed;
    }
    
    public boolean consistsOf(Token t){
        if ((t.equals(this.t1)) || (t.equals(this.t2))){
            return true;
        }else return false;
    }
    
    public Line getLastLine(){
        return (Line)this.getChildren().get(this.getChildren().size() - 1);
    }
    
    public List<Line> getLines(){
        return this.getChildren()
                .stream()
                .map(node -> Line.class.cast(node))
                .collect(Collectors.toList());
    }
}
