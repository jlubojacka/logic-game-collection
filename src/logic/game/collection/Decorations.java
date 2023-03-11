
package logic.game.collection;

/*
 * Window decoration widths for user's platform
*/
public class Decorations {
    private OS platform;
    
    public Decorations(){
        String os = System.getProperty("os.name");
        
        if (os.startsWith("Windows")){
            platform = OS.WINDOWS;
        }else {
            platform = OS.UNIVERSAL;
        }
        
    }

    /**
     * @return the top
     */
    public int getTop() {
        return platform.getTop();
    }

    /**
     * @return the left
     */
    public int getLeft() {
        return platform.getLeft();
    }

    /**
     * @return the right
     */
    public int getRight() {
        return platform.getRight();
    }

    /**
     * @return the bottom
     */
    public int getBottom() {
        return platform.getBottom();
    }
    
}
