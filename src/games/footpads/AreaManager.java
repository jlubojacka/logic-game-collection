
package games.footpads;

import java.util.ArrayList;
import java.util.List;


public class AreaManager {
    private List<Item> leftArea;
    private List<Item> rightArea;
    private List<Item> topArea;
     
    public AreaManager(List<Item> itemsAll){
        leftArea = new ArrayList<>();
        rightArea = new ArrayList<>();
        topArea = itemsAll;
    }
    
    public void moveLeft(Item i){
        if (!leftArea.contains(i)){
            //determine where i belongs and move it
            if (topArea.contains(i)){
                topArea.remove(i);
            }else if (rightArea.contains(i)){
                rightArea.remove(i);
            }
            leftArea.add(i);
        }
    }
        
    public void moveRight(Item i){
        if (!rightArea.contains(i)){
            //determine where i belongs and move it
            if (topArea.contains(i)){
                topArea.remove(i);
                
            }else if (leftArea.contains(i)){
                leftArea.remove(i);
            }
            rightArea.add(i);
        }
    }
    
    public void moveUp(Item i){
        if (!topArea.contains(i)){
            if (leftArea.contains(i)){
                leftArea.remove(i);
            }else if (rightArea.contains(i)){
                rightArea.remove(i);
            }
            topArea.add(i);
        }
    }
    
    public void restart(){
        topArea.addAll(leftArea);
        topArea.addAll(rightArea);
        leftArea.clear();
        rightArea.clear();
    }
    
    public int getLeftSum(){
        return leftArea.stream()
                       .mapToInt(Item::getWeight)
                       .sum();
    }
    
    public int getRightSum(){
        return rightArea.stream()
                       .mapToInt(Item::getWeight)
                       .sum();
    }
    
    public boolean isTopEmpty(){
        return topArea.isEmpty();
    }
}
