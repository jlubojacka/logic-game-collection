
package games.blury;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import logic.game.collection.GameView;
import logic.game.collection.LogicGameCollection;
import scenecontroller.SceneController;

public class BluryView extends GameView{

    private Blury game;
    private static final int WORLD_WIDTH = LogicGameCollection.WORLD_WIDTH;
    private static final int WORLD_HEIGHT = LogicGameCollection.WORLD_HEIGHT;
    private int maxHeight;
    private int sliceSize;
    private ResourceBundle texts;
    private Pane gameBoard;
    private double centerX;
    private double centerY;
    private Image bg;
    private int TOP_OFFSET = 35;
    private CircleCruncher cruncher;
    private Group circle;
    private ImageView arrowUp;
    private ImageView arrowDown;
    
    public BluryView(Blury game, SceneController sc, ResourceBundle bundle) {
        super(sc, bundle);
        this.game = game;
        
        maxHeight = (WORLD_HEIGHT - TOP_OFFSET - BOTTOM_PANE_HEIGHT);
        sliceSize = (maxHeight/2);
        centerY = (maxHeight/2.0) + TOP_OFFSET;
        centerX = WORLD_WIDTH/2.0;
        Locale defaultLocale = Locale.getDefault();
        texts = ResourceBundle.getBundle("games.bundles.BluryBundle",defaultLocale);
        init();
    }
    
    private void init(){
        loadGraphics();
        gameBoard = new Pane();
        cruncher = new CircleCruncher(game.getSliceCount(), sliceSize);
        circle = new Group();
        if (bg != null){
            List<Slice> slices = cruncher.getSliced(bg);
            circle.getChildren().addAll(slices);
        }
        circle.setTranslateX(centerX - (maxHeight/2.0));
        circle.setTranslateY(centerY);
        gameBoard.getChildren().add(circle);
        this.addGameGui(gameBoard);
        this.setInstructionsText(texts.getString("instructions"));
    }
    
    private void loadGraphics(){
        String path = "games/" + game.getImageName();
        InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream(path);
        bg = new Image(imageStream);
    }
    
    @Override
    public void start() {
        clearSlices();
        shuffleBlur();
    }
    
    private void shuffleBlur(){
        List<Integer> indexes = new ArrayList<>();
        int count = (game.getSliceCount()/2) + 1;
        Random r = new Random();
        while(indexes.size() < count){
            int index = r.nextInt(game.getSliceCount());
            if (!indexes.contains(index)){
                indexes.add(index);
            }
        }
        
        for (Integer i : indexes){
            Slice s = (Slice) circle.getChildren().get(i);
            s.setBlury(true);
        }
    }
    
    private void clearSlices(){
        for(Node n : circle.getChildren()){
            Slice s = (Slice) n;
            s.setBlury(false);
        }
    }
    
    private void setArrowMouseHandler(){
        arrowUp.setOnMouseClicked((MouseEvent e) -> {
            if (e.getClickCount() == 2){
                int angle = 360/game.getSliceCount();
                int rotation = game.getUpCount() * angle;
                rotate(-rotation);
            }
        });
        arrowDown.setOnMouseClicked((MouseEvent e) -> {
            if (e.getClickCount() == 2){
                int angle = 360/game.getSliceCount();
                int rotation = game.getDownCount() * angle;
                rotate(rotation);
            }
        });
    }
    
    private void rotate(int angle){
        Rotate rotation = new Rotate(0, centerX, centerY);
        circle.getTransforms().add(rotation);
        //rotation.setAxis(Rotate.Y_AXIS);
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(rotation.angleProperty(), 0)),
            new KeyFrame(Duration.seconds(1), new KeyValue(rotation.angleProperty(), angle))
        );
        timeline.setOnFinished(e -> changeSlice());
        timeline.play();
    }
    
    private void changeSlice(){
        //filter slice with right coordinates
        testEnd();
    }
    
    private void testEnd(){
        boolean end = true;
        for (Node n : circle.getChildren()){
            Slice s = (Slice)n;
            if (!s.isSharp()){
                end = false;
                break;
            }
        }
        if (end){
            this.setEndGameInfo(true);
            this.showEndGamePane(true);
        }
    }
    
}
