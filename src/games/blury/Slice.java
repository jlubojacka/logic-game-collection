/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package games.blury;

import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Slice extends ImageView{
    private boolean sharp;
    private GaussianBlur blur;
    
    public Slice(Image image){
        this.setImage(image);
        sharp = true;
        blur = new GaussianBlur(35);
    }
    
    public void flipSide(){
        if (sharp){
            this.setEffect(blur);
        }else {
            this.setEffect(null);
        }
        sharp = !sharp;
    }
    
    public void setBlury(boolean value){
        if (value){
            this.setEffect(blur);
            sharp = false;
        }else {
            this.setEffect(null);
            sharp = true;
        }
    }
    
    public boolean isSharp(){
        return sharp;
    }
}
