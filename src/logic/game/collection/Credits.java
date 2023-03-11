
package logic.game.collection;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Credits extends CreditsCard{
    
    public Credits(){
        super("left-paper.png","CreditsBundle");
        
    }
    
    @Override
    protected Parent createContent(){
        
        Text appTitle = new Text(bundle.getString("appTitle"));
        appTitle.getStyleClass().add("title");
        Text credits = new Text(bundle.getString("credits"));
        credits.setWrappingWidth(this.getPrefWidth() - 140);
        credits.setTextAlignment(TextAlignment.JUSTIFY);
        credits.getStyleClass().add("pane-text");
        Text footer = new Text(bundle.getString("footer"));
        footer.setWrappingWidth(this.getPrefWidth() - 140);
        footer.setTextAlignment(TextAlignment.CENTER);
        footer.getStyleClass().add("pane-text");
        VBox content = new VBox(25);
        content.setAlignment(Pos.CENTER);
        content.getChildren().addAll(appTitle, credits, footer);
        content.setPrefSize(this.getPrefWidth(), this.getPrefHeight() - 70);
        
        return content;
    }
    
}
