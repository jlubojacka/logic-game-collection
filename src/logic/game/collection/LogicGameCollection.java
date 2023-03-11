
package logic.game.collection;

import deserializer.Deserializer;
import games.blury.Blury;
import games.footpads.Footpads;
import games.frogs.Frogs;
import games.knight.tour.KnightTour;
import games.rotable.memory.RotableMemory;
import games.wall_snail.WallSnail;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import games.magic.square.MagicSquare;
import games.magic.triangle.MagicTriangle;
import games.motherboard.Motherboard;
import games.stack.Stack;
import games.stars.Stars;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.SimpleFormatter;
import javafx.scene.image.Image;
import javafx.scene.transform.Scale;
import scenecontroller.SceneController;


public class LogicGameCollection extends Application {
    private Scene mainScene;
    private List<Scene> categoryScenes;
    private Scene gameScene;
    private Scene creditsScene;
    private static double defaultWindowWidth = 0;
    private static double defaultWindowHeight = 0;
    public static final int WORLD_WIDTH = 1120;
    public static final int WORLD_HEIGHT = 630;
    
    private List<Object> gamesObj;
    private List<GamePrototype> gamesCast;
    private List<Category> availableCatgrs;
    
    private ResourceBundle mainBundle;
    private SceneController sceneCntrl;
    
    private FileHandler fileHandler;
    private Formatter simpleFormatter;
    
    @Override
    public void start(Stage primaryStage) {
        Rectangle2D dimensions = Screen.getPrimary().getBounds();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = bounds.getWidth();
        double screenHeight = bounds.getHeight();
        //double screenWidth = 1150;
        //double screenHeight = 646;
        //System.out.println(screenWidth + " x "+ screenHeight);
        Decorations decorations = new Decorations();
        if (Math.round(dimensions.getHeight()/(dimensions.getWidth()/4)) == 3){ //4:3
            defaultWindowWidth = screenWidth;
            defaultWindowHeight = (screenWidth/16)*9;
        }else {
            defaultWindowHeight = screenHeight - decorations.getTop();
            defaultWindowWidth = (16 * defaultWindowHeight) / 9;
        }
        initialize();
        sceneCntrl = new SceneController(primaryStage);
        
        mainScene = new Scene(new Entrance(mainBundle, sceneCntrl));
        fitToWindow(mainScene.getRoot());
        creditsScene = new Scene(new CreditsView(mainBundle, sceneCntrl));
        fitToWindow(creditsScene.getRoot());
        createCategoryScenes();
        
        sceneCntrl.addScene("main", mainScene);
        for (int i =0; i< categoryScenes.size(); i++){
            sceneCntrl.addScene(availableCatgrs.get(i).name().toLowerCase(), categoryScenes.get(i));
        }
        sceneCntrl.addScene("game", gameScene);
        sceneCntrl.addScene("credits", creditsScene);
        sceneCntrl.setInitScene("main");
        
        setMainSceneDrawers();
        
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        primaryStage.setMaxWidth(defaultWindowWidth);
        primaryStage.setMaxHeight(screenHeight - decorations.getBottom());
        primaryStage.setMinWidth(defaultWindowWidth);
        primaryStage.setMinHeight(screenHeight - decorations.getBottom());
        primaryStage.setTitle(mainBundle.getString("mainTitle"));
        InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream("main/icon.png");
        Image icon = new Image(imageStream);
        primaryStage.getIcons().add(icon);
        primaryStage.show();
        
    }
    
    
    public void initialize(){
        initLogger();
        loadGames();
        castLoadedGames();
        getCategories();
        loadMainLocalization();
    }
    
    private void initLogger(){
        simpleFormatter = new SimpleFormatter();
        try {
            fileHandler = new FileHandler("./errors.log");
            fileHandler.setFormatter(simpleFormatter);
            fileHandler.setLevel(Level.ALL);
            Logger.getLogger(LogicGameCollection.class.getName()).addHandler(fileHandler);
        } catch (IOException | SecurityException ex) {
            Logger.getLogger(LogicGameCollection.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
    
    private void loadGames(){
        Deserializer d = new Deserializer();
        d.registerClass(WallSnail.class);
        d.registerClass(KnightTour.class);
        d.registerClass(MagicSquare.class);
        d.registerClass(RotableMemory.class);
        d.registerClass(Frogs.class);
        d.registerClass(Stars.class);
        d.registerClass(Footpads.class);
        d.registerClass(Motherboard.class);
        d.registerClass(MagicTriangle.class);
        d.registerClass(Stack.class);
        d.registerClass(Blury.class);
        
        try {
            gamesObj = d.deserialize();
            
        } catch (Exception ex) {
            gamesObj = new ArrayList();
            ex.printStackTrace();
            Logger.getLogger(LogicGameCollection.class.getName())
                    .log(Level.SEVERE, "Deserialization error. {0}", ex.getMessage());
        }
        
    }
    
    private void castLoadedGames(){
        gamesCast = gamesObj.stream()
                .map(a -> GamePrototype.class.cast(a))
                .collect(Collectors.toList());
    }
    
    private void loadMainLocalization(){
        Locale defaultLocale = Locale.getDefault();
        mainBundle = ResourceBundle.getBundle("main.MainBundle",defaultLocale);
        
    }
    
    private void getCategories(){
        availableCatgrs = new ArrayList<>();
        for (GamePrototype g : gamesCast){
            Category name = g.getCategory();
            if (!availableCatgrs.contains(name)){
                availableCatgrs.add(name);
            }
        }
    }
    
    private void createCategoryScenes(){
        categoryScenes = new ArrayList<>();
        
        for (Category c : availableCatgrs){
            List<GamePrototype> categoryGames = sortByCategory(c);
            CategoryView catView = new CategoryView(c, categoryGames, mainBundle, sceneCntrl);
            Scene scene = new Scene(catView,defaultWindowWidth, defaultWindowHeight);
            scene.setFill(Color.TRANSPARENT);
            categoryScenes.add(scene);
            fitToWindow(scene.getRoot());
        }
    }
    
    private List<GamePrototype> sortByCategory(Category category){
        List<GamePrototype> sortedGames = gamesCast.stream()
                .filter(a -> a.getCategory().equals(category))
                .collect(Collectors.toList());
        return sortedGames;
    }
    
    public static void fitToWindow(Parent root){
        double sx = (defaultWindowWidth - 1)/WORLD_WIDTH;
        double sy = (defaultWindowHeight - 1)/WORLD_HEIGHT;
        root.getTransforms().add(new Scale(sx,sy));
        
    }
    
    private void setMainSceneDrawers(){
        // according to availableCatgrs unlock drawers and set names, which drawer is category
        if (mainScene.getRoot() instanceof Entrance){
            Entrance entrance = (Entrance) mainScene.getRoot();
            Drawer []drawers = entrance.getDrawers();
            if (availableCatgrs.size() <= drawers.length){
                for (int i = 0; i < availableCatgrs.size(); i++){
                    String key = availableCatgrs.get(i).name().toLowerCase() + "Category";
                    drawers[i].setCategory(availableCatgrs.get(i));
                    if (i == 1){
                        drawers[i].setLabelText(mainBundle.getString(key), -3);
                    }else {
                        drawers[i].setLabelText(mainBundle.getString(key));
                    }
                    drawers[i].setLocked(false);
                    drawers[i].setSceneController(sceneCntrl);
                }
            }
        }
    }
    
    
    public static double getDefaultWindowWidth(){
        return defaultWindowWidth;
    }
    
    public static double getDefaultWindowHeight(){
        return defaultWindowHeight;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
