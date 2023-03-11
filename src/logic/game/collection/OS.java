
package logic.game.collection;


public enum OS {
    WINDOWS(31,8,8,8),
    UNIVERSAL(0,0,0,0);
    
    private final int top;
    private final int left;
    private final int right;
    private final int bottom;
    
    private OS(int top, int left, int right, int bottom){
        this.top = top;
        this.left = left;
        this.right = right;
        this.bottom = bottom;
    }

    /**
     * @return the top
     */
    public int getTop() {
        return top;
    }

    /**
     * @return the left
     */
    public int getLeft() {
        return left;
    }

    /**
     * @return the right
     */
    public int getRight() {
        return right;
    }

    /**
     * @return the bottom
     */
    public int getBottom() {
        return bottom;
    }
    
}
