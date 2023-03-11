
package games.footpads;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import logic.game.collection.LogicGameCollection;

public class Scroll extends Group{
    private List<ItemDescription> distinctItems;
    private List<ImageView> images;
    private static final int WORLD_WIDTH = LogicGameCollection.WORLD_WIDTH;
    private static final int WIDTH = 370;
    private double imgHeight;
    private static final int PART = 30;
    private static final int VGAP = 18;
    private static final int HGAP = 20;
    private static final int TOP_MARGIN = 10;
    private static final int LEFT_MARGIN = 20;
    private TranslateTransition close;
    private TranslateTransition open;
    private static final double UNIT_SIZE = 50;
    private double unitSizeChange = UNIT_SIZE;
    private double columns;
    private ImageView paper;
    private int rows = 0;
    private List<Group> rowsContent;
    private Pane main;
    private double diff = 0;
    
    public Scroll(List<ItemDescription> descriptions){
        InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream("games/scroll.png");
        Image img = new Image(imageStream);
        paper = new ImageView(img);
        paper.setPreserveRatio(true);
        paper.setFitWidth(WIDTH);
        this.getChildren().add(paper);
        imgHeight = img.getHeight()/img.getWidth()*WIDTH;
        
        distinctItems = descriptions.stream().distinct().collect(Collectors.toList());
        images = new ArrayList<>();
        ColorAdjust adjust = new ColorAdjust();
        adjust.setContrast(0.15);
        adjust.setBrightness(-0.35);
        adjust.setSaturation(-0.15);
        
        for (ItemDescription d : distinctItems){
            String path = "games/" + d.getImgName();
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream(path);
            if (stream != null) {
                ImageView imgView = new ImageView(new Image(stream));
                imgView.setPreserveRatio(true);
                if (imgView.getImage().getHeight() > imgView.getImage().getWidth()){
                    imgView.setFitHeight(UNIT_SIZE);
                }else{ 
                    imgView.setFitWidth(UNIT_SIZE);
                }
                imgView.setEffect(adjust);
                images.add(imgView);
            }
            
        }
        main = new Pane();
        rowsContent = new ArrayList<>();
        fillScroll();
        placeContent();
        setMouseHandlers();
    }
    
    
    private void placeContent(){
        double mainHeight = getContentHeight();
        if (mainHeight > imgHeight){
            //more items than expected, decrease unitSizeChange -> smaller thumbnails
        }else{
            diff = imgHeight - mainHeight;
            main.setTranslateY(diff);
        }
        this.getChildren().add(main);
    }
    
    private void fillScroll(){
        double posX ;
        double posY = TOP_MARGIN;
        int i = 0;
        while (i < images.size()){
            posX = LEFT_MARGIN;
            Group g = new Group();
            while(posX <= (WIDTH - LEFT_MARGIN - unitSizeChange - 15)){
                if (i >= images.size())
                    break;
                HBox h = createBox(images.get(i), distinctItems.get(i).getWeight());
                h.setTranslateX(posX);
                h.setTranslateY(posY);
                posX += (getImgWidth(images.get(i)) + 15 + HGAP);
                g.getChildren().add(h);
                i++;
            }
            posY += getGroupHeight(g);
            posY += VGAP;
            rowsContent.add(g);
            rows++;
        }
        main.getChildren().addAll(rowsContent);
    }
    
    private HBox createBox(ImageView img, int weight){
        HBox h = new HBox(5);
        h.setAlignment(Pos.CENTER);
        h.getChildren().add(img);
        Text t = new Text(String.valueOf(weight));
        t.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        h.getChildren().add(t);
        return h;
    }
    
    
    private void setMouseHandlers(){
        this.setTranslateX(WORLD_WIDTH/2.0 - (WIDTH/2.0));
        this.setTranslateY(PART - imgHeight);
        
        open = new TranslateTransition(Duration.millis(350),this);
        open.setToY(-diff);
        close = new TranslateTransition(Duration.millis(350), this);
        close.setToY(PART - imgHeight);
        
        this.setOnMouseClicked((MouseEvent e)-> {
            if (this.getTranslateY() == (PART - imgHeight)){
                open.play();
            }else {
                close.play();
            }
        });
    }
    
    private void changeUnitSize(){
        unitSizeChange -= 5;
        for (ImageView imgView : images){
            if (imgView.getImage().getHeight() > imgView.getImage().getWidth()){
                imgView.setFitHeight(unitSizeChange);
            }else{ 
                imgView.setFitWidth(unitSizeChange);
            }
        }
    }
    
    private double getContentHeight(){
        double sum = TOP_MARGIN;
        for (Group g : rowsContent){
            sum+= getGroupHeight(g);
            sum+= VGAP;
        }
        return sum;
    }
    
    private double getGroupHeight(Group g){
        double max = g.getChildren().stream()
                    .map(node -> HBox.class.cast(node))
                    .map(hbox -> hbox.getChildren().get(0))
                    .map(node -> ImageView.class.cast(node))
                    .map(i -> getImgHeight(i))
                    .max(Comparator.naturalOrder())
                    .orElse(0.0);
        return max;
    }
    
    private double getImgHeight(ImageView img){
        if (img.getFitHeight() != 0){
            return img.getFitHeight();
        }
        double h = img.getImage().getHeight()/img.getImage().getWidth()*img.getFitWidth();
        return h;
    }
    
    private double getImgWidth(ImageView img){
        if (img.getFitWidth() != 0){
            return img.getFitWidth();
        }
        double w = img.getImage().getWidth()/img.getImage().getHeight()*img.getFitHeight();
        return w;
    }
}
