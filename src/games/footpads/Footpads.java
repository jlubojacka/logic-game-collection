
package games.footpads;

import deserializer.Deserialize;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import logic.game.collection.Category;
import logic.game.collection.GamePrototype;
import logic.game.collection.GameView;
import scenecontroller.SceneController;

@Deserialize(as = "Footpads")
public class Footpads implements GamePrototype{
    private FootpadsView gui;
    private static final Category CATEGORY = Category.IT;
    private String title;
    private int level = 0;
    private List<ItemDescription> items;
    
    public Footpads(){
        Locale defaultLocale = Locale.getDefault();
        ResourceBundle texts = ResourceBundle.getBundle("games.bundles.FootpadsBundle",defaultLocale); 
        title = texts.getString("title");
        items = new ArrayList<>();
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
        gui = new FootpadsView(this,sceneCntrl, bundle);
        return gui;
    }

    @Override
    public String getClassName() {
        return Footpads.class.getSimpleName();
    }

    @Override
    public void start() {
        gui.start();
    }
    
    @Deserialize(as = "item")
    public void addItem(String imgPath, int width, int weight, double posX, double posY){
        ItemDescription item = new ItemDescription(imgPath, width, weight, posX, posY);
        items.add(item);
    }
    
    public List<ItemDescription> getItemDescriptions(){
        return items;
    }
    
}
