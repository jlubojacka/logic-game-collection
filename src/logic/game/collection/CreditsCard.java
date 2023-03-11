
package logic.game.collection;

import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public abstract class CreditsCard extends Pane{
    private static final int PAPER_WIDTH = 670;
    private static final int PAPER_HEIGHT = 465;
    private ImageView bg;
    protected ResourceBundle bundle;
    protected StackPane cardTitle;
    protected StackPane contentLayer;
    
    public CreditsCard(String imgPath, String bundleName){
        loadCSS();
        loadLocalization(bundleName);
        init(imgPath);
        
    }
    
    private void init(String imgPath){
        InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream("main/" + imgPath);
        bg = new ImageView(new Image(imageStream));
        bg.setFitWidth(PAPER_WIDTH);
        bg.setFitHeight(PAPER_HEIGHT);
        this.setPrefSize(PAPER_WIDTH, PAPER_HEIGHT);
        createGUI();
        this.getChildren().addAll(bg, contentLayer, cardTitle);
    
    }
    
    private void loadCSS(){
        URL path = this.getClass().getClassLoader().getResource("css/CreditsViewCSS.css");
        this.getStylesheets().add(path.toExternalForm());
    }
    
    private void loadLocalization(String fileName){
        Locale defaultLocale = Locale.getDefault();
        bundle = ResourceBundle.getBundle("main." + fileName,defaultLocale);
    }
    
    private void createGUI(){
        Text cardText = new Text(bundle.getString("cardTitle"));
        double halfWidth = this.getPrefWidth()/2.0;
        cardTitle = new StackPane();
        cardTitle.setPrefSize(halfWidth, 45);
        cardTitle.setLayoutY(10);
        cardTitle.getChildren().add(cardText);
        cardTitle.getStyleClass().addAll("pane-text","card-title");
        
        contentLayer = new StackPane();
        contentLayer.getChildren().add(createContent());
        contentLayer.setLayoutY(50);
        contentLayer.setPrefSize(this.getPrefWidth(), this.getPrefHeight() - 70);
    }
    
    protected abstract Parent createContent();
    
    public StackPane getTitle(){
        return cardTitle;
    }
    
}
