
package logic.game.collection;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import scenecontroller.SceneController;

public class CreditsView extends Pane{
    private static final int WORLD_WIDTH = LogicGameCollection.WORLD_WIDTH;
    private static final int WORLD_HEIGHT = LogicGameCollection.WORLD_HEIGHT;
    private static final int POS_X = 237;
    private static final int POS_Y = 125;
    private ResourceBundle mainBundle;
    private SceneController sceneCntrl;
    private ImageView background;
    private Credits creditsPane;
    private Guide guidePane;
    private Button backBtn;
    private Text quote;
    
    public CreditsView(ResourceBundle mainBundle, SceneController sceneCntrl){
        this.mainBundle = mainBundle;
        this.sceneCntrl = sceneCntrl;
        loadCSS();
        createUI();
    }
    
    private void createUI(){
        InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream("main/credits.png");
        background = new ImageView(new Image(imageStream));
        background.setPreserveRatio(true);
        background.setSmooth(true);
        background.setFitWidth(WORLD_WIDTH);
        
        guidePane = new Guide();
        creditsPane = new Credits();
        guidePane.setLayoutX(POS_X);
        guidePane.setLayoutY(POS_Y);
        creditsPane.setLayoutX(POS_X);
        creditsPane.setLayoutY(POS_Y);
        creditsPane.setPickOnBounds(false);
        guidePane.setPickOnBounds(false);
        setActionHandler(creditsPane.getTitle(), creditsPane);
        setActionHandler(guidePane.getTitle(), guidePane);
        
        quote = new Text("Schola ludus");
        quote.setLayoutX(156);
        quote.setLayoutY(95);
        quote.setRotate(-18);
        quote.getStyleClass().add("quote");
        
        backBtn = new Button(mainBundle.getString("backButton"));
        backBtn.getStyleClass().add("pane-btn");
        backBtn.setOnMouseClicked((MouseEvent e) -> {
            if (e.getClickCount() == 1){
                sceneCntrl.goBack();
            }
        });
        StackPane btnLayer = new StackPane(backBtn);
        btnLayer.setPrefSize(WORLD_WIDTH, 40);
        btnLayer.setLayoutY(WORLD_HEIGHT - 50);
        
        this.getChildren().addAll(background, btnLayer, quote, guidePane, creditsPane);
    }
    
    private void loadCSS(){
        URL path = this.getClass().getClassLoader().getResource("css/CreditsViewCSS.css");
        this.getStylesheets().add(path.toExternalForm());
    }
    
    private void setActionHandler(StackPane title, Pane p){
        title.setOnMouseClicked((MouseEvent e) -> {
            if (e.getClickCount() == 1){
                p.toFront();
            }
        });
    }
    
    
}
