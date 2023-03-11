
package games.motherboard;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import logic.game.collection.GameView;
import logic.game.collection.LogicGameCollection;
import scenecontroller.SceneController;

public class MotherboardView extends GameView{
    private Motherboard game;
    private static final int WORLD_WIDTH = LogicGameCollection.WORLD_WIDTH;
    private static final int WORLD_HEIGHT = LogicGameCollection.WORLD_HEIGHT;
    private int unitSize;
    private ResourceBundle texts;
    private List<Color> colors;
    private Pane gameBoard;
    private TracesManager traceManager;
    private EventHandler<MouseEvent> tokenClicked;
    private EventHandler<MouseEvent> moveInGrid;
    private EventHandler<MouseEvent> tileClicked;
    private List<Token> tokens;
    private ImageView background;
    
    public MotherboardView(Motherboard game,SceneController sc, ResourceBundle bundle) {
        super(sc, bundle);
        unitSize = (WORLD_HEIGHT - BOTTOM_PANE_HEIGHT - 30)/ game.getRows();
        this.game = game;
        Locale defaultLocale = Locale.getDefault();
        texts = ResourceBundle.getBundle("games.bundles.MotherboardBundle",defaultLocale);
        init();
    }

    private void init(){
        traceManager = new TracesManager(unitSize, game.getBoardMatrix());
        createRandomColors();
        createGameBoard();
        createTraces();
        InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream("games/motherboard.png");
        background = new ImageView(new Image(imageStream));
        background.setPreserveRatio(true);
        background.setFitWidth(WORLD_WIDTH);
        background.setOpacity(0.75);
        gameBoard.getChildren().add(traceManager);
        gameBoard.getChildren().addAll(tokens);
        this.addGameGui(background, gameBoard);
        this.setInstructionsText(texts.getString("instructions"));
        this.setDoYouKnowText(texts.getString("doYouKnow"));
    }
    
    private void createRandomColors(){
        colors = Arrays.asList(Color.GOLD, Color.FORESTGREEN, 
            Color.CRIMSON, Color.SIENNA, Color.CORNFLOWERBLUE,
            Color.DARKBLUE,Color.SPRINGGREEN, Color.MEDIUMORCHID,
             Color.RED, Color.DARKTURQUOISE,Color.BURLYWOOD,
            Color.MEDIUMVIOLETRED); 
    }
    
    private void createGameBoard(){
        gameBoard = new Pane();
        gameBoard.setMaxHeight(WORLD_HEIGHT - BOTTOM_PANE_HEIGHT - 30);
        
        int [][]boardMatrix = game.getBoardMatrix();
        
        for (int i = 0; i<game.getRows(); i++){
            for (int j = 0; j<game.getColumns(); j++){
                Tile tile = new Tile(unitSize, i, j);
                tile.setTranslateX(j * unitSize);
                tile.setTranslateY(i * unitSize);
                gameBoard.getChildren().add(tile);
                if((boardMatrix[i][j]==0) || (boardMatrix[i][j] == 2)){
                    tile.setFill(Color.WHITE);  
                    setTileClickedHandler(tile);
                }else tile.setFill(Color.GRAY);
            }
        }
        
        gameBoard.setTranslateX(WORLD_WIDTH/2.0 - (game.getColumns()*unitSize)/2.0);
        gameBoard.setTranslateY(30);
        setMoveInGridHandler();
        
    }
    
    private void createTraces(){
        tokens = new ArrayList<>();
        List<Connection> conn = game.getConnections();
        int i = 0; 
        for (Connection c: conn){
            Token t1 = new Token(unitSize - (unitSize/7.0), 
                    (int) c.getFirst().getY(), (int)c.getFirst().getX());
            Token t2 = new Token(unitSize - (unitSize/7.0), 
                    (int) c.getSecond().getY(), (int)c.getSecond().getX());
            t1.setCenterX((c.getFirst().getX()*unitSize) + (unitSize/2.0));
            t1.setCenterY((c.getFirst().getY()*unitSize) + (unitSize/2.0));
            t2.setCenterX((c.getSecond().getX()*unitSize) + (unitSize/2.0));
            t2.setCenterY((c.getSecond().getY()*unitSize) + (unitSize/2.0));
            t1.setFill(colors.get(i));
            t2.setFill(colors.get(i));
            tokens.add(t1);
            tokens.add(t2);
            setTokenClickedHandler(t1);
            setTokenClickedHandler(t2);
            i++;
            traceManager.addTrace(new Trace(t1,t2));
        }
    }
    
    private void setTileClickedHandler(Tile t){
        t.setOnMouseClicked((MouseEvent e) -> {
            if (e.getButton().equals(MouseButton.PRIMARY))
                traceManager.tileClicked(t);
        });
    }
    
    private void setTokenClickedHandler(Token t){
        t.setOnMouseClicked((MouseEvent e) -> {
            if (e.getButton().equals(MouseButton.PRIMARY)){
                traceManager.tokenClicked(t);
                testEnd();
            }else if (e.getButton().equals(MouseButton.SECONDARY)){
                traceManager.removeTrace(t);
            }
        });
    }
    
    private void setMoveInGridHandler(){
        gameBoard.setOnMouseMoved((MouseEvent e) -> {
            if (traceManager.isMoveEnabled()){
                Point2D localPoint = gameBoard.sceneToLocal(e.getSceneX(), e.getSceneY());
                traceManager.moveInBoard(localPoint.getX(), localPoint.getY());
            }
        });
    }
    
    private void testEnd(){
        if (traceManager.won()){
            this.setEndGameInfo(true);
            this.showEndGamePane(true);
        }
    }
    
    
    @Override
    public void start() {
        traceManager.reset();
    }
    
}
