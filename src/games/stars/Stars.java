
package games.stars;

import deserializer.Deserialize;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point2D;
import logic.game.collection.Category;
import logic.game.collection.GamePrototype;
import logic.game.collection.GameView;
import logic.game.collection.LogicGameCollection;
import scenecontroller.SceneController;

@Deserialize(as = "Stars")
public class Stars implements GamePrototype{
    private StarsView gui;
    private static final Category CATEGORY = Category.IT;
    private String title;
    private int level = 0;
    private LinkedList<Integer>[] vertices;
    private int verticeCount;
    private Map<Integer, Point2D> verticePositions;
    
    public Stars(){
        Locale defaultLocale = Locale.getDefault();
        ResourceBundle texts = ResourceBundle.getBundle("games.bundles.StarsBundle",defaultLocale); 
        title = texts.getString("title");
        verticePositions = new HashMap<Integer, Point2D>();
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
        gui = new StarsView(this,sceneCntrl, bundle);
        return gui;
    }

    
    @Override
    public String getClassName() {
        return Stars.class.getSimpleName();
    }

    @Override
    public void start() {
        gui.start();
    }
    
    @Deserialize(as = "verticeCount")
    public void setVerticeCount(int count){
        this.verticeCount = count;
        vertices = new LinkedList[count];
        for (int i = 0; i < count; i++){
            vertices[i] = new LinkedList<Integer>();
        }
    }
    
    @Deserialize(as = "vertice")
    public void addVertice(int id, int posX, int posY){
        if ((id >= 0) && (id < verticeCount)){
            verticePositions.put(id, new Point2D(posX,posY));
        }
    }
    
    @Deserialize(as = "edges")
    public void setEdges(String edges){
        String []input = edges.trim().split("(\\s*)-(\\s*)");
        if (input.length == 2){
            try{
                int firstVertice = Integer.parseInt(input[0]);
                if ((firstVertice >= 0) && (firstVertice < verticeCount)){
                    List<Integer> adjacent = parseVertices(input[1]);
                    for (Integer v: adjacent){
                        addEdge(firstVertice,v);
                    }
                }
            }catch(NumberFormatException e){
                Logger.getLogger(LogicGameCollection.class.getName()).log(Level.SEVERE,
                        "Wrong number format.", e.getMessage());
            }  
        }
    }
    
    public List<Integer> parseVertices(String input) throws NumberFormatException{
        List<Integer> vert = new ArrayList<>();
        String []parts = input.split("(\\s*),+(\\s*)");
        for (String p : parts){
            int v = Integer.parseInt(p);
            if ((v >= 0) && (v < verticeCount)){
                vert.add(v);
            }
        }
        return vert;
    }
    
    public void addEdge(int vert1, int vert2){
        vertices[vert1].add(vert2);
        vertices[vert2].add(vert1);
    }
    
    public int getVerticeCount(){
        return verticeCount;
    }
    
    public Map<Integer,Point2D> getVertices(){
        return verticePositions;
    }
    
    public LinkedList<Integer>[] getAdjacencyList(){
        return vertices;
    }
}
