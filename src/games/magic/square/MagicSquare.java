
package games.magic.square;

import deserializer.Deserialize;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import logic.game.collection.Category;
import logic.game.collection.GamePrototype;
import logic.game.collection.GameView;
import logic.game.collection.LogicGameCollection;
import scenecontroller.SceneController;

@Deserialize(as = "MagicSquare")
public class MagicSquare implements GamePrototype{
    private MagicSquareView view;
    private static final int INIT_SIZE = 4;
    private int size;
    private static final Category CATEGORY = Category.MATH;
    private String title;
    private int level = 0;
    private int initSum = 0;
    private int[][]boardMatrix;
    private static final Logger LOGGER = Logger.getLogger(LogicGameCollection.class.getName());
    
    public MagicSquare(){
        Locale defaultLocale = Locale.getDefault();
        ResourceBundle texts = ResourceBundle.getBundle("games.bundles.MagicSquareBundle",defaultLocale); 
        title = texts.getString("title");
        boardMatrix = new int[INIT_SIZE][INIT_SIZE];
    }
    
    public int getSize(){
        return size;
    }
    
    @Deserialize(as = "size")
    public void setSize(int size){
        this.size = size;
        changeBoardSize();
    }
    
    @Deserialize(as = "row")
    public void fillRow(String rowContent){
        try {
            String []input = rowContent.trim().split("(\\s*)-(\\s*)");
            if (input.length != 2){
                throw new IllegalRowException();
            }
            int index = Integer.parseInt(input[0]);
            List<Integer> numbers = parseInputRow(input[1]);
            writeToMatrix(index,numbers);
        }catch(IllegalRowException ex){
            LOGGER.log(Level.SEVERE, ex.getMessage());
        }
    }
    
    private void writeToMatrix(int index, List<Integer> numbers){
        if (index >= 0 && index < size){
            for (int j = 0; j < size; j++){
                boardMatrix[index][j] = numbers.get(j);
            }
        }else LOGGER.log(Level.WARNING, "Row index out of range, values ignored.");
    }
    
    private List<Integer> parseInputRow(String row) throws IllegalRowException{
        String []parts = row.split("(\\s*),+(\\s*)");
        if (parts.length != size){
            throw new IllegalRowException();
        }
        List<Integer> numbers = new ArrayList<>();
        for (String p : parts){
            int number = Integer.parseInt(p);
            numbers.add(number);
        }
        return numbers;
    }
    
    private void changeBoardSize(){
        if (size != INIT_SIZE){
            boardMatrix = new int[size][size];
        }
    }
    
    private void saveSum(){
        int sum = 0;
        for (int j=0; j < size; j++){
            sum += boardMatrix[0][j]; 
        }
        initSum = sum;
    }
    
    public boolean testSum(){
        if (!testRowSum())
            return false;
        if (!testCollumnSum())
            return false;
        return testDiagonalSum();
    }
    
    private boolean testRowSum(){
        int sum;
        for (int i=0; i < size; i++){
            sum = 0;
            for (int j=0; j < size; j++){
                sum += boardMatrix[i][j];
            }
            if (sum != initSum)
                return false;
        }
        return true;
    }
    
    private boolean testCollumnSum(){
        int sum;
        for (int i=0; i < size; i++){
            sum = 0;
            for (int j=0; j < size; j++){
                sum += boardMatrix[j][i];
            }
            if (sum != initSum)
                return false;
        }
        return true;
    }
    
    private boolean testDiagonalSum(){
        int sum = 0;
        for (int i=0; i < size; i++){
            sum += boardMatrix[i][i];
        }
        if (sum != initSum)
            return false;
        int k = 0;
        sum = 0;
        for (int j = size - 1; j >=0; j--){
            sum += boardMatrix[k][j];
            k++;
        }
        if (sum != initSum)
            return false;
        
        return true;
    }
    
    public void shuffle(){
        Random r = new Random();
        int k, l, temp;
        for (int i = boardMatrix.length - 1; i > 0; i--){
            for (int j = boardMatrix[i].length - 1; j > 0; j--){
                k = r.nextInt(i + 1);
                l = r.nextInt( j + 1);
                temp = boardMatrix[k][l];
                boardMatrix[k][l] = boardMatrix[i][j];
                boardMatrix[i][j] = temp;
            }
        }
    }
    
    public int[][] getBoardMatrix(){
        return boardMatrix;
    }
    
    public int getInitSum(){
        return initSum;
    }
    
    public void setSquare(int i, int j, int value){
        boardMatrix[i][j] = value;
    }

    @Override
    public String getTitle() {
        return title;
    }
    
    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public Category getCategory() {
        return CATEGORY;
    }
    
    @Deserialize(as = "level")
    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public GameView createGui(SceneController sceneCntrl, ResourceBundle bundle) {
        view = new MagicSquareView(this,sceneCntrl, bundle);
        return view;
    }

    @Override
    public String getClassName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void start() {
        saveSum();
        view.start();
    }
    
}
