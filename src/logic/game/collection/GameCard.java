
package logic.game.collection;

import java.util.ResourceBundle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Light.Distant;
import javafx.scene.effect.Lighting;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import scenecontroller.SceneController;


public class GameCard extends VBox{
    private ResourceBundle mainBundle;
    private GamePrototype gamePrototype;
    private Text numberText;
    private Text levelText;
    private Background background;
    private SceneController sceneCntrl;
    private Bloom bloom = new Bloom(1.0);
    
    public GameCard(GamePrototype gP, ResourceBundle bundle, SceneController sc){
        super();
        this.mainBundle = bundle;
        this.gamePrototype = gP;
        this.sceneCntrl = sc;
        createGUI();
        setMouseInteractions();
        this.setAlignment(Pos.CENTER);
        this.setEffect(bloom);
    }
    
    private void createGUI(){
        levelText = new Text(mainBundle.getString("levelText"));
        String number = gamePrototype.getLevel() + ".";
        numberText = new Text(number);
        numberText.setFont(new Font(30));
        Lighting l = new Lighting();
        Distant light = new Distant();
        light.setAzimuth(-125.0f);
        l.setLight(light);
        l.setSurfaceScale(3.0f);
        numberText.setEffect(l);
        this.setSpacing(5);
        this.setPrefSize(70, 70);
        background = new Background(new BackgroundFill(Color.BEIGE, new CornerRadii(5.0), Insets.EMPTY));
        this.setBackground(background);
        this.getChildren().addAll(numberText, levelText);
    }
    
    private void setMouseInteractions(){
        this.setOnMouseEntered(e->{
            bloom.setThreshold(0.5);
        });
        this.setOnMouseExited(e->{
            bloom.setThreshold(1.0);
        });
        this.setOnMouseClicked((MouseEvent e) -> {
            if (e.getClickCount() == 1){
                if (sceneCntrl != null){
                    GameView gameView = gamePrototype.createGui(sceneCntrl, mainBundle);
                    LogicGameCollection.fitToWindow(gameView);
                    
                    //aux layer for proper refreshing
                    Pane viewLayer = new Pane();
                    viewLayer.getChildren().add(gameView);
                    double w = LogicGameCollection.getDefaultWindowWidth();
                    double h = LogicGameCollection.getDefaultWindowHeight();
                    viewLayer.setMinSize(w, h);
                    Scene gameScene = new Scene(viewLayer);
                    
                    String id = "game" + gamePrototype.getClassName();
                    sceneCntrl.addScene(id, gameScene);
                    gamePrototype.start();
                    sceneCntrl.goTo(id);
                }
            }
        }); 
    }
    
}
