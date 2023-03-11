
package logic.game.collection;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import scenecontroller.SceneController;

public class CategoryView extends Pane{
    private static final int WORLD_WIDTH = LogicGameCollection.WORLD_WIDTH;
    private static final int WORLD_HEIGHT = LogicGameCollection.WORLD_HEIGHT;
    private final double DEFAULT_PANE_WIDTH = WORLD_WIDTH/1.5;
    private final double DEFAULT_PANE_HEIGHT= WORLD_HEIGHT/2;
    private final double DEFAULT_PANE_X = 165;
    private final double DEFAULT_PANE_Y = 140;
    private ResourceBundle mainBundle;
    private SceneController sceneController;
    private Category category;
    private List<GamePrototype> gamePrototypes;
    private List<String> gameNames;
    private List<Thumbnail> thumbnails;
    private ImageView background;
    private BorderPane borderPane;
    private ScrollPane scrollPane;
    private FlowPane flowPane;
    private Button backBtn;
    private Text categoryTitle;
    private Pane thumbPane;
    private GameLevelsPane levelsPane;
    
    public CategoryView(Category c, List<GamePrototype> games, ResourceBundle mainBundle, SceneController sceneCntrl){
        super();
        this.category = c;
        this.gamePrototypes = games;
        this.mainBundle = mainBundle;
        this.sceneController = sceneCntrl;
        
        thumbPane = new Pane();
        thumbPane.setMinSize(WORLD_WIDTH, WORLD_HEIGHT);
        levelsPane = new GameLevelsPane(mainBundle, sceneController);
        levelsPane.setLayoutX(WORLD_WIDTH/2 - (levelsPane.getMaxWidth()/2));
        levelsPane.setLayoutY(WORLD_HEIGHT/2 - (levelsPane.getMaxHeight()/2));
        
        levelsPane.setVisible(false);
        loadCSS();
        getGameNames();
        createUI();
        registerCloseListener();
    }
    
    private void loadCSS(){
        URL path = this.getClass().getClassLoader().getResource("css/CategoryViewCSS.css");
        this.getStylesheets().add(path.toExternalForm());
    }
    
    private void createUI(){
        
        this.setPrefSize(WORLD_WIDTH, WORLD_HEIGHT);
        createThumbnails();
        loadBackgroundImage();
        
        backBtn = new Button(mainBundle.getString("backButton"));
        backBtn.getStyleClass().add("back-button");
        backBtn.setAlignment(Pos.CENTER);
        backBtn.setOnAction(e->{
            levelsPane.setVisible(false);
            sceneController.goBack();
        });
        StackPane buttonLayer = new StackPane();
        buttonLayer.setBackground(Background.EMPTY);
        buttonLayer.getChildren().add(backBtn);
        buttonLayer.setPrefSize(WORLD_WIDTH, backBtn.getHeight());
        buttonLayer.setLayoutY(533);
        categoryTitle = new Text(mainBundle.getString(category.name().toLowerCase() + "Category"));
        categoryTitle.setTextAlignment(TextAlignment.CENTER);
        categoryTitle.getStyleClass().add("title");
        StackPane titleLayer = new StackPane();
        titleLayer.setBackground(Background.EMPTY);
        titleLayer.getChildren().add(categoryTitle);
        titleLayer.setPrefWidth(WORLD_WIDTH);
        titleLayer.setLayoutY(30);
        
        flowPane = new FlowPane();
        flowPane.setHgap(20);
        flowPane.setVgap(20);
        flowPane.setBackground(Background.EMPTY);
        flowPane.setPrefSize(DEFAULT_PANE_WIDTH - 20, DEFAULT_PANE_HEIGHT - 20);
        flowPane.getChildren().addAll(thumbnails);
        
        scrollPane = new ScrollPane();
        scrollPane.setContent(flowPane);
        scrollPane.setLayoutX(DEFAULT_PANE_X);
        scrollPane.setLayoutY(DEFAULT_PANE_Y);
        scrollPane.setPrefSize(DEFAULT_PANE_WIDTH, DEFAULT_PANE_HEIGHT);
        scrollPane.setBackground(Background.EMPTY);
        
        thumbPane.getChildren().addAll(background,titleLayer, scrollPane, buttonLayer);
        this.getChildren().addAll(thumbPane, levelsPane);
        
        
    }
    
    private void createThumbnails(){
        thumbnails = new ArrayList<>();
        for (String name : gameNames){
            List<GamePrototype> levels = gamePrototypes.stream()
                    .filter(a -> a.getClassName().equals(name))
                    .collect(Collectors.toList());
            levels.sort((g1,g2)-> {return g1.getLevel() - g2.getLevel();});
            Thumbnail thumb = new Thumbnail(levels, mainBundle);
            thumb.setPrefSize(100, 50);
            thumb.getPlayButton().setOnMouseClicked((MouseEvent e) -> {
                if (e.getClickCount() == 1)
                    openLevelsPane(thumb.getLevels());
            });
            thumbnails.add(thumb);
        }
    }
    
    private void getGameNames(){
        gameNames = new ArrayList<>();
        for (GamePrototype g : gamePrototypes){
            String name = g.getClassName();
            if (!gameNames.contains(name)){
                gameNames.add(name);
            }
        }
    }
    
    private void openLevelsPane(List<GamePrototype> levels){
        levelsPane.setContent(levels);
        levelsPane.fadeIn();
        levelsPane.setVisible(true);
        thumbPane.setOpacity(0.5);
        thumbPane.setDisable(true);
    }
    
    private EventHandler<MouseEvent> closeLevelsPaneHandler(){
        EventHandler<MouseEvent> h = (MouseEvent event) -> {
            thumbPane.setOpacity(1.0);
            thumbPane.setDisable(false);
        };
        return h;
    }
    
    public void hideLevelsPane(){
        levelsPane.setVisible(false);
        thumbPane.setOpacity(1.0);
        thumbPane.setDisable(false);
    }
    
    private void registerCloseListener(){
        levelsPane.getBackBtn().addEventHandler(MouseEvent.MOUSE_CLICKED, closeLevelsPaneHandler());
    }
    
    private void loadBackgroundImage(){
        InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream("main/LG-Category.png");
        background = new ImageView(new Image(imageStream));
        background.setFitWidth(WORLD_WIDTH);
        background.setPreserveRatio(true);
        background.setSmooth(true);
        background.setCache(true);
        
    }
    
}
