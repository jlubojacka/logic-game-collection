
package scenecontroller;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import javafx.animation.FadeTransition;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

/*
 * Router for scenes
*/
public class SceneController {
    private static final int ANIM_DURATION = 360;
    private Stage stage;
    private Map<String, Scene> scenes;
    private Deque<Scene> sceneStack;
    private Scene currentScene = null;
    
    public SceneController(Stage stage){
        sceneStack = new ArrayDeque<>();
        scenes = new HashMap<>();
        this.stage = stage;
        
    }
    
    public void addScene(String id, Scene scene){
        scenes.put(id, scene);
    }
    
    public void removeScene(String id){
        scenes.remove(id);
    }
    
    public void setInitScene(String id){
        Scene init = scenes.get(id);
        sceneStack.addFirst(init);
        currentScene = init;
    }
    
    public void goTo(String id){
        if (scenes.containsKey(id)){
            Scene newS = scenes.get(id);
            playAnimation(sceneStack.peekFirst(), newS);
            sceneStack.addFirst(newS);
            currentScene = newS;
        }
    }
    
    public void goTo(String id, Parent root){
        if (scenes.containsKey(id)){
            Scene newS = scenes.get(id);
            newS.setRoot(root);
            playAnimation(sceneStack.peekFirst(), newS);
            sceneStack.addFirst(newS);
            currentScene = newS;
        }
    }
    
    public void goBack(){
        if (sceneStack.size() > 1){
            Scene old = sceneStack.removeFirst();
            currentScene = sceneStack.peekFirst();
            playAnimation(old,currentScene);
        }
    }
    
    public void goBackNoAnim(){
        if (sceneStack.size() > 1){
            sceneStack.removeFirst();
            currentScene = sceneStack.peekFirst();
            sceneStack.addFirst(currentScene);
            stage.setScene(currentScene);
        }
    }
    
    private void playAnimation(Scene oldScene, Scene newScene){
        oldScene.getRoot().setOpacity(0.);
        // Fade out
        FadeTransition ft = new FadeTransition(Duration.millis(ANIM_DURATION),oldScene.getRoot());
        ft.setFromValue(1.);
        ft.setToValue(0.);
        ft.setOnFinished(e1 -> {
            stage.setScene(newScene);
            fadeIn(newScene.getRoot());
        });
        ft.play();
    }
    
    private void fadeIn(Parent root){
        FadeTransition ft = new FadeTransition(Duration.millis(ANIM_DURATION),root);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }
    
    public Scene getCurrentScene(){
        return currentScene;
    }
}
