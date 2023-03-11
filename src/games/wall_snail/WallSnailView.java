
package games.wall_snail;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import logic.game.collection.GameView;
import logic.game.collection.LogicGameCollection;
import scenecontroller.SceneController;

public class WallSnailView extends GameView{
    private Circle token;
    private WallSnail game;
    private Circle tokenBg;
    private static final int WORLD_WIDTH = LogicGameCollection.WORLD_WIDTH;
    private static final int WORLD_HEIGHT = LogicGameCollection.WORLD_HEIGHT;
    private int unitSize;
    private double initTokenPosX;
    private double initTokenPosY;
    private List<Arrow> directionArrows;
    private GridPane arrowsPane;
    private GridPane gameBoard;
    private SceneController sceneCntrl;
    private ResourceBundle texts;
    private TranslateTransition dropTrans;
    private TranslateTransition moveTrans;
    private Point2D mouseDragPoint;
    private EventHandler<MouseEvent> pressed;
    private EventHandler<MouseEvent> released;
    private EventHandler<MouseEvent> dragged;
    
    public WallSnailView(WallSnail game,SceneController sceneCntrl, ResourceBundle bundle){
        super(sceneCntrl, bundle);
        this.game = game;
        this.sceneCntrl = sceneCntrl;
        directionArrows = new ArrayList<>();
        gameBoard = new GridPane();
        unitSize = (WORLD_HEIGHT - 210)/ game.getRows();
        init();
        dropTrans = new TranslateTransition(Duration.millis(200), token);
        moveTrans = new TranslateTransition();
        moveTrans.setNode(token);
    }

    private void init() {
        createToken();
        createEventHandlers();
        Locale defaultLocale = Locale.getDefault();
        texts = ResourceBundle.getBundle("games.bundles.WallSnailBundle",defaultLocale);
        
        gameBoard.setMaxHeight(WORLD_HEIGHT - 200);
        gameBoard.setAlignment(Pos.CENTER);
        
        createDirectionArrows();
        
        IntegerProperty[][] boardMatrix = game.getBoardMatrix();
        for (int i = 0; i<game.getRows(); i++){
            for (int j = 0; j<game.getColumns(); j++){
                if (boardMatrix[i][j].getValue() == 0){
                    Tile tile = new Tile(Color.ANTIQUEWHITE, unitSize);
                    tile.fillProperty().bind(Bindings.when(boardMatrix[i][j].isEqualTo(0))
                            .then(Color.LIGHTSKYBLUE).otherwise(Color.ROYALBLUE));
                    
                    tile.setId(i+"-"+j);
                    gameBoard.add(tile, j, i); //(node, column, row)
                    
                }else {
                    Tile barrier = new Tile(Color.GRAY, unitSize);
                    barrier.setId(i+"-"+j);
                    gameBoard.add(barrier, j, i);
                }
                
            }
        }
        AnchorPane.setTopAnchor(gameBoard, 50.0);
        AnchorPane.setBottomAnchor(gameBoard, (double)LEFT_PANE_WIDTH);
        double leftOffset = WORLD_WIDTH/2.0 - ((game.getColumns()*unitSize) / 2.0);
        AnchorPane.setLeftAnchor(gameBoard, leftOffset);
        
        this.addGameGui(gameBoard,tokenBg);
        this.addGameGui(token);
        setupArrows();
        this.setInstructionsText(texts.getString("instructions"));
    }
    
    private void createToken(){
        InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream("games/boatEmblem.png");
        Image p = new Image(imageStream,unitSize + 10, unitSize + 10, true, true);
        ImagePattern pattern = new ImagePattern(p);
        token = new Circle(unitSize/2);
        token.setFill(pattern);
        initTokenPosX = WORLD_WIDTH  - 200;
        initTokenPosY = WORLD_HEIGHT/2 - (token.getRadius()/2) - BOTTOM_PANE_HEIGHT;
        tokenBg = new Circle((unitSize/2) + 5,Color.THISTLE);
        tokenBg.setCenterX(initTokenPosX);
        tokenBg.setCenterY(initTokenPosY);
    }
    
    private void createEventHandlers(){
        pressed = (MouseEvent event)-> {
            token.setMouseTransparent(true);
            mouseDragPoint = token.sceneToLocal(event.getSceneX(), event.getSceneY());
        };
        released = (MouseEvent event)->{
            token.setMouseTransparent(false);
            //test correct position
            boolean valid = testPosition(event.getSceneX(), event.getSceneY());
            if (valid){
                snapToTile();
                //removeEventHandlers
                token.setOnMousePressed(null);
                token.setOnMouseReleased(null);
                token.setOnMouseDragged(null);
                token.setOnDragDetected(null);
                
                setDirectionHint();
            }else {
                //move back
                dropTrans.setToX(initTokenPosX);
                dropTrans.setToY(initTokenPosY);
                dropTrans.play();
            }
        };
        dragged = (MouseEvent event) -> {
            if (mouseDragPoint != null){
                token.setTranslateX(token.getTranslateX() - mouseDragPoint.getX() + event.getX());
                token.setTranslateY(token.getTranslateY() - mouseDragPoint.getY() + event.getY());
            }
            
        };
    }
    
    public void start() {
        token.setTranslateX(initTokenPosX);
        token.setTranslateY(initTokenPosY);
        game.clearBoard();
        setDragNDrop();
        hideArrows();
    }

    private void setupArrows(){
        arrowsPane = new GridPane();
        Circle c = new Circle(unitSize/2, Color.ALICEBLUE);
        c.setVisible(false);
        arrowsPane.add(directionArrows.get(0), 1, 0, 2, 1);
        arrowsPane.add(directionArrows.get(2), 0, 1, 1, 2);
        arrowsPane.add(directionArrows.get(3), 3, 1, 1, 2);
        arrowsPane.add(directionArrows.get(1), 1, 3, 2, 1);
        arrowsPane.add(c, 1,1,2,2);
        GridPane.setHalignment(directionArrows.get(0), HPos.CENTER);
        GridPane.setHalignment(directionArrows.get(1), HPos.CENTER);
        arrowsPane.setVgap(5);
        this.addGameGui(arrowsPane);
        hideArrows();
    }
    
    private void setDragNDrop(){
        token.setOnMousePressed(pressed);
        token.setOnMouseReleased(released);
        token.setOnMouseDragged(dragged);
    }
    
    private void setDirectionHint(){
        List<Direction> directions = game.getDirectionHint();
        if (directions.isEmpty()){
            boolean full = game.isFilled();
            if (full){
                this.setEndGameInfo(texts.getString("winTitle"), texts.getString("winText"));
            }else {
                this.setEndGameInfo(texts.getString("gameOverTitle"), texts.getString("gameOverText"));
            }
            showEndGamePane(true);
        }else {
            for (Direction d : directions){
                for (Arrow a : directionArrows){
                    if (a.getDirection().equals(d)){
                        a.setVisible(true);
                    }
                }
            }
        }
    }
    
    private void createDirectionArrows(){
        directionArrows.add(new Arrow(Direction.NORTH));
        directionArrows.add(new Arrow(Direction.SOUTH));
        directionArrows.add(new Arrow(Direction.WEST));
        directionArrows.add(new Arrow(Direction.EAST));
        for (Arrow a : directionArrows){
            a.addEventFilter(MouseEvent.MOUSE_CLICKED,this::arrowClicked);
        }
    }
    
    public void arrowClicked(MouseEvent evt){
        Arrow source = (Arrow) evt.getSource();
        hideArrows();
        int count = game.move(source.getDirection());
        moveTrans.setDuration(Duration.millis(100 * count));
        moveTrans.setByX(0);
        moveTrans.setByY(0);
        switch(source.getDirection().name()){
            case "NORTH": moveTrans.setByY(- (unitSize * count)); break;
            case "SOUTH": moveTrans.setByY(unitSize * count); break;
            case "WEST": moveTrans.setByX(- (unitSize * count)); break;
            case "EAST": moveTrans.setByX(unitSize * count); break;
        }
        moveTrans.setOnFinished(e -> {
            snapToTile();
            setDirectionHint();
        });
        moveTrans.play();
    }
    
    private void snapToTile(){
        Point2D tokenPos = game.getTokenPosition();
        int index = game.getRows() * (int)tokenPos.getY() + (int)tokenPos.getX();
        Tile r = (Tile)gameBoard.getChildren().get(index);
        Bounds tilePos = gameBoard.localToParent(r.getBoundsInParent());

        double half = tilePos.getWidth()/2;
        token.setTranslateX(tilePos.getMinX() + half);
        token.setTranslateY(tilePos.getMinY() + half);
        Bounds b = arrowsPane.getBoundsInParent();
        arrowsPane.setTranslateX(tilePos.getMinX() + half - (b.getWidth()/2));
        arrowsPane.setTranslateY(tilePos.getMinY() + half - (b.getHeight()/2));
    }
    
    private void hideArrows(){
        for (Arrow a : directionArrows){
            a.setVisible(false);
        }
    }

    private boolean testPosition(double sceneX, double sceneY) {
        Point2D pInBoard = gameBoard.sceneToLocal(new Point2D(sceneX,sceneY));
        if (((pInBoard.getX()) < 0) 
                || ((pInBoard.getX()) > (game.getColumns() * unitSize))){
            return false;
        }else if (((pInBoard.getY()) < 0) 
                || ((pInBoard.getY()) > (game.getRows() * unitSize))){
            return false;
        }else {
            int row = (int)(pInBoard.getY()/unitSize);
            int column = (int)(pInBoard.getX()/unitSize);
            boolean blocked = game.isBlocked(row, column);
            if (!blocked){
                game.setTokenPosition(row, column);
                return true;
            }else return false;
        }
    }
    
}
