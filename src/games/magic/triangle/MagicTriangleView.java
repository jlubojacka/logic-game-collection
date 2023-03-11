
package games.magic.triangle;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.util.Pair;
import logic.game.collection.GameView;
import logic.game.collection.LogicGameCollection;
import scenecontroller.SceneController;

public class MagicTriangleView extends GameView{
    private MagicTriangle game;
    private static final int WORLD_WIDTH = LogicGameCollection.WORLD_WIDTH;
    private static final int WORLD_HEIGHT = LogicGameCollection.WORLD_HEIGHT;
    private int unitSize;
    private ResourceBundle texts;
    private Pane gameBoard;
    private static final int OFFSET = 70;
    private List<Token> tokens;
    private List<Triangle> triangles;
    private List<Integer> numbers;
    private Token selected;
    private ImageView background;

    public MagicTriangleView(MagicTriangle game, SceneController sc, ResourceBundle bundle) {
        super(sc, bundle);
        this.game = game;
        unitSize = (WORLD_HEIGHT - BOTTOM_PANE_HEIGHT - OFFSET - 20)/ game.getRowsCount();
        Locale defaultLocale = Locale.getDefault();
        texts = ResourceBundle.getBundle("games.bundles.MagicTriangleBundle",defaultLocale);
        init();
    }
    
    private void init(){
        gameBoard = new Pane();
        tokens = new ArrayList<>();
        triangles = new ArrayList<>();
        createTriangles();
        processNumbers();
        sortTokens();
        shuffleNumbers();
        
        InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream("games/arithmeticae.png");
        background = new ImageView(new Image(imageStream));
        background.setPreserveRatio(true);
        background.setFitWidth(WORLD_WIDTH);
        background.setOpacity(0.85);
        
        this.addGameGui(background);
        this.addGameGui(triangles);
        this.addGameGui(tokens);
        this.setInstructionsText(texts.getString("instructions"));
        this.setDoYouKnowText(texts.getString("doYouKnow"));
    }

    private void createTriangles(){
        List<List<Integer>> rows = game.getRows();
        double sideHalf = unitSize/Math.sqrt(3);
        double rCircumcircle = (2*unitSize)/3;
        double rIncircle = unitSize/3;
        double startX = WORLD_WIDTH / 2.0;
        double startY = OFFSET + 30 + rCircumcircle; // offset + distance to center
        for (List<Integer> row : rows){
            for (int i=0; i < row.size(); i++){
                Triangle t = new Triangle(unitSize, row.get(i));
                if ((i % 2) == 0){ 
                    t.setCenterX(startX + i * sideHalf);
                    t.setCenterY(startY);
                    Token token = new Token(sideHalf, startX + i * sideHalf, startY - rCircumcircle);
                    tokens.add(token);
                    setTokenHandler(token);
                }else {
                    t.setCenterX(startX + (i * sideHalf));
                    t.setCenterY(startY - rIncircle);
                    t.rotate();
                }
                triangles.add(t);
            }
            startY += unitSize;
            startX -= sideHalf;
        }
        //base tokens
        int toCreate = game.getRequestedNumCount() - tokens.size();
        for (int i=0; i < toCreate; i++){
            Token t = new Token(sideHalf, startX + i * (sideHalf * 2), startY - rCircumcircle);
            setTokenHandler(t);
            tokens.add(t);
        }
    }
    
    private void setTokenHandler(Token t){
        t.setOnMouseClicked((MouseEvent e) -> {
            if (selected == null){
                t.highlightOn(true);
                selected = t;
            }else {
                if (selected != t){
                    swap(selected, t);
                    selected.highlightOn(false);
                    selected = null;
                    testEnd();
                }
            }
        });
        
    }
    
    private void swap(Token t1, Token t2){
        int tmp = t1.getNumber();
        t1.setNumber(t2.getNumber());
        t2.setNumber(tmp);
    }
    
    private void testEnd(){
        boolean correctSums = true;
        for (Triangle t : triangles){
            if (!t.isSumCorrect()){
                correctSums = false;
            }
        }
        
        if (correctSums){
            this.setEndGameInfo(true);
            this.showEndGamePane(true);
        }
    }
    
    private void sortTokens(){
        for (Triangle t : triangles){
            Point2D a = t.getA();
            Point2D b = t.getB();
            Point2D c = t.getC();
            Token aToken = findToken(a);
            if (aToken != null){
                t.addToken(aToken);
            }
            Token bToken = findToken(b);
            if (bToken != null){
                t.addToken(bToken);
            }
            Token cToken = findToken(c);
            if (cToken != null){
                t.addToken(cToken);
            }
        } 
    }
    
    private Token findToken(Point2D trianglePoint){
        int tollerance = 5;
        for (Token t : tokens){
            int centerX = (int) t.getCenterX();
            double centerY = (int) t.getCenterY();
            if (centerX == (int)trianglePoint.getX()){
                
                if ((trianglePoint.getY() < (centerY + tollerance))
                        && (trianglePoint.getY() > (centerY - tollerance))){
                    return t;
                }
            }
        }
        return null;
    }
    
    private void processNumbers(){
        numbers = new ArrayList<>();
        for (Pair<Integer, Integer> p : game.getNumbers()){
            for (int i=0; i < p.getValue(); i++){
                numbers.add(p.getKey());
            }
        }
        
        if (numbers.size() < tokens.size()){
            int difference = tokens.size() - numbers.size();
            for (int i=0; i < difference; i++){
                numbers.add(0);
            }
        }
    }
    
    private void shuffleNumbers(){
        Collections.shuffle(numbers);
        for (int i=0; i < tokens.size(); i++){
            tokens.get(i).setNumber(numbers.get(i));   
        }
    }
    
    @Override
    public void start() {
        if (selected != null){
            selected.highlightOn(false);
        }
        selected = null;
        shuffleNumbers();
    }
    
}
