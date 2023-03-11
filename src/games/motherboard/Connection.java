
package games.motherboard;

import javafx.geometry.Point2D;


public class Connection {
    private Point2D first;
    private Point2D second;
    
    public Connection(int fRow, int fCol, int sRow, int sCol){
        first = new Point2D(fCol, fRow);
        second = new Point2D(sCol, sRow);
    }

    /**
     * @return the first
     */
    public Point2D getFirst() {
        return first;
    }

    /**
     * @return the second
     */
    public Point2D getSecond() {
        return second;
    }
}
