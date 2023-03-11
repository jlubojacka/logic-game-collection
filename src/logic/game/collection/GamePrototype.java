
package logic.game.collection;

import java.util.ResourceBundle;
import scenecontroller.SceneController;


public interface GamePrototype {
    
    public String getTitle();
    public int getLevel();
    public void setLevel(int number);
    public Category getCategory();
    public GameView createGui(SceneController sceneCntrl, ResourceBundle bundle);
    public String getClassName();
    public void start();
}
