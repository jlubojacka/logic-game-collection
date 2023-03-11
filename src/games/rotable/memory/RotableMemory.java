
package games.rotable.memory;

import deserializer.Deserialize;
import java.util.Locale;
import java.util.ResourceBundle;
import logic.game.collection.Category;
import logic.game.collection.GamePrototype;
import logic.game.collection.GameView;
import scenecontroller.SceneController;

@Deserialize(as = "RotableMemory")
public class RotableMemory implements GamePrototype{

    private static final Category CATEGORY = Category.MORE;
    private int count;
    private int level = 0;
    private String title;
    private RotableMemoryView gui;
    private String imagesName;
    private int startImgIndex;
    private int endImgIndex;
    
    public RotableMemory(){
        Locale defaultLocale = Locale.getDefault();
        ResourceBundle texts = ResourceBundle.getBundle("games.bundles.RotableMemoryBundle",defaultLocale); 
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
    
    public int getCount(){
        return count;
    }
    
    @Deserialize(as = "count")
    public void setCount(int count){
        if ((count % 2) == 0){
            this.count = count;
        }else {  //even number not possible
            this.count = count - 1;
        }
    }
    
    public String getImagesName(){
        return imagesName;
    }
    @Deserialize(as = "imagesName")
    public void setImagesName(String name){
        imagesName = name;
    }
    
    @Deserialize(as = "imagesIndex")
    public void setImageIndex(int from, int to){
        if ((to - from) >= 0){  
            startImgIndex = from;
            endImgIndex = to;
        }
    }
    
    public int getStartImgIndex(){
        return startImgIndex;
    }
    
    public int getEndImgIndex(){
        return endImgIndex;
    }
    
    public int getInnerCount(){
        int half = count/2; //surely even number
        if ((half % 2) == 0){
            return half - 2;
        }else return half - 3;
    }
    
    public int getOuterCount(){
        int half = count/2;
        if ((half % 2) == 0){
            return half + 2;
        }else return half + 3;
    }

    @Override
    public GameView createGui(SceneController sceneCntrl, ResourceBundle bundle) {
        gui = new RotableMemoryView(this,sceneCntrl,bundle);
        return gui;
    }

    @Override
    public String getClassName() {
        return RotableMemory.class.getSimpleName();
    }

    @Override
    public void start() {
        gui.start();
    }

    
    
}
