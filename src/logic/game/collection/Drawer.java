
package logic.game.collection;

import java.net.URL;
import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import scenecontroller.SceneController;


public class Drawer extends Group{
    private Category category;
    private ImageView drawer;
    private ImageView label;
    private Label labelText;
    private boolean opened = false;
    private boolean locked = true;   //no content
    private TranslateTransition openDrawer;
    private TranslateTransition shutDrawer;
    private SceneController sceneCntrl;
    
    public Drawer(ImageView drawer, double layoutX, double layoutY, double scaleX, double scaleY){
        super(drawer);
        this.drawer = drawer;
        openDrawer= new TranslateTransition(Duration.millis(250));
        shutDrawer = new TranslateTransition(Duration.millis(250));
        openDrawer.setNode(this);
        shutDrawer.setNode(this);
        openDrawer.setByY(30);
        shutDrawer.setByY(-30);
        openDrawer.setOnFinished(e->{
            opened = true;
        });
        shutDrawer.setOnFinished(e->{
            opened = false;
        });
        
        this.setLayoutX(layoutX);
        this.setLayoutY(layoutY);
        this.setScaleX(scaleX);
        this.setScaleY(scaleY);
        
        loadCSS();
        setMouseHover();
        setMouseClicked();
    }
    
    public Drawer(ImageView drawer, double layoutX, double layoutY){
        super(drawer);
        this.drawer = drawer;
        openDrawer= new TranslateTransition(Duration.millis(150));
        shutDrawer = new TranslateTransition(Duration.millis(150));
        openDrawer.setNode(this);
        shutDrawer.setNode(this);
        openDrawer.setByY(30);
        shutDrawer.setByY(-30);
        openDrawer.setOnFinished(e->{
            opened = true;
        });
        shutDrawer.setOnFinished(e->{
            opened = false;
        });

        this.setLayoutX(layoutX);
        this.setLayoutY(layoutY);
        loadCSS();
        setMouseHover();
        setMouseClicked();
    }
    
    private void loadCSS(){
        URL path = this.getClass().getClassLoader().getResource("css/DrawerCSS.css");
        this.getStylesheets().add(path.toExternalForm());
    }
    
    private void setMouseHover(){
        this.setOnMouseEntered(e->{
            if ((!locked) && (!opened)){
                openDrawer.play();
            }
        });
        this.setOnMouseExited(e->{
            if ((!locked) && opened){
                shutDrawer.play();
            }
        });
        
    }
    
    private void setMouseClicked(){
        this.setOnMouseClicked((MouseEvent e) -> {
            if (e.getClickCount() == 1){
                if ((sceneCntrl != null)&&(!locked)){
                    sceneCntrl.goTo(category.name().toLowerCase());
                }
            }
        }); 
    }
    
    public void setLabel(ImageView labelImage, double deltaX, double deltaY){
        createLabel(labelImage, deltaX, deltaY);
    }
    
    public void setLabel(ImageView labelImage){
        createLabel(labelImage, 0, 0);
    }
    
    private void createLabel(ImageView labelImage, double deltaX, double deltaY){
        label = labelImage;
        Bounds bounds = this.getBoundsInParent();
        label.setLayoutX(bounds.getWidth()/2 - (label.getFitWidth()/2) + deltaX);
        label.setLayoutY(47 + deltaY);
        this.getChildren().add(label);
        setLabelVisible(false);
    }
    
    public void setLabelText(String text){
        createLabelText(text, 0);
    }

    public void setLabelText(String text, double deltaY){
        createLabelText(text, deltaY);
    }
    
    private void createLabelText(String text, double deltaY){
        labelText = new Label(text);
        labelText.getStyleClass().add("drawer-label");
        StackPane labelLayer = new StackPane();
        labelLayer.setPrefSize(drawer.getFitWidth(), labelText.getHeight());
        labelLayer.setBackground(Background.EMPTY);
        labelLayer.getChildren().add(labelText);
        StackPane.setAlignment(labelText, Pos.CENTER);
        labelLayer.setLayoutY(58 + deltaY);
        this.getChildren().add(labelLayer);
        setLabelVisible(true);
    }
    
    public void setLocked(boolean locked){
        this.locked = locked;
    }
    
    public String getLabelText(){
        return labelText.getText();
    }
    
    public boolean isLocked(){
        return locked;
    }
    
    public void setLabelVisible(boolean visible){
        label.setVisible(visible);
    }
    
    public boolean isLabelVisible(){
        return label.isVisible();
    }
    
    public void setCategory(Category category){
        this.category = category;
    }
    
    public void setSceneController(SceneController sc){
        this.sceneCntrl = sc;
    }

    private double getTextWidth(Text txt){
        new Scene(new Group(txt));  //auxiliary scene, computing css needs a real node in real scene
        txt.applyCss();
        double textWidth = txt.getLayoutBounds().getWidth();
        return textWidth;
    }
}
