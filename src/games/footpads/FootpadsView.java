
package games.footpads;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;
import logic.game.collection.GameView;
import logic.game.collection.LogicGameCollection;
import scenecontroller.SceneController;

public class FootpadsView extends GameView{
    private static final int WORLD_WIDTH = LogicGameCollection.WORLD_WIDTH;
    private static final int WORLD_HEIGHT = LogicGameCollection.WORLD_HEIGHT;
    private Footpads game;
    private ResourceBundle texts;
    private ImageView background;
    private Scroll scroll;
    private List<Item> items;
    private EventHandler<MouseEvent> pressed;
    private EventHandler<MouseEvent> released;
    private EventHandler<MouseEvent> dragged;
    private TranslateTransition dropTrans;
    private AreaManager areaManager;
    private Point2D mouseDragPoint;
    private Point2D defaultPos;
    private Polygon left;
    private Polygon right;
    
    public FootpadsView(Footpads game, SceneController sc, ResourceBundle bundle) {
        super(sc, bundle);
        Locale defaultLocale = Locale.getDefault();
        texts = ResourceBundle.getBundle("games.bundles.FootpadsBundle",defaultLocale);
        this.game = game;
        init();
    }
    
    private void init(){
        createItems();
        createAreas();
        scroll = new Scroll(game.getItemDescriptions());
        InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream("games/footpads.png");
        background = new ImageView(new Image(imageStream));
        background.setPreserveRatio(true);
        background.setFitWidth(WORLD_WIDTH);
        dropTrans = new TranslateTransition(Duration.millis(200));
        
        this.setInstructionsText(texts.getString("instructions"));
        this.setDoYouKnowText(texts.getString("doYouKnow"));
        this.addGameGui(background,left,right);
        this.addGameGui(items);
        this.addGameGui(scroll);
        
    }
    
    private void createAreas(){
        areaManager = new AreaManager(items);
        createPolygons();
    }
    
    private void createItems(){
        items = new ArrayList<>();
        List<ItemDescription> descriptions = game.getItemDescriptions();
        for (ItemDescription d : descriptions){
            Item i = new Item(d.getImgName(), d.getImgWidth(), d.getWeight());
            i.setInitPosition(d.getPosition());
            setEventHandler(i);
            items.add(i);
        }
    }
    
    private void createPolygons(){
        left = new Polygon(new double[]{227.0, 490.0, 
                                        145.0, 611.0,
                                        515.0, 615.0,
                                        513.0, 490.0});
        right = new Polygon(new double[]{586.0, 490.0, 
                                         586.0, 615.0,
                                         943.0, 612.0, 
                                         870.0, 487.0});
        left.setOpacity(0.25);
        right.setOpacity(0.25);
        left.setFill(Color.SADDLEBROWN);
        left.setStroke(Color.SADDLEBROWN);
        right.setFill(Color.SADDLEBROWN);
        right.setStroke(Color.SADDLEBROWN);
        right.setOnMouseDragEntered((MouseDragEvent e) -> right.setOpacity(0.35));
        right.setOnMouseDragExited((MouseDragEvent e) -> right.setOpacity(0.25));
        left.setOnMouseDragEntered((MouseDragEvent e) -> left.setOpacity(0.35));
        left.setOnMouseDragExited((MouseDragEvent e) -> left.setOpacity(0.25));
        
    }
    
    private void setEventHandler(Item i){
        i.setOnMousePressed((MouseEvent e)-> {
            i.setMouseTransparent(true);
            mouseDragPoint = i.sceneToLocal(e.getSceneX(), e.getSceneY());
            defaultPos = new Point2D(i.getTranslateX(), i.getTranslateY());
        });
        
        i.setOnMouseDragged((MouseEvent e)-> {
            if (mouseDragPoint != null){
                i.setTranslateX(i.getTranslateX() - mouseDragPoint.getX() + e.getX());
                i.setTranslateY(i.getTranslateY() - mouseDragPoint.getY() + e.getY());
            }
        });
        
        i.setOnDragDetected((MouseEvent e) -> { i.startFullDrag();});
        
        i.setOnMouseReleased((MouseEvent e)-> {
             boolean valid = testPosition(i,e.getSceneX(), e.getSceneY());
             if (valid){
                 testEnd();
             }else {
                 areaManager.moveUp(i);
                 if (defaultPos != null){
                    dropTrans.setNode(i);
                    dropTrans.setToX(i.getDefaultXPos());
                    dropTrans.setToY(i.getDefaultYPos());
                    dropTrans.play();
                    
                 }
             }
             i.setMouseTransparent(false);
        });
    }
    
    private boolean testPosition(Item i,double x, double y){
        Point2D localPoint1 = left.sceneToLocal(x,y);
        if (left.contains(localPoint1)){
            areaManager.moveLeft(i);
            return true;
        }else {
            Point2D localPoint2 = right.sceneToLocal(x,y);
            if (right.contains(localPoint2)){
                areaManager.moveRight(i);
                return true;
            }else return false;
        }
    }
    
    private void testEnd(){
        if (areaManager.isTopEmpty()){
            if (areaManager.getLeftSum() == areaManager.getRightSum()){
                this.setEndGameInfo(true);
                this.showEndGamePane(true);
            }
        }
    }
    
    @Override
    public void start() {
        areaManager.restart();
        for (Item i : items){
            i.setDefaultPosition();
        }
    }
    
}
