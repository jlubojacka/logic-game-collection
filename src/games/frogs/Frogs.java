
package games.frogs;

import deserializer.Deserialize;
import java.util.Locale;
import java.util.ResourceBundle;
import logic.game.collection.Category;
import logic.game.collection.GamePrototype;
import logic.game.collection.GameView;
import scenecontroller.SceneController;

@Deserialize(as = "Frogs")
public class Frogs implements GamePrototype{
    private static final Category CATEGORY = Category.MORE;
    private FrogsView gui;
    private int level = 0;
    private String title;
    private int greensCount;
    private int brownsCount;
    
    public Frogs(){
        Locale defaultLocale = Locale.getDefault();
        ResourceBundle texts = ResourceBundle.getBundle("games.bundles.FrogsBundle",defaultLocale); 
        title = texts.getString("title");
    }
    
    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Deserialize(as = "level")
    @Override
    public void setLevel(int number) {
        this.level = number;
    }

    @Override
    public Category getCategory() {
        return CATEGORY;
    }
    

    @Override
    public GameView createGui(SceneController sceneCntrl, ResourceBundle bundle) {
        gui = new FrogsView(this,sceneCntrl,bundle);
        return gui;
    }

    @Override
    public String getClassName() {
        return Frogs.class.getSimpleName();
    }

    @Override
    public void start() {
        gui.start();
    }

    public int getCount(){
        return greensCount + brownsCount;
    }
    
    /**
     * @return the greensCount
     */
    public int getGreensCount() {
        return greensCount;
    }

    /**
     * @param greensCount the greensCount to set
     */
    @Deserialize(as = "green")
    public void setGreensCount(int greensCount) {
        this.greensCount = greensCount;
    }

    /**
     * @return the brownsCount
     */
    public int getBrownsCount() {
        return brownsCount;
    }

    /**
     * @param brownsCount the brownsCount to set
     */
    @Deserialize(as = "brown")
    public void setBrownsCount(int brownsCount) {
        this.brownsCount = brownsCount;
    }
    
}
