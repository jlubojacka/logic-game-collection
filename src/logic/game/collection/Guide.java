
package logic.game.collection;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class Guide extends CreditsCard{
    private VBox content;
    private ImageView buttonLeft;
    private ImageView buttonRight;
    private List<Image> images;
    private ImageView currentImg;
    private Text currentText;
    private List<String> texts;
    private ColorAdjust adjust1;
    private ColorAdjust adjust2;
    
    public Guide(){
        super("right-paper.png", "GuideBundle");
        cardTitle.setLayoutX(this.getPrefWidth()/2.0);
    }
    
    @Override
    protected Parent createContent(){
        loadImages();
        currentImg = new ImageView(images.get(0));
        currentImg.setPreserveRatio(true);
        currentImg.setFitWidth(350);
        
        loadButtons();
        buttonLeft.setVisible(false);
        
        loadTexts();
        currentText = new Text(texts.get(0));
        currentText.getStyleClass().add("pane-text");
        currentText.setWrappingWidth(this.getPrefWidth() - 120);
        currentText.setTextAlignment(TextAlignment.JUSTIFY);
        
        HBox slide = new HBox(25);
        slide.setAlignment(Pos.CENTER);
        slide.getChildren().addAll(buttonLeft, currentImg, buttonRight);
        content = new VBox(15);
        StackPane.setMargin(content, new Insets(35,0,0,0));
        content.setAlignment(Pos.TOP_CENTER);
        content.getChildren().addAll(slide, currentText);
        return content;
    }
    
    private void loadImages(){
        images = new ArrayList<>();
        String extension;
        for (int i = 1; i <=4; i++){
            if ((i == 1) || (i == 4)){
                extension = ".gif";
            } else {
                extension = ".jpg";
            }
            InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream("main/guide/img"+i+extension);
            Image img = new Image(imageStream);
            images.add(img);
        }
        
    }
    
    private void loadButtons(){
        adjust1 = new ColorAdjust();
        adjust2 = new ColorAdjust();
        InputStream imageStream1 = this.getClass().getClassLoader().getResourceAsStream("main/guide/btn_next.png");
        buttonRight = new ImageView(new Image(imageStream1));
        buttonRight.setPreserveRatio(true);
        buttonRight.setFitWidth(55);
        InputStream imageStream2 = this.getClass().getClassLoader().getResourceAsStream("main/guide/btn_prev.png");
        buttonLeft = new ImageView(new Image(imageStream2));
        buttonLeft.setPreserveRatio(true);
        buttonLeft.setFitWidth(55);
        
        buttonLeft.setPickOnBounds(true);
        buttonRight.setPickOnBounds(true);
        
        buttonLeft.setEffect(adjust1);
        buttonRight.setEffect(adjust2);
        
        addMouseHandlers();
    }
    
    private void addMouseHandlers(){
        buttonLeft.setOnMouseEntered(e->{
            adjust1.setBrightness(-1);
        });
        buttonLeft.setOnMouseExited(e->{
            adjust1.setBrightness(0.1);
        });
        buttonRight.setOnMouseEntered(e->{
            adjust2.setBrightness(-1);
        });
        buttonRight.setOnMouseExited(e->{
            adjust2.setBrightness(0.1);
        });
        
        buttonLeft.setOnMouseClicked((MouseEvent e) -> {
            int index = texts.indexOf(currentText.getText());
            int newIndex = index - 1;
            if (index == (texts.size() - 1)){
                buttonRight.setVisible(true);
            }
            if (newIndex == 0){
                buttonLeft.setVisible(false);
            }
            if (newIndex >= 0){
                currentText.setText(texts.get(newIndex));
                currentImg.setImage(images.get(newIndex));
            }
        });
        
        buttonRight.setOnMouseClicked((MouseEvent e) -> {
            int index = texts.indexOf(currentText.getText());
            int newIndex = index + 1;
            if (index == 0){
                buttonLeft.setVisible(true);
            }
            if (newIndex == (texts.size() - 1)){
                buttonRight.setVisible(false);
            }
            if (newIndex < texts.size()){
                currentText.setText(texts.get(newIndex));
                currentImg.setImage(images.get(newIndex));
            }
        });
    }
    
    private void loadTexts(){
        texts = new ArrayList();
        for (int i=1; i <= 4; i++){
            texts.add(bundle.getString("text"+i));
        }
    }
    
}
