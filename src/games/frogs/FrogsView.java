
package games.frogs;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;
import javafx.util.Duration;
import logic.game.collection.GameView;
import logic.game.collection.LogicGameCollection;
import scenecontroller.SceneController;


public class FrogsView extends GameView{
    private static final int WORLD_WIDTH = LogicGameCollection.WORLD_WIDTH;
    private static final int WORLD_HEIGHT = LogicGameCollection.WORLD_HEIGHT;
    private int frogSize;
    private double leafWidth;
    private ResourceBundle texts;
    private List<Frog> greenFrogs;
    private List<Frog> brownFrogs;
    private List<Leaf> leaves;
    private Pane content;
    private Frog[] frogs;
    private Frogs game;
    private ImageView background;
    private Frog selected;
    private EventHandler<MouseEvent> leafHandler;
    private TranslateTransition move;
    private PathTransition jump;
    
    public FrogsView(Frogs game,SceneController sc, ResourceBundle bundle) {
        super(sc, bundle);
        this.game = game;
        Locale defaultLocale = Locale.getDefault();
        texts = ResourceBundle.getBundle("games.bundles.FrogsBundle",defaultLocale);
        leafWidth = (WORLD_WIDTH - 140)/(game.getCount() + 1);
        frogSize = (int)leafWidth - 20;
        init();
    }
    
    private void init(){
        createLeafHandler();
        InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream("games/water.png");
        background = new ImageView(new Image(imageStream));
        background.setPreserveRatio(true);
        background.setFitWidth(WORLD_WIDTH);
        AnchorPane.setTopAnchor(background, 0.0);
        AnchorPane.setLeftAnchor(background, 0.0);
        createContent();
        createTransitions();
        this.addGameGui(background,content);
        this.setInstructionsText(texts.getString("instructions"));
    }
    
    private void createLeafHandler(){
        leafHandler = new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                if (event.getSource() instanceof ImageView){
                    if (selected == null)
                        return;
                    ImageView leaf = (ImageView)event.getSource();
                    int leafIndex = leaves.indexOf(leaf);
                    int frogIndex = Integer.parseInt(selected.getId());
                    
                    if ((frogIndex + selected.getType()) == leafIndex){
                        if (frogs[leafIndex] == null){
                            //move
                            move.setNode(selected);
                            move.setByX(selected.getType() * leafWidth);
                            move.play();
                            int newPos = leafIndex;
                            selected.setId(String.valueOf(newPos));
                            frogs[newPos] = selected;
                            frogs[frogIndex] = null;
                            testEnd();
                        }
                    }else if ((frogIndex + (selected.getType() * 2)) == leafIndex){
                        int newPos = leafIndex;
                        if (frogs[newPos] == null){
                            //jump
                            double fromX = selected.getCenterX();
                            double fromY = selected.getCenterY();
                            double toX = leaves.get(leafIndex).getCenterX();
                            double toY = fromY;
                            selected.toFront();
                            jump.setNode(selected);
                            jump.setPath(createPath(fromX, fromY, toX, toY));
                            jump.play();
                            selected.setId(String.valueOf(newPos));
                            frogs[newPos] = selected;
                            frogs[frogIndex] = null;
                            testEnd();
                        }
                    }
                }
            }
        };
    }
    
    
    private Path createPath(double fromX, double fromY, double toX, double toY){
        Path path = new Path();
        PathElement[] el = {new MoveTo(fromX, fromY),
            new QuadCurveTo(toX - (toX - fromX)/6.0,toY - 100,toX, toY)};
        path.getElements().addAll(el);
        return path;
    }
    
    private void testEnd(){
        boolean ends = true;
        for (int i =0; i < game.getCount(); i++){
            if (i < game.getGreensCount()){
                if ((frogs[i] == null)||(frogs[i].getType() != -1)){
                    ends = false;
                }
            }else {
                if ((frogs[i+1] == null) || (frogs[i+1].getType() != 1)){
                    ends = false;
                }
            }
        }
        if (ends){
            this.setEndGameInfo(texts.getString("winTitle"), texts.getString("winText"));
            this.showEndGamePane(true);
        }
    }
    
    private void createTransitions(){
        move = new TranslateTransition(Duration.millis(300));
        jump = new PathTransition();
        jump.setDuration(Duration.millis(430));
        jump.setInterpolator(Interpolator.SPLINE(0, 0, 0.65, 0.45));
    }
    
    private void createContent(){
        frogs = new Frog[game.getCount() + 1];
        content = new Pane();
        content.setPrefSize(leafWidth * (game.getCount() + 1), leafWidth);
        content.setLayoutX((WORLD_WIDTH/2.0) - (content.getPrefWidth()/2.0));
        content.setLayoutY((WORLD_HEIGHT/2.0) - (content.getPrefHeight()/2.0));
        content.getChildren().addAll(createLeaves());
        createFrogs();
    }
    
    public List<Leaf> createLeaves(){
        leaves = new ArrayList<>();
        int count = game.getCount();
        for (int i=0; i <= count; i++){
            Leaf leaf = new Leaf(leafWidth);
            if (i < game.getGreensCount()){
                leaf.setRotate(180);
            }
            leaves.add(leaf);
            leaf.setCenterX((leafWidth/2.0) + (i*leafWidth));
            leaf.setCenterY(content.getPrefHeight()/2.0);
            leaf.setOnMouseClicked(leafHandler);
        }
        return leaves;
    }
    
    
    public void createFrogs(){
        greenFrogs = new ArrayList<>();
        brownFrogs = new ArrayList<>();
        String path1 = "games/frog1.png";
        String path2 = "games/frog2.png";
        for (int i = 0; i < game.getCount(); i++){
            Frog frog;
            if (i < game.getGreensCount()){
                frog = new Frog(path1,1,frogSize);
                greenFrogs.add(frog);
            }else {
                frog = new Frog(path2,-1,frogSize);
                brownFrogs.add(frog);
            }
            content.getChildren().add(frog);
            frog.setOnMouseClicked(e-> {
                if (selected != null){
                    selected.bloomOn(false);
                }
                selected = frog;
                selected.bloomOn(true);
            });
        }
        reorderFrogs();
    }
    
    private void reorderFrogs(){
        for (int i=0; i<game.getCount();i++){
            if (i < game.getGreensCount()){
                Frog frog = greenFrogs.get(i);
                frog.setId(String.valueOf(i));
                frogs[i] = frog;
                frog.setCenterX((leafWidth/2.0) + (i*leafWidth));
                frog.setCenterY(content.getPrefHeight()/2.0);
            }else {
                Frog frog = brownFrogs.get(i - game.getGreensCount());
                frogs[i+1] = frog;
                frog.setId(String.valueOf(i+1));
                frog.setCenterX((leafWidth/2.0) + ((i+1)*leafWidth));
                frog.setCenterY(content.getPrefHeight()/2.0);
            }
        }
        int middle = frogs.length/2;
        frogs[middle] = null;
    }
    
    
    
    @Override
    public void start(){
        if (selected != null){
            selected.bloomOn(false);
            selected = null;
        }
        reorderFrogs();
    }
    
}
