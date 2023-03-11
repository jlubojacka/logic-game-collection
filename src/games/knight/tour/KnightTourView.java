
package games.knight.tour;

import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.stl.StlMeshImporter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import logic.game.collection.GameView;
import logic.game.collection.LogicGameCollection;
import scenecontroller.SceneController;

public class KnightTourView extends GameView{
    private static final int WORLD_WIDTH = LogicGameCollection.WORLD_WIDTH;
    private static final int WORLD_HEIGHT = LogicGameCollection.WORLD_HEIGHT;
    private double unitSize;
    private KnightTour game;
    private Group root;
    private ResourceBundle texts;
    private SubScene world3d;
    private Group knight;
    private Field [][]chessBoard;
    private Group chessBoardGroup;
    private Group rotableGroup;
    private List<PointLight> lights;
    private PerspectiveCamera camera;
    private TranslateTransition moveTrans = new TranslateTransition(Duration.millis(600));
    private TranslateTransition z1 = new TranslateTransition(Duration.millis(300)); 
    private TranslateTransition z2 = new TranslateTransition(Duration.millis(300));
    private SequentialTransition seqTrans = new SequentialTransition();
    private ParallelTransition parallelTrans = new ParallelTransition();
    private List<Point2D> possibleMoves;
    
    public KnightTourView(KnightTour game,SceneController sc, ResourceBundle bundle) {
        super(sc, bundle);
        this.game = game;
        unitSize = (WORLD_HEIGHT - 300)/ (double)game.getRows();
        init();
    }
    
    private void init(){
        moveTrans = new TranslateTransition();
        Locale defaultLocale = Locale.getDefault();
        texts = ResourceBundle.getBundle("games.bundles.KnightTourBundle",defaultLocale);
        create3D();
        moveTrans.setNode(knight);
        z1.setNode(knight);
        z1.setByZ(-30);
        z2.setNode(knight);
        z2.setByZ(30);
        seqTrans.setInterpolator(Interpolator.LINEAR);
        seqTrans.getChildren().addAll(z1,z2);
        parallelTrans.getChildren().addAll(moveTrans,seqTrans);
        
        this.addGameGui(world3d);
        this.setInstructionsText(texts.getString("instructions"));
        this.setDoYouKnowText(texts.getString("doYouKnow"));
    }
    
    @Override
    public void start(){
        game.restart();
        Point2D initPos = game.getInitKnightPosition();
        knight.setTranslateX(initPos.getX() * unitSize);
        knight.setTranslateY(initPos.getY() * unitSize);
        knight.setTranslateZ(-2.5);
        colorizeChessBoard();
        possibleMoves = game.getPossibleMoves();
    }
    
    private void create3D(){
        root = new Group();
        makeKnight();
        makeChessBoard();
        makeLights();
        rotableGroup = new Group(chessBoardGroup,knight);
        rotableGroup.setRotationAxis(Rotate.Z_AXIS);
        rotableGroup.setRotate(30);
        root.getChildren().addAll(lights);
        root.getChildren().addAll(rotableGroup);
        camera = new PerspectiveCamera(true); //fixedEyeAtCameraZero = true
        camera.setNearClip(10);
        camera.setFarClip(2000.0);
        camera.setTranslateX(120);
        camera.setTranslateY(700);
        camera.setTranslateZ(-300);
        camera.setRotationAxis(Rotate.X_AXIS);
        camera.setRotate(60);
        world3d = new SubScene(root, WORLD_WIDTH, WORLD_HEIGHT, true, SceneAntialiasing.BALANCED); //depthBuffer = true
        world3d.setCamera(camera);
    }
    
    private void makeKnight() {
        PhongMaterial materialK = new PhongMaterial();
        materialK.setDiffuseColor(Color.BURLYWOOD);
        StlMeshImporter stlImporter = new StlMeshImporter();
        MeshView meshV = new MeshView();
        try{
            stlImporter.read(this.getClass().getClassLoader().getResource("games/knight.stl"));
            Mesh m = stlImporter.getImport();
            stlImporter.close();
            meshV = new MeshView(m);
        }catch(ImportException e){
            Logger.getLogger(LogicGameCollection.class.getName())
                    .log(Level.SEVERE,"Could not load knight mesh. ", e);
        }
        Point2D p = game.getInitKnightPosition();
        meshV.setTranslateZ(-2.5);
        meshV.setMaterial(materialK);
        knight = new Group(meshV);
    }

    private void makeLights() {
        PointLight light1 = new PointLight();
        light1.setTranslateX(0);
        light1.setTranslateY(600);
        light1.setTranslateZ(-600);
        lights = new ArrayList<>();
        lights.add(light1);
    }

    private void colorizeChessBoard(){
        int [][]matrix = game.getBoardMatrix();
    
        for (int i=0; i < matrix.length; i++){
            for(int j=0; j < matrix[i].length; j++){
                Field f = chessBoard[i][j];
                if (((i + j)%2) == 0 ) {
                    //dark
                    if (matrix[i][j] == 2){  //barrier
                        f.setMaterial(Color.DARKRED,false);
                    }else if (matrix[i][j] == 1){
                        f.setMaterial(Color.DARKCYAN);
                    }else{
                        f.setMaterial(Color.BLACK,false);
                    }
                }else{
                    //light
                    
                    if (matrix[i][j] == 2){ //barrier
                        f.setMaterial(Color.INDIANRED,true);
                    }else if (matrix[i][j] == 1){
                        f.setMaterial(Color.AQUAMARINE);
                    }else{
                        f.setMaterial(Color.ALICEBLUE,true);
                    }
                }
            }
        }
        
    }
    
    private void makeChessBoard() {
        chessBoardGroup = new Group();
        PhongMaterial materialG = new PhongMaterial();
        materialG.setDiffuseColor(Color.SIENNA);
        double borderSize = game.getRows() * unitSize;
        Box border = new Box(borderSize+10,borderSize+10,10);
        border.setMaterial(materialG);
        border.setTranslateX((borderSize / 2.0) - (unitSize/2));
        border.setTranslateY((borderSize / 2.0) - (unitSize/2));
        border.setTranslateZ(7.5);
        chessBoardGroup.getChildren().add(border);
        
        int [][]matrix = game.getBoardMatrix();
        chessBoard = new Field[game.getRows()][game.getColumns()];
        
        for (int i=0; i < matrix.length; i++){
            for(int j=0; j < matrix[i].length; j++){
                Field f = new Field(unitSize, unitSize, 5);
                f.setPosX(j);
                f.setPosY(i);
                f.setTranslateX(j * unitSize);
                f.setTranslateY(i * unitSize);
                
                f.setOnMouseClicked(e->{
                    testNJump(f);
                });
                chessBoard[i][j] = f;
                chessBoardGroup.getChildren().add(f);
            }
        }
    }
    
    private void testNJump(Field f){
        int i = f.getPosY();
        int j = f.getPosX();
        if (testJump(i,j)){
            moveTrans.setToX(j * unitSize);
            moveTrans.setToY(i * unitSize);
            parallelTrans.play();
            parallelTrans.setOnFinished(e -> {
                finishJump(f);
            });
        }
    }
    
    private boolean testJump(int i,int j){
        Point2D tested = new Point2D(j,i);
        for (Point2D possition : possibleMoves){
            if (possition.equals(tested))
                return true;
        }
        return false;
    }
    
    private void finishJump(Field field){
        //field visited
        game.moveKnight(field.getPosY(), field.getPosX());
        game.incCounter();
        if (field.isLight()){
            field.setMaterial(Color.AQUAMARINE);
        }else {
            field.setMaterial(Color.DARKCYAN);
        }
        testGameOver();
    }
    
    private void testGameOver(){
        possibleMoves = game.getPossibleMoves();
        if (possibleMoves.isEmpty()){
            //filled -> won, else gameOver
            boolean full = game.isFilled();
            if (full){
                this.setEndGameInfo(texts.getString("winTitle"), texts.getString("winText"));
            }else {
                this.setEndGameInfo(texts.getString("gameOverTitle"), texts.getString("gameOverText"));
            }
            showEndGamePane(true);
        }
    }
    
    
}
    

