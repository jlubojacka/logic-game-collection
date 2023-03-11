
package games.blury;

import deserializer.Deserialize;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import logic.game.collection.Category;
import logic.game.collection.GamePrototype;
import logic.game.collection.GameView;
import logic.game.collection.LogicGameCollection;
import scenecontroller.SceneController;

@Deserialize(as = "Blury")
public class Blury implements GamePrototype{
    private BluryView gui;
    private static final Category CATEGORY = Category.MORE;
    private String title;
    private int sliceCount = 0;
    private int upCount = 0;
    private int downCount = 0;
    private int level = 0;
    private String imageName;
    private static final Logger LOGGER = Logger.getLogger(LogicGameCollection.class.getName());
    
    public Blury(){
        
        Locale defaultLocale = Locale.getDefault();
        ResourceBundle texts = ResourceBundle.getBundle("games.bundles.BluryBundle",defaultLocale); 
        title = texts.getString("title");
    }
    
    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    @Deserialize(as = "level")
    public void setLevel(int level) {
         this.level = level;
    }
    
    @Deserialize(as = "slices")
    public void setSliceCount(int slices) {
        if (slices >= 0){
            this.sliceCount = slices;
        }else {
            this.sliceCount = Math.abs(slices);
            LOGGER.log(Level.WARNING, "Invalid argument for method setSliceCount: {0}. Value cannot be negative."
                    + "An absolute value was set.", slices);
        }
    }
    
    @Deserialize(as = "up")
    public void setUpCount(int up) {
        if (up >= 0){
            this.upCount = up;
        }else {
            this.upCount = Math.abs(up);
            LOGGER.log(Level.WARNING, "Invalid argument for method setUpCount: {0}. Value cannot be negative. "
                    + "An absolute value was set.", up);
        }
         
    }
    
    @Deserialize(as = "down")
    public void setDownCount(int down) {
        if (down >= 0){
            this.downCount = down;
        }else {
            this.downCount = Math.abs(down);
            LOGGER.log(Level.WARNING, "Invalid argument for method setDownCount: {0}. Value cannot be negative. "
                    + "An absolute value was set.", down);
        }
    }
    
    @Deserialize(as = "imageName")
    public void setImageName(String name){
        this.imageName = name;
    }

    @Override
    public Category getCategory() {
        return this.CATEGORY;
    }

    @Override
    public GameView createGui(SceneController sceneCntrl, ResourceBundle bundle) {
        gui = new BluryView(this, sceneCntrl, bundle);
        return gui;
    }

    @Override
    public String getClassName() {
        return Blury.class.getSimpleName();
    }

    @Override
    public void start() {
        gui.start();
    }

    /**
     * @return the sliceCount
     */
    public int getSliceCount() {
        return sliceCount;
    }

    /**
     * @return the upCount
     */
    public int getUpCount() {
        return upCount;
    }

    /**
     * @return the downCount
     */
    public int getDownCount() {
        return downCount;
    }

    /**
     * @return the imageName
     */
    public String getImageName() {
        return imageName;
    }
    
    
}
