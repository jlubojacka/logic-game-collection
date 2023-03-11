
package games.wall_snail;

public enum Direction {
    NORTH(-1),
    SOUTH(1),
    WEST(-1),
    EAST(1);
    
    private final int value;
    private Direction(int value){
        this.value = value;
    }
    
    public int value(){
        return this.value;
    }
}
