
package logic.game.collection;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import scenecontroller.SceneController;

public class Entrance extends Parent{
    private final ImageView []drawerImages = new ImageView[4];
    private ImageView bg;
    private ImageView top;
    private ImageView middlePatch;
    private ImageView labelLeftT;
    private ImageView labelRightT;
    private ImageView labelLeftB;
    private ImageView labelRightB;
    private Group questionMark;
    private Text questionText;
    private VBox questionBox;
    
    private static final int WORLD_WIDTH = LogicGameCollection.WORLD_WIDTH;
    private static final int WORLD_HEIGHT = LogicGameCollection.WORLD_HEIGHT;
    
    private ResourceBundle mainBundle;
    private SceneController sceneCntrl;
    
    private Drawer []drawers = new Drawer[4];
    private Text title;
    
    public Entrance(ResourceBundle bundle, SceneController sc){
        super();
        mainBundle = bundle;
        sceneCntrl = sc;
        loadMainResources();
        placeImages();
        placeTitle();
        placeQuestionGroup();
    }
    
    private void loadMainResources(){
        loadCSS();
        InputStream imageStream1 = this.getClass().getClassLoader().getResourceAsStream("main/main_screen/bg.png");
        bg = new ImageView(new Image(imageStream1));
        bg.setFitWidth(WORLD_WIDTH);
        bg.setPreserveRatio(true);
        bg.setSmooth(true);
        bg.setCache(true);
        
        InputStream imageStream2 = this.getClass().getClassLoader().getResourceAsStream("main/main_screen/top.png");
        top = new ImageView(new Image(imageStream2));
        top.setFitWidth(WORLD_WIDTH - 68);
        top.setPreserveRatio(true);
        InputStream imageStream3 = this.getClass().getClassLoader().getResourceAsStream("main/main_screen/middle_patch.png");
        middlePatch = new ImageView(new Image(imageStream3));
        middlePatch.setFitWidth(WORLD_WIDTH - 120);
        middlePatch.setPreserveRatio(true);
        InputStream imageStream4 = this.getClass().getClassLoader().getResourceAsStream("main/main_screen/label_left.png");
        labelLeftT = new ImageView(new Image(imageStream4));
        labelLeftT.setFitWidth(300);
        labelLeftT.setPreserveRatio(true);
        InputStream imageStream5 = this.getClass().getClassLoader().getResourceAsStream("main/main_screen/label_left.png");
        labelLeftB = new ImageView(new Image(imageStream5));
        labelLeftB.setFitWidth(290);
        labelLeftB.setPreserveRatio(true);
        InputStream imageStream6 = this.getClass().getClassLoader().getResourceAsStream("main/main_screen/label_right.png");
        labelRightT = new ImageView(new Image(imageStream6));
        labelRightT.setFitWidth(300);
        labelRightT.setPreserveRatio(true);
        InputStream imageStream7 = this.getClass().getClassLoader().getResourceAsStream("main/main_screen/label_right.png");
        labelRightB = new ImageView(new Image(imageStream7));
        labelRightB.setFitWidth(290);
        labelRightB.setPreserveRatio(true);
        for (int i=1; i<=4; i++){
            InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream("main/main_screen/drawer"+i+".png");
            drawerImages[i-1] = new ImageView(new Image(imageStream));
            drawerImages[i-1].setPreserveRatio(true);
            drawerImages[i-1].setSmooth(true);
            if (i <= 2){
                drawerImages[i-1].setFitWidth(454);
            }else {
                drawerImages[i-1].setFitWidth(420);
            }
        }
        
        InputStream questionStream1 = this.getClass().getClassLoader().getResourceAsStream("main/qWhite.png");
        ImageView questionWhite = new ImageView(new Image(questionStream1));
        questionWhite.setFitHeight(60);
        questionWhite.setPreserveRatio(true);
        InputStream questionStream2 = this.getClass().getClassLoader().getResourceAsStream("main/qColored.png");
        ImageView questionColored = new ImageView(new Image(questionStream2));
        questionColored.setFitHeight(60);
        questionColored.setPreserveRatio(true);
        questionMark = new Group(questionColored);
        questionMark.getChildren().add(questionWhite);
    }
    
    private void loadCSS(){
        URL path = this.getClass().getClassLoader().getResource("css/EntranceCSS.css");
        this.getStylesheets().add(path.toExternalForm());
    }
    
    private void placeImages(){
        drawers[0] = new Drawer(drawerImages[0],85,292);
        drawers[1] = new Drawer(drawerImages[1],582,294);
        drawers[2] = new Drawer(drawerImages[2],120,462);
        drawers[3] = new Drawer(drawerImages[3],580,462);
        middlePatch.setLayoutX(60);
        middlePatch.setLayoutY(446);
        middlePatch.setScaleX(0.9);
        top.setLayoutX(35);
        top.setLayoutY(88);
        
        drawers[0].setLabel(labelLeftT);
        drawers[1].setLabel(labelRightT, 0,-4);
        drawers[2].setLabel(labelLeftB);
        drawers[3].setLabel(labelRightB);

        this.getChildren().addAll(bg,drawers[3],drawers[2],middlePatch,drawers[1],drawers[0],top);
    }
    
    private void placeTitle(){
        title = new Text(mainBundle.getString("mainTitle"));
        title.getStyleClass().add("title");
        StackPane titleLayer = new StackPane();
        titleLayer.setBackground(Background.EMPTY);
        titleLayer.setPrefSize(WORLD_WIDTH, title.getBoundsInParent().getHeight());
        titleLayer.getChildren().add(title);
        StackPane.setAlignment(title, Pos.CENTER);
        titleLayer.setLayoutY(30);
        titleLayer.setRotate(2);

        this.getChildren().addAll(titleLayer);
    }
    
    private void placeQuestionGroup(){
        questionBox = new VBox(5);
        questionBox.setAlignment(Pos.CENTER);
        questionText = new Text(mainBundle.getString("moreInfoText"));
        questionText.getStyleClass().add("question-text");
        questionBox.getChildren().addAll(questionMark, questionText);
        questionBox.setTranslateX(118);
        questionBox.setTranslateY(75);
        questionBox.getTransforms().add(new Rotate(-25));
        this.getChildren().add(questionBox);
        setQuestionMouseHandler();
    }
    
    public void setQuestionMouseHandler(){
        questionBox.setOnMouseEntered((MouseEvent e) -> {
            questionMark.getChildren().get(1).setOpacity(0.0);
            questionText.setFill(Color.RED);
        });
        questionBox.setOnMouseExited((MouseEvent e) -> {
            questionMark.getChildren().get(1).setOpacity(1.0);
            questionText.setFill(Color.web("#221f1f"));
        });
        
        questionBox.setOnMouseClicked((MouseEvent e) -> {
            if (e.getClickCount() == 1){
                if (sceneCntrl != null){
                    sceneCntrl.goTo("credits");
                }
            }
        });
        
    }
    
    public void setSceneController(SceneController cntrl){
        this.sceneCntrl = cntrl;
    }
    
    public Drawer[] getDrawers(){
        return this.drawers;
    }
}
