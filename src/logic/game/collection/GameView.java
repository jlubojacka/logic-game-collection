
package logic.game.collection;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import scenecontroller.SceneController;

public abstract class GameView extends AnchorPane{
    private static final int WORLD_WIDTH = LogicGameCollection.WORLD_WIDTH;
    private static final int WORLD_HEIGHT = LogicGameCollection.WORLD_HEIGHT;
    protected static final int LEFT_PANE_WIDTH = 160;
    protected static final int BOTTOM_PANE_HEIGHT = 180;
    private Button menuBtn;
    private Button infoBtn;
    private HBox infoPane;
    private VBox menuPane;
    private ImageView centerPaneImg;
    private StackPane centerPane;
    private Text dykTitle;
    private Text dykText;
    private StackPane dykPane;
    private Text endGameText;
    private Text endGameTitle;
    private Text instructionsText;
    private SceneController sceneCntrl;
    private Button backCategoryBtn;
    private Button backEntranceBtn;
    private Button restartBtn;
    private Button doYouKnowBtn;
    private Button closeCenterBtn;
    public ResourceBundle mainBundle;
    private PauseTransition pause;
    private AnchorPane gameElements; //elements of particular game
   
    public GameView(SceneController sc, ResourceBundle bundle){
        super();
        this.sceneCntrl = sc;
        this.mainBundle = bundle;
        loadCSS();
        init();
    }
        
    private void init(){
        this.setPrefSize(WORLD_WIDTH, WORLD_HEIGHT);
        this.instructionsText = new Text();
        pause = new PauseTransition(Duration.millis(200));
        createButtons();
        createMenu();
        createInfoPane();
        createCenterPane();
        createDykPane();
        setMoveTransitions();
        setSceneNavigation();
        setShadowEffect();
        gameElements = new AnchorPane();
        gameElements.setPrefSize(WORLD_WIDTH, WORLD_HEIGHT);
        gameElements.setMaxSize(WORLD_WIDTH, WORLD_HEIGHT);
        this.getChildren().addAll(gameElements, menuPane,infoPane,menuBtn,infoBtn,dykPane,centerPane);
        
        AnchorPane.setBottomAnchor(menuBtn, 35.0);
        AnchorPane.setLeftAnchor(menuBtn,40.0);
        AnchorPane.setBottomAnchor(infoBtn, 35.0);
        AnchorPane.setRightAnchor(infoBtn, 40.0);
        AnchorPane.setTopAnchor(menuPane,0.0);
        AnchorPane.setBottomAnchor(menuPane,0.0);
        AnchorPane.setLeftAnchor(infoPane,0.0);
        AnchorPane.setRightAnchor(infoPane,0.0);
        AnchorPane.setBottomAnchor(infoPane,0.0);
    }
    
    private void createButtons(){
        menuBtn = new Button(mainBundle.getString("menu"));
        menuBtn.getStyleClass().add("open-pane-btn");
        infoBtn = new Button(mainBundle.getString("instructions"));
        infoBtn.getStyleClass().add("open-pane-btn");
        closeCenterBtn = new Button(mainBundle.getString("closeButton"));
        closeCenterBtn.getStyleClass().add("menu-btn");
    }
    
    private void createMenu(){
        menuPane = new VBox();
        menuPane.setAlignment(Pos.CENTER);
        menuPane.setSpacing(20);
        menuPane.setPrefWidth(LEFT_PANE_WIDTH);
        menuPane.setTranslateX(- LEFT_PANE_WIDTH);
        menuPane.getStyleClass().add("menu-pane");
        setMenuContent();
 
    }
    
    private void createDykPane(){
        dykTitle = new Text(mainBundle.getString("menuDoYouKnow"));
        dykText = new Text();
        
        dykText.getStyleClass().add("end-text");
        dykTitle.getStyleClass().add("end-title");
        dykPane = new StackPane();
        InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream("main/centerPane.png");
        Image i = new Image(imageStream);
        double aspect = i.getWidth()/i.getHeight();
        double prefPaneHeight = ((WORLD_WIDTH/2.0) / aspect);
        double prefPaneWidth = ((WORLD_WIDTH/2.0));
        ImageView dykImage = new ImageView(i);
        dykImage.setFitWidth(prefPaneWidth);
        dykImage.setPreserveRatio(true);
        dykImage.setSmooth(true);
        dykPane.setPrefSize(prefPaneWidth, prefPaneHeight);
        dykPane.setLayoutX(WORLD_WIDTH/2.0 - (dykPane.getPrefWidth()/2));
        dykPane.setLayoutY(WORLD_HEIGHT/2.0 - (prefPaneHeight/2) - 50);
        dykText.setTextAlignment(TextAlignment.CENTER);
        dykText.setWrappingWidth(prefPaneWidth - 80);
        dykTitle.setTextAlignment(TextAlignment.CENTER);
        Button close = new Button(mainBundle.getString("closeButton"));
        close.getStyleClass().add("menu-btn");
        close.setOnAction((ActionEvent e)-> {
            dykPane.setVisible(false);
        });
        VBox b = new VBox(10);
        b.setAlignment(Pos.CENTER);
        b.getChildren().addAll(dykTitle, dykText, close);
        dykPane.getChildren().addAll(dykImage, b);
        dykPane.setVisible(false);
    }
    
    private void createInfoPane(){
        infoPane = new HBox();
        instructionsText.setWrappingWidth(700);
        instructionsText.getStyleClass().add("instructions");
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(instructionsText);
        scrollPane.setBackground(Background.EMPTY);
        scrollPane.setMinWidth(720);
        scrollPane.setMaxHeight(BOTTOM_PANE_HEIGHT - 60);
        infoPane.getChildren().add(scrollPane);
        HBox.setMargin(scrollPane, new Insets(35,100,5,110));
        infoPane.setPrefHeight(BOTTOM_PANE_HEIGHT);
        infoPane.setTranslateX(WORLD_WIDTH);
        infoPane.getStyleClass().add("info-pane");
    }

    private void createCenterPane(){
        InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream("main/centerPane.png");
        Image i = new Image(imageStream);
        double aspect = i.getWidth()/i.getHeight();
        double prefPaneHeight = ((WORLD_WIDTH/2.0) / aspect) - 150;
        double prefPaneWidth = ((WORLD_WIDTH/2.0) - 150);
        centerPaneImg = new ImageView(i);
        centerPaneImg.setFitWidth(prefPaneWidth);
        centerPaneImg.setPreserveRatio(true);
        centerPaneImg.setSmooth(true);
        
        centerPane = new StackPane();
        centerPane.setPrefSize(prefPaneWidth, prefPaneHeight);
        centerPane.setLayoutX(WORLD_WIDTH/2.0 - (centerPane.getPrefWidth()/2));
        centerPane.setLayoutY(WORLD_HEIGHT/2.0 - (prefPaneHeight/2));
        endGameTitle = new Text();
        endGameTitle.getStyleClass().add("end-title");
        endGameText = new Text();
        endGameText.getStyleClass().add("end-text");
        endGameText.setFill(Color.WHITESMOKE);
        endGameTitle.setFill(Color.WHITESMOKE);
        endGameText.setWrappingWidth(prefPaneWidth - 80);
        endGameText.setTextAlignment(TextAlignment.CENTER);
        closeCenterBtn.setOnAction((ActionEvent e)-> {
            centerPane.setVisible(false);
        });
        VBox c = new VBox(20);
        c.setAlignment(Pos.CENTER);
        c.getChildren().addAll(endGameTitle, endGameText, closeCenterBtn);
        centerPane.getChildren().addAll(centerPaneImg, c);
        centerPane.setVisible(false);
    }
    
    private void setShadowEffect(){
        DropShadow shadow = new DropShadow();
        shadow.setRadius(20);
        shadow.setOffsetX(-3);
        shadow.setOffsetY(12);
        shadow.setColor(Color.hsb(0.197, 0.4252, 0.1905,0.65));
        infoPane.setEffect(shadow);
        //menuPane.setEffect(shadow);
        dykPane.setEffect(shadow);
        centerPane.setEffect(shadow);
    }
    
    private void loadCSS(){
        URL path = this.getClass().getClassLoader().getResource("css/GameViewCSS.css");
        this.getStylesheets().add(path.toExternalForm());
    }
    
    private void setMoveTransitions(){
        TranslateTransition open = new TranslateTransition(Duration.millis(400),menuPane);
        open.setToX(0);
        TranslateTransition close = new TranslateTransition(Duration.millis(400),menuPane);
        
        TranslateTransition openC = new TranslateTransition(Duration.millis(400),infoPane);
        openC.setToX(LEFT_PANE_WIDTH + 5);
        TranslateTransition closeC = new TranslateTransition(Duration.millis(400),infoPane);
        
        menuBtn.setOnAction(e->{
            if (menuPane.getTranslateX() == 0){
                close.setToX(-(menuPane.getWidth()));
                close.play();
            }else {
                open.play();
            }
        });
        
        infoBtn.setOnAction(e->{
            if (infoPane.getTranslateX() == WORLD_WIDTH){
                openC.play();
            }else {
                closeC.setToX(WORLD_WIDTH);
                closeC.play();
            }
        });
    }
    
    private void setMenuContent(){
        backCategoryBtn = new Button(mainBundle.getString("menuBackCategory"));
        backEntranceBtn = new Button(mainBundle.getString("menuBackEntrance"));
        restartBtn = new Button(mainBundle.getString("menuRestart"));
        doYouKnowBtn = new Button(mainBundle.getString("menuDoYouKnow"));
        backCategoryBtn.setWrapText(true);
        backCategoryBtn.setMaxWidth(Double.MAX_VALUE);
        backEntranceBtn.setWrapText(true);
        backEntranceBtn.setMaxWidth(Double.MAX_VALUE);
        restartBtn.setWrapText(true);
        restartBtn.setMaxWidth(Double.MAX_VALUE);
        doYouKnowBtn.setWrapText(true);
        doYouKnowBtn.setMaxWidth(Double.MAX_VALUE);
        backCategoryBtn.getStyleClass().add("menu-btn");
        backEntranceBtn.getStyleClass().add("menu-btn");
        restartBtn.getStyleClass().add("menu-btn");
        doYouKnowBtn.getStyleClass().add("menu-btn");
        doYouKnowBtn.setVisible(false);
        setDoYouKnowHandler();
        setRestartHandler();
        menuPane.getChildren().addAll(backCategoryBtn, backEntranceBtn, restartBtn, doYouKnowBtn);
    }
    
    /**
     * Adds special game elements.
     * The first node in enumeration will be under all of the others.
     * @param elements
     */
    public void addGameGui(Node... elements){
        gameElements.getChildren().addAll(elements);
    }
    
    public void addGameGui(List<? extends Node> guiElements){
        gameElements.getChildren().addAll(guiElements);
    }
    
    public void addGameGui(Parent gui){
        gameElements.getChildren().add(gui);
    }
    
    public void addGameGui(Collection<Node> gui){
        gameElements.getChildren().addAll(gui);
    }
    
    public void setInstructionsText(String text){
        instructionsText.setText(text);
    }
    
    public void setDoYouKnowText(String text){
        dykText.setText(text);
        doYouKnowBtn.setVisible(true);
    }
    
    /*
    * Explicitely set desired title and text of pane with info about game state
    */
    public void setEndGameInfo(String msgTitle, String msg){
        endGameTitle.setText(msgTitle);
        endGameText.setText(msg);
    }
    
    /*
    * Set implicit texts from mainBundle according to game state (win text or game over text).
    */
    public void setEndGameInfo(boolean won){
        if (won){
            endGameTitle.setText(mainBundle.getString("winTitle"));
            endGameText.setText(mainBundle.getString("winText"));
        }else {
            endGameTitle.setText(mainBundle.getString("gameOverTitle"));
            endGameText.setText(mainBundle.getString("gameOverText"));
        }
    }
    
    public void showEndGamePane(boolean show){
        if (show){
            //small delay with pause animation before opening 
            pause.setOnFinished(e -> centerPane.setVisible(show));
            pause.play();
        }else centerPane.setVisible(show);
    }
    
    public void setSceneController(SceneController sceneCntrl){
        this.sceneCntrl = sceneCntrl;
    }
    
    public void setRestartHandler(EventHandler<MouseEvent> handler){
        restartBtn.setOnMouseClicked(handler);
    }
    
    private void setRestartHandler(){
        restartBtn.setOnMouseClicked((MouseEvent e) -> {
            centerPane.setVisible(false);
            dykPane.setVisible(false);
            start();
        });
    }
    
    public abstract void start();
    
    private void setDoYouKnowHandler(){
        doYouKnowBtn.setOnMouseClicked((MouseEvent e)-> {
            if (e.getClickCount() == 1){
                dykPane.setVisible(true);
            }
        });
    }
    
    private void setSceneNavigation(){
        backCategoryBtn.setOnMouseClicked((MouseEvent e) -> {
            if (e.getClickCount() == 1){
                sceneCntrl.goBack();
            }
        });
        
        backEntranceBtn.setOnMouseClicked((MouseEvent e) -> {
            if (e.getClickCount() == 1){
                sceneCntrl.goBack();
                Scene categoryScene = sceneCntrl.getCurrentScene();
                if (categoryScene.getRoot() instanceof CategoryView){
                    CategoryView category = (CategoryView)categoryScene.getRoot();
                    //close pane with levels of previously played game
                    category.hideLevelsPane();
                }
                sceneCntrl.goBack();
            }
        });
        
    }
    
}
