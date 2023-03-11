
package games.magic.triangle;

import deserializer.Deserialize;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import logic.game.collection.Category;
import logic.game.collection.GamePrototype;
import logic.game.collection.GameView;
import logic.game.collection.LogicGameCollection;
import scenecontroller.SceneController;

@Deserialize(as = "MagicTriangle")
public class MagicTriangle implements GamePrototype{
    private static final Category CATEGORY = Category.MATH;
    private int level = 0;
    private String title;
    private MagicTriangleView gui;
    private List<List<Integer>> rows;
    private List<Pair<Integer, Integer>> numbers;
    
    public MagicTriangle(){
        Locale defaultLocale = Locale.getDefault();
        ResourceBundle texts = ResourceBundle.getBundle("games.bundles.MagicTriangleBundle",defaultLocale); 
        title = texts.getString("title");
        rows = new ArrayList<>();
        numbers = new ArrayList<>();
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
    public void setLevel(int level) {
        this.level = level;
    }

    @Deserialize(as = "row")
    public void addRow(String numbers){
        List<Integer> row = new ArrayList<>();
        String []parts = numbers.split("(\\s*),+(\\s*)");
        for (String p : parts){
            try {
                int number = Integer.parseInt(p);
                row.add(number);
            }catch(NumberFormatException e){
                Logger.getLogger(LogicGameCollection.class.getName())
                        .log(Level.WARNING, "Wrong number format in row definition.", e);
            }
        }
        
        if (!rows.isEmpty()){
            int lastRowSize = rows.get(rows.size() - 1).size();
            int thisRowSize = row.size();
            if ((lastRowSize + 2) == thisRowSize){ //number of triangles should grow
                rows.add(row);
            }
        }else rows.add(row);
        
    }
    
    @Deserialize(as = "number")
    public void addNumber(int number, int count){
        if ((number > 0) && (count > 0)){
            Pair<Integer, Integer> definition = new Pair<>(number, count);
            numbers.add(definition);
        }
    }
    
    public List<List<Integer>> getRows(){
        return rows;
    }
    
    public List<Pair<Integer, Integer>> getNumbers(){
        return numbers;
    }
    
    /*
    * Number of tokens on vertices of triangles according to row count
    */
    public int getRequestedNumCount(){
        int n = getRowsCount() + 1;
        int sum = n*(1 + n)/2;
        return sum;
    }
    
    public int getRowsCount(){
        return rows.size();
    }
    
    @Override
    public Category getCategory() {
        return CATEGORY;
    }

    @Override
    public GameView createGui(SceneController sceneCntrl, ResourceBundle bundle) {
        gui = new MagicTriangleView(this, sceneCntrl, bundle);
        return gui;
    }

    @Override
    public String getClassName() {
        return MagicTriangle.class.getSimpleName();
    }

    @Override
    public void start() {
        gui.start();
    }
    
}
