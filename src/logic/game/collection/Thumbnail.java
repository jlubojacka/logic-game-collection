
package logic.game.collection;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Thumbnail extends BorderPane{
    private static final String UNIVERSAL_PATH = "main/thumbnails/universal.png";
    private ImageView image;
    private Text titleText;
    private Button playButton;
    private ResourceBundle mainBundle;
    private List<GamePrototype> games;
    
    public Thumbnail(List<GamePrototype> games, ResourceBundle mainBundle){
        super();
        this.games = games;
        this.mainBundle = mainBundle;
        loadCSS();
        this.getStyleClass().clear();
        this.getStyleClass().add("thumbnail");
        createChildren();
    }
    
    private void loadCSS(){
        URL path = this.getClass().getResource("/css/ThumbnailCSS.css");
        this.getStylesheets().add(path.toExternalForm());
    }
    
    private void createChildren(){
        String gameTitle = games.get(0).getTitle();
        titleText = new Text(gameTitle);
        titleText.setTextAlignment(TextAlignment.CENTER);
        titleText.getStyleClass().add("title-text");
        
        playButton = new Button(mainBundle.getString("previewPlayBtn"));
        playButton.setAlignment(Pos.CENTER);
        HBox hBox = new HBox();
        hBox.getChildren().add(playButton);
        hBox.setPadding(new Insets(5,0,0,0));
        hBox.setAlignment(Pos.CENTER);
        
        VBox vBox = new VBox();
        vBox.getChildren().addAll(titleText);
        vBox.setPadding(new Insets(0,0,5,0));
        vBox.setSpacing(4);
        vBox.setAlignment(Pos.CENTER);
        
        String path = "main/thumbnails/" + games.get(0).getClassName() + ".png";
        InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream(path);
        if (imageStream == null){
            //no valid image was found, use predefined
            imageStream = this.getClass().getClassLoader().getResourceAsStream(UNIVERSAL_PATH);
        }
        image = new ImageView(new Image(imageStream));
        image.setPreserveRatio(true);
        image.setFitWidth(120);
        StackPane imgPane = new StackPane();
        imgPane.getChildren().add(image);
        imgPane.setPrefSize(120, 70);
        imgPane.setScaleShape(true);
                
        this.setPadding(new Insets(10,10,10,10));
        this.setTop(vBox);
        this.setCenter(imgPane);
        this.setBottom(hBox);
        
    }
    
    public List<GamePrototype> getLevels(){
        return this.games;
    }
    
    public Button getPlayButton(){
        return playButton;
    }
    
    
    
}
