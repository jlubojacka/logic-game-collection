
package logic.game.collection;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import scenecontroller.SceneController;

public class GameLevelsPane extends StackPane{
    private static final int WORLD_WIDTH = LogicGameCollection.WORLD_WIDTH;
    private static final int WORLD_HEIGHT = LogicGameCollection.WORLD_HEIGHT;
    private static final double PREF_W = WORLD_WIDTH/1.2;
    private static final double PREF_H = WORLD_HEIGHT/1.2;
    private static final int TOP_OFFSET = 70;
    private Text titleText;
    private Button backBtn;
    private BorderPane borderPane;
    private ImageView bg;
    private double imgHeight;
    private ScrollPane scrollPane;
    private FlowPane flowPane;
    private List<GamePrototype> gameLevels;
    private List<GameCard> gameCards;
    private ResourceBundle mainBundle;
    private FadeTransition fadeIn;
    private FadeTransition fadeOut;
    private SceneController controller;
    
    public GameLevelsPane(ResourceBundle bundle, SceneController sc){
        super();
        this.mainBundle = bundle;
        this.controller = sc;
        fadeIn = new FadeTransition(Duration.millis(350));
        fadeIn.setNode(this);
        fadeOut = new FadeTransition(Duration.millis(250));
        fadeOut.setNode(this);
        
        createGUI();
        setEffect();
        setButtonHandle();
    }

    private void createGUI(){
        this.setMaxSize(PREF_W, PREF_H);
        this.setBackground(Background.EMPTY);
        
        titleText = new Text();
        titleText.setFill(Color.FLORALWHITE);
        titleText.setFont(Font.font("Verdana", FontWeight.BOLD,21));
        backBtn = new Button(mainBundle.getString("closeButton"));
        backBtn.setFont(new Font(15));
        borderPane = new BorderPane();
        flowPane = new FlowPane();
        scrollPane = new ScrollPane();
        
        borderPane.setBackground(Background.EMPTY);
        borderPane.setMaxSize(400,250);
        
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setHgap(10);
        flowPane.setVgap(10);
        flowPane.setBackground(Background.EMPTY);
        flowPane.setPadding(new Insets(10,10,10,10));
        flowPane.setMaxWidth(370);
        
        scrollPane.setPadding(new Insets(10,10,10,10));
        scrollPane.setBackground(Background.EMPTY);

        borderPane.setTop(titleText);
        borderPane.setCenter(flowPane);
        borderPane.setBottom(backBtn);
        BorderPane.setAlignment(titleText, Pos.CENTER);
        BorderPane.setAlignment(scrollPane, Pos.CENTER);
        BorderPane.setAlignment(backBtn, Pos.CENTER);
        BorderPane.setMargin(titleText, new Insets(10,0,10,0));
        BorderPane.setMargin(scrollPane, new Insets(20,0,0,0));
        BorderPane.setMargin(backBtn, new Insets(10,0,10,0));
        
        loadBgImage();
        this.getChildren().addAll(bg, borderPane);
        
        StackPane.setAlignment(bg, Pos.CENTER);
        StackPane.setMargin(bg, new Insets(TOP_OFFSET,0,0,0));
        StackPane.setMargin(borderPane, new Insets(TOP_OFFSET + 20,0,0,0));
    }    
    
    private void loadBgImage(){
        InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream("main/owlsPane2.png");
        bg = new ImageView(new Image(imageStream));
        bg.setFitWidth(PREF_W);
        bg.setPreserveRatio(true);
        bg.setSmooth(true);
        bg.setCache(true);
    }
    
    private void setEffect(){
        DropShadow shadow = new DropShadow();
        shadow.setRadius(12);
        shadow.setOffsetX(-10);
        shadow.setOffsetY(10);
        shadow.setColor(Color.hsb(226, 0.25, 0.45, 0.6));
        this.setEffect(shadow);
    }
    
    private void setButtonHandle(){
        backBtn.setOnMouseClicked((MouseEvent e) -> {
            if (e.getClickCount() == 1){
                fadeOut();
            }
        });
    }
    
    public void fadeIn(){
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.setOnFinished(e -> {
            this.setVisible(true);
        });
        fadeIn.play();
    }
    
    public void fadeOut(){
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            this.setVisible(false);
        });
        fadeOut.play();
    }
    
    public Button getBackBtn(){
        return backBtn;
    }
    
    public void setContent(List<GamePrototype> levels){
        gameLevels = levels;
        flowPane.getChildren().clear();
        gameCards  = new ArrayList<>();
        for (GamePrototype g : levels){
            GameCard card = new GameCard(g, mainBundle, controller);
            gameCards.add(card);
        }
        flowPane.getChildren().addAll(gameCards);
        titleText.setText(levels.get(0).getTitle());
    }
}
