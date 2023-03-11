
package games.magic.square;

import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import logic.game.collection.GameView;
import logic.game.collection.LogicGameCollection;
import scenecontroller.SceneController;

public class MagicSquareView extends GameView{
    private static final int TOLERANCE = 5;
    private static final int WORLD_WIDTH = LogicGameCollection.WORLD_WIDTH;
    private static final int WORLD_HEIGHT = LogicGameCollection.WORLD_HEIGHT;
    private Text numberText;
    private int unitSize;
    private Pane gameBoard;
    private MagicSquare game;
    private TranslateTransition dropTrans;
    private TranslateTransition swapTrans;
    private Tile firstSelected;
    private Point2D selectedTilePoint;
    private ResourceBundle texts;
    private ImageView background;
    private SimpleIntegerProperty counter;
    private Accordion accordion;
    
    public MagicSquareView(MagicSquare game, SceneController sc, ResourceBundle bundle) {
        super(sc, bundle);
        this.game = game;
        gameBoard = new Pane();
        unitSize = (WORLD_HEIGHT - 230)/ game.getSize();
        dropTrans = new TranslateTransition(Duration.millis(200));
        swapTrans = new TranslateTransition(Duration.millis(300));
        init();
    }
    
    private void init(){
        Locale defaultLocale = Locale.getDefault();
        texts = ResourceBundle.getBundle("games.bundles.MagicSquareBundle",defaultLocale);
        InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream("games/melancholia.png");
        background  = new ImageView(new Image(imageStream));
        background.setPreserveRatio(true);
        background.setFitWidth(WORLD_WIDTH);
        background.setOpacity(0.87);
        counter = new SimpleIntegerProperty(0);
        createHintBox();
        
        this.addGameGui(background, gameBoard, accordion);
        this.setInstructionsText(texts.getString("instructions"));
        
        AnchorPane.setTopAnchor(gameBoard, 50.0);
        AnchorPane.setBottomAnchor(gameBoard, LEFT_PANE_WIDTH + 20.0);
        gameBoard.setTranslateX(WORLD_WIDTH/2.0 - ((game.getSize() *unitSize) / 2.0));
        
        accordion.setTranslateX(70.0);
        accordion.setTranslateY(70.0);
        
        this.setEndGameInfo(texts.getString("winTitle"), texts.getString("winText"));
        this.setDoYouKnowText(texts.getString("doYouKnow"));
    }
    
    private void createHintBox(){
        Label counterField = new Label();
        counterField.setPadding(new Insets(10));
        counterField.textProperty().bind(counter.asString());
        TitledPane counterP = new TitledPane(texts.getString("counterText"),counterField);
        
        Text hintText = new Text(texts.getString("hintText"));
        hintText.setFont(Font.font(14));
        numberText = new Text(String.valueOf(game.getInitSum()));
        numberText.setFont(Font.font(20));
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));
        box.setAlignment(Pos.CENTER);
        box.getChildren().addAll(hintText, numberText);
        TitledPane hintP = new TitledPane(texts.getString("hintTitle"), box);
        
        accordion = new Accordion(counterP, hintP);
        accordion.setExpandedPane(counterP);
        accordion.setScaleX(1.2);
        accordion.setScaleY(1.2);
    }
    
    private void fillPane(){
        int [][]boardMatrix = game.getBoardMatrix();
        for (int i=0; i< game.getSize(); i++){
            for (int j=0; j < game.getSize(); j++){
                Tile t = new Tile(unitSize, boardMatrix[i][j]);
                t.setXPos(j);
                t.setYPos(i);
                t.setTranslateX(j * unitSize);
                t.setTranslateY(i * unitSize);
                setEventHandler(t);
                gameBoard.getChildren().add(t);
            }
        }
    }
    
    @Override
    public void start(){
        counter.setValue(0);
        showEndGamePane(false);
        game.shuffle();
        gameBoard.getChildren().clear();
        if (numberText.getText().equals("0")){
            numberText.setText(String.valueOf(game.getInitSum()));
        }
        fillPane();
    }
    
    private void setEventHandler(Tile t){
        t.setOnMousePressed((MouseEvent e)-> {
            t.setMouseTransparent(true);
            t.toFront();
            firstSelected = t;
            selectedTilePoint = t.sceneToLocal(e.getSceneX(), e.getSceneY());
        });
        
        t.setOnMouseDragged((MouseEvent e)-> {
            if (selectedTilePoint != null){
                t.setTranslateX(t.getTranslateX() - selectedTilePoint.getX() + e.getX());
                t.setTranslateY(t.getTranslateY() - selectedTilePoint.getY() + e.getY());
            }
        });
        
        t.setOnMouseReleased((MouseEvent e)-> {
             boolean valid = testPosition(e.getSceneX(), e.getSceneY());
             if (valid){
                 Tile secondSelected = findTile(e.getSceneX(), e.getSceneY());
                 swapTiles(firstSelected, secondSelected);
                 counter.setValue(counter.getValue()+1);
             }else {
                 //drop trans
                 dropTrans.setNode(t);
                 dropTrans.setToX(firstSelected.getXPos() * unitSize);
                 dropTrans.setToY(firstSelected.getYPos() * unitSize);
                 dropTrans.play();
             }
             t.setMouseTransparent(false);
        });
    }
    
    private boolean testPosition(double x, double y){
        Point2D pInBoard = gameBoard.sceneToLocal(new Point2D(x,y));
        if (((pInBoard.getX() + TOLERANCE) < 0) 
                || ((pInBoard.getX() - TOLERANCE) > (game.getSize() * unitSize))){
            return false;
        }else if (((pInBoard.getY() + TOLERANCE) < 0) 
                || ((pInBoard.getY() - TOLERANCE) > (game.getSize() * unitSize))){
            return false;
        }else return true;
    }
    
    private Tile findTile(double sceneX, double sceneY){
        Point2D pInBoard = gameBoard.sceneToLocal(sceneX, sceneY);
        int i = (int)(pInBoard.getY()/unitSize);
        int j = (int)(pInBoard.getX()/unitSize);
        for (Node n : gameBoard.getChildren()){
            Tile tile = (Tile)n;
            if ((tile.getXPos() == j) && (tile.getYPos() == i)){
                return tile;
            }
        }
        return (Tile)gameBoard.getChildren().get(0);
    }
    
    private void swapTiles(Tile first, Tile second){
        int fJ = first.getXPos();
        int fI = first.getYPos();
        int sJ = second.getXPos();
        int sI = second.getYPos();
        
        int [][]matrix = game.getBoardMatrix();
        first.setTranslateX(second.getXPos() * unitSize);
        first.setTranslateY(second.getYPos() * unitSize);
        swapTrans.setNode(second);
        swapTrans.setToX(fJ * unitSize);
        swapTrans.setToY(fI * unitSize);
        swapTrans.setOnFinished(e -> {
            matrix[fI][fJ] = second.getNmuber();
            matrix[sI][sJ] = first.getNmuber();
            first.setXPos(second.getXPos());
            first.setYPos(second.getYPos());
            second.setXPos(fJ);
            second.setYPos(fI);
            testSum();
        });
        swapTrans.play();
        
    }
    
    private void testSum(){
        boolean won = game.testSum();
        if (won){
            this.showEndGamePane(true);
        }
    }
    
}
