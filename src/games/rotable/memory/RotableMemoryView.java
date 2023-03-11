
package games.rotable.memory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import logic.game.collection.GameView;
import logic.game.collection.LogicGameCollection;
import scenecontroller.SceneController;


public class RotableMemoryView extends GameView{
    private static final int WORLD_WIDTH = LogicGameCollection.WORLD_WIDTH;
    private static final int WORLD_HEIGHT = LogicGameCollection.WORLD_HEIGHT;
    private static final int INNER_GAP = 5;
    private static final int OUTER_GAP = 10;
    private static final int ROTATION_ANGLE = 90;
    private double radius;
    private RotableMemory game;
    private List<Image> images;
    private Group root;
    private ResourceBundle texts;
    private SubScene world3d;
    private Group camera;
    private Group innerCircle;
    private Group outerCircle;
    private List<Token> tokens;
    private Token selected;
    private int flipped;
    private volatile boolean rotationEnabled = true;
    
    public RotableMemoryView(RotableMemory game,SceneController sc, ResourceBundle bundle) {
        super(sc, bundle);
        this.game = game;
        radius = 35 + (10 /(game.getCount()/32.0));
        init();
    }
    
    
    @Override
    public void start(){
        innerCircle.getChildren().clear();
        outerCircle.getChildren().clear();
        flipped = 0;
        selected = null;
        shuffle();
        flipTokens();
        placeTokens();
        
    }
    
    private void init(){
        Locale defaultLocale = Locale.getDefault();
        texts = ResourceBundle.getBundle("games.bundles.RotableMemoryBundle",defaultLocale);
        tokens = new ArrayList<>();
        create3D(); 
        this.addGameGui(world3d);
        this.setInstructionsText(texts.getString("instructions"));
        
        for (Token t : tokens){
            t.getTransforms().add(new Rotate(180));
            addListener(t.getFlipUp());
            addListener(t.getFlipDown());
        }
    }
    
    private void addListener(Timeline t){
        t.statusProperty().addListener((ObservableValue<? extends Status> observable, Status oldValue, Status newValue) -> {
            if (newValue.equals(Status.RUNNING)){
                rotationEnabled = false;
            }else if (newValue.equals(Status.STOPPED)){
                rotationEnabled = true;
            }
        });
        
    }
    
    private void create3D(){
        loadImages();
        createTokens();
        innerCircle = new Group();
        outerCircle = new Group();
        root = new Group(innerCircle,outerCircle);
        
        camera = new Group();
        PerspectiveCamera cam = new PerspectiveCamera(true);
        camera.getChildren().add(cam);
        cam.setNearClip(10);  //important to set properly (to avoid artefacts)
        cam.setFarClip(1900.0);
        camera.setTranslateY(1400);
        camera.setRotationAxis(Rotate.X_AXIS);
        camera.setRotate(90);
        world3d = new SubScene(root, WORLD_WIDTH, WORLD_HEIGHT, true, SceneAntialiasing.BALANCED); //depthBuffer = true
        world3d.setCamera(cam);
        
    }
    
    private void createTokens(){
        int half = game.getCount()/2;
        for (int i = 0; i < half ; i++){
            Image img = null;
            if (i < images.size()){
                img = images.get(i);
            }
            tokens.add(createToken(radius,10,img,i));
            tokens.add(createToken(radius,10,img,i));
        }
        
    }
    
    private Token createToken(double radius,int depth,Image img,int id){
        Token t = new Token(radius,depth,img);
        t.setTokenId(String.valueOf(id));
        t.setOnMouseClicked(mouseInteraction(t));
        return t;
    }
    
    private EventHandler<MouseEvent> mouseInteraction(Token t){
        EventHandler<MouseEvent> mouseHandler = (MouseEvent e)-> {
            
            if (t == selected){
                return;
            }
            if (!rotationEnabled){
                return;
            }
            Timeline tim = t.getFlipUp();
            if (selected == null){
                selected = t;
                tim.setOnFinished(ev->t.setFlipped(true));
                tim.play();
            }else{
                t.setFlipped(true);
                tim.setOnFinished(ev -> testMatch(selected,t));
                tim.play();
            }
        };
        
        return mouseHandler;
    }
    
    private void flipTokens(){
        for (Token t : tokens){
            if (t.isFlipped()){
                t.resetRotate();
                t.setFlipped(false);
            }
            t.setOnMouseClicked(mouseInteraction(t));
        }
    }
    
    private void testMatch(Token t1, Token t2){
        if (t1.getTokenId().equals(t2.getTokenId())){
            //match
            t1.setOnMouseClicked(null);
            t2.setOnMouseClicked(null);
            t1.setFlipped(true);
            t2.setFlipped(true);
            flipped = flipped + 2;
            if (flipped == (game.getCount())){
                this.setEndGameInfo(texts.getString("winTitle"), texts.getString("winText"));
                this.showEndGamePane(true);
            }
            selected = null;
        }else {
            ParallelTransition p = new ParallelTransition();
            Timeline tim1 = t1.getFlipDown();
            Timeline tim2 = t2.getFlipDown();
            tim1.setOnFinished(ev->t1.setFlipped(false));
            tim2.setOnFinished(ev->t2.setFlipped(false));
            p.getChildren().addAll(tim1,tim2);
            p.setOnFinished(e -> {
                rotationEnabled = true;
                p.getChildren().clear();
                rotateCircles(t1, t2);
            });
            p.setDelay(Duration.millis(700));
            rotationEnabled = false;
            p.play();
            
        }
    }
    
    private void rotateCircles(Token t1, Token t2){
        String parentCircle1 = t1.getParentCircle();
        String parentCircle2 = t2.getParentCircle();
        if (parentCircle1.equals(parentCircle2)){
            //rotate one
            Timeline t = rotateCircleT(parentCircle1, 2 * ROTATION_ANGLE);
            t.play();
            
        }else {
            //rotate both
            ParallelTransition pTrans = new ParallelTransition();
            Timeline tim1 = rotateCircleT(parentCircle1, ROTATION_ANGLE);
            Timeline tim2 = rotateCircleT(parentCircle2, ROTATION_ANGLE);
            pTrans.getChildren().addAll(tim1, tim2);
            pTrans.setOnFinished(e -> {
                pTrans.getChildren().clear();
                //selected = null;
            });
            pTrans.play();
            
        } 
    }
    
    private Timeline rotateCircleT(String circleId, int angle){
        if (circleId.equals("INNER")){
            Rotate rotation = new Rotate(0,0,0);
            rotation.setAxis(Rotate.Y_AXIS);
            innerCircle.getTransforms().add(rotation);
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(rotation.angleProperty(), 0)),
                    new KeyFrame(Duration.seconds(1), new KeyValue(rotation.angleProperty(), -angle))
            );
            timeline.setOnFinished(e -> selected = null);
            return timeline;
        }else {
            Rotate rotation = new Rotate(0,0,0);
            rotation.setAxis(Rotate.Y_AXIS);
            outerCircle.getTransforms().add(rotation);
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(rotation.angleProperty(), 0)),
                    new KeyFrame(Duration.seconds(1), new KeyValue(rotation.angleProperty(), angle))
            );
            timeline.setOnFinished(e -> selected = null);
            return timeline;
        }
    }
    
    private void placeTokens(){
        int innerCount = game.getInnerCount();
        double a360 = 2 * Math.PI;
        double anglePhi = a360 / innerCount;
        double u = Math.sin(anglePhi/2);
        double dist = (2*radius/(2*u)) + INNER_GAP;
        
        for (int i=0; i < innerCount; i++){
            double angle = 2*i*Math.PI/innerCount;
            double xOffset = dist * Math.cos(angle);
            double yOffset = dist * Math.sin(angle);
            Token t = tokens.get(i);
            t.setParentCircle("INNER");
            t.setTranslateX(xOffset);
            t.setTranslateZ(yOffset);
            innerCircle.getChildren().add(t);
        }
        int outerCount = game.getOuterCount();
        double anglePhi2 = a360 / outerCount;
        double u2 = Math.sin(anglePhi2/2);
        double dist2 = (2*radius/(2*u)) + (2*radius) + INNER_GAP + OUTER_GAP;
        
        for (int i=innerCount; i < (innerCount + outerCount); i++){
            int j = i - innerCount;
            double angle = 2*j*Math.PI/outerCount;
            double xOffset = dist2 * Math.cos(angle);
            double yOffset = dist2 * Math.sin(angle);
            Token t = tokens.get(i);
            t.setParentCircle("OUTER");
            t.setTranslateX(xOffset);
            t.setTranslateZ(yOffset);
            outerCircle.getChildren().add(t);
        }
    }
    
    private void shuffle(){
        Random randomGenerator = new Random();
        int r;
        Token temp;
        for (int i = tokens.size() - 1; i > 0; i--){
            r = randomGenerator.nextInt(i + 1);
            temp = tokens.get(r);
            tokens.set(r, tokens.get(i));
            tokens.set(i, temp);
        }
    }
    
    private List<Image> loadImages(){
        images = new ArrayList<>();
        try{
            for (int i = game.getStartImgIndex(); i< game.getEndImgIndex(); i++){
                InputStream stream = this.getClass().getClassLoader().getResourceAsStream("games/token" + i +".jpg");
                Image img = new Image(stream,300,300,true,true);
                images.add(img);
            }
        }catch(IllegalArgumentException e){
            Logger.getLogger(LogicGameCollection.class.getName())
                    .log(Level.SEVERE, "Images could not be loaded.", e);
        }
        return images;
        
    }
}
