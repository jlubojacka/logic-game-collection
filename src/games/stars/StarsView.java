
package games.stars;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import logic.game.collection.GameView;
import logic.game.collection.LogicGameCollection;
import scenecontroller.SceneController;


public class StarsView extends GameView{
    private static final int WORLD_WIDTH = LogicGameCollection.WORLD_WIDTH;
    private static final int WORLD_HEIGHT = LogicGameCollection.WORLD_HEIGHT;
    private Stars game;
    private ResourceBundle texts;
    private Image[] starGraphics;
    private List<Star> stars;
    private List<Edge> edges;
    private ImageView sky;
    private Star selectedStar;
    private LinkedList<Integer>[] adjacencyLists;
    
    public StarsView(Stars game,SceneController sc, ResourceBundle bundle) {
        super(sc, bundle);
        this.game = game;
        Locale defaultLocale = Locale.getDefault();
        texts = ResourceBundle.getBundle("games.bundles.StarsBundle",defaultLocale);
        adjacencyLists = game.getAdjacencyList();
        init();
    }
    
    private void init(){
        loadGraphics();
        createVertices();
        createEdges();
        addMouseHandler();
        
        this.addGameGui(sky);
        this.addGameGui(edges);
        this.addGameGui(stars);
        this.setInstructionsText(texts.getString("instructions"));
        this.setDoYouKnowText(texts.getString("doYouKnow"));
    }
    
    private void addMouseHandler(){
        for (Star s : stars){
            s.setOnMouseEntered((MouseEvent e)-> {
                if (s != selectedStar){
                    s.bloomOn(true);
                }
            });
            s.setOnMouseExited((MouseEvent e)-> {
                if (s != selectedStar){
                    s.bloomOn(false);
                }
            });
            s.setOnMouseClicked(mouseClicked(s));
        }
    }
    
    private EventHandler<MouseEvent> mouseClicked(Star s){
        return (MouseEvent e) -> {
            if (e.getClickCount() == 1){
                s.bloomOn(true);
                if (selectedStar == null){
                    selectedStar = s;
                }else {
                    if (areAdjacent(s,selectedStar)){
                        Edge edge = findEdge(s,selectedStar);
                        if ((edge != null) && (!edge.isVisited())){
                            edge.setVisited(true);
                            selectedStar.bloomOn(false);
                            selectedStar = s;
                            testEnd();
                        }
                    }
                }
            }
        };
    }
    
    private boolean areAdjacent(Star s1, Star s2){
        LinkedList<Integer> list = adjacencyLists[s1.getID()];
        return list.contains(s2.getID());
    }
    
    private Edge findEdge(Star s1, Star s2){
        for (Edge e : edges){
            if (e.equals(s1, s2)){
                return e;
            }
        }
        return null;
    }
    
    private void createEdges(){
        edges = new ArrayList<>();
        Map<Integer,Point2D> vertices = game.getVertices();
        vertices.forEach((key,value) -> {
            Star first = stars.stream()
                    .filter(star -> key.equals(star.getID()))
                    .findAny()
                    .orElse(null);
            if (first != null){
                LinkedList<Integer> list = adjacencyLists[key];
                for (Integer id : list){
                    if (!edgeExists(first.getID(),id)){
                        Star second = stars.stream()
                                .filter(star -> id.equals(star.getID()))
                                .findAny()
                                .orElse(null);
                        if (second != null){
                            Edge e = new Edge(first,second);
                            edges.add(e);
                        }
                    }
                }
            }
        });
        
    }
    
    private boolean edgeExists(int firstID, int secondID){
        if (edges.isEmpty()){
            return false;
        }else {
            for (Edge e : edges){
                if (e.matches(firstID, secondID)){
                    return true;
                }
            }
            return false;
        }
    }
    
    private void createVertices(){
        Random randomG = new Random();
        Map<Integer,Point2D> vertices = game.getVertices();
        stars = new ArrayList<>();
        vertices.forEach((key,value) -> {
            int imgIndex = randomG.nextInt(4);
            Star s = new Star(starGraphics[imgIndex],key, value);
            stars.add(s);
        });
    }
    
    private void loadGraphics(){
        InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream("games/starBackground.png");
        sky = new ImageView(new Image(imageStream));
        
        starGraphics = new Image[4];
        String path;
        for (int i = 1; i <= 4; i++){
            path = "games/star" + i + ".png";
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream(path);
            Image img = new Image(stream);
            starGraphics[i-1] = img;
        }
    }
    
    private void testEnd(){
        if ( edges.stream().allMatch(e -> e.isVisited())){
            this.setEndGameInfo(true);
            this.showEndGamePane(true);
        }
    }
    
    @Override
    public void start(){
        if (selectedStar != null)
            selectedStar.bloomOn(false);
        selectedStar = null;
        for (Edge e : edges){
            e.setVisited(false);
        }
    }

}
