
package games.magic.square;

public class IllegalRowException extends Exception{
    
    public IllegalRowException(){
        super("Incorrect row specification in xml file.");
    }
    
}
