/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package games.blury;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;

public class CircleCruncher {
    private int slices;
    private int sliceSize;
    private Image[] parts;
    
    public CircleCruncher(int slices, int sliceSize){
        this.slices = slices;
        this.sliceSize = sliceSize;
    }
    
    public List<Slice> getSliced(Image original){
        parts = new Image[slices];
        splitImage(slices, original);
        double pivotX = sliceSize;
        double pivotY = 0;
        List<Slice> images = new ArrayList<>();
        int angle = 360/slices;
        for (int i=0; i < slices; i++){
            Slice img = new Slice(parts[i]);
            img.setFitWidth(sliceSize);
            img.setPreserveRatio(true);
            if (i > 0)
                img.getTransforms().add(new Rotate(i*angle,pivotX, pivotY));            
            Arc clip = createClip(angle, img);
            img.setClip(clip);
            images.add(img);
        }
        return images;
    }
    
    private void splitImage(int slices, Image original){
        int angle = 360/slices;
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Circle aux = new Circle(300);
        aux.setFill(new ImagePattern(original));
        int i = 0;
        do{
            aux.setRotate(i*angle);
            Image img = aux.snapshot(params, null);
            parts[i] = SwingFXUtils.toFXImage(getPart(angle, img),null);
            //parts[i] = img;
            i++;
        } while(i < slices);
    }
    
    private BufferedImage getPart(int angle, Image original){
        BufferedImage buff = SwingFXUtils.fromFXImage(original, null);
        int y = (int)(original.getWidth()/2.0);
        double alpha =  angle * (Math.PI/180);
        double sin = Math.sin(alpha/2.0);
        int t = (int)(original.getWidth()*sin);
        
        double phi = 90 - ((180 - angle)/2.0);
        double alpha2 = 180 - 90 - phi;
        double alpharad = alpha2 * (Math.PI/180);
        int height = (int)(Math.sin(alpharad) * t);
        int width = (int)(original.getWidth()/2.0);
        BufferedImage part = buff.getSubimage(0, y, width, height);
        
        return part;
    }
    
    private Arc createClip(int angle, ImageView part){
        double center = part.getFitWidth();
        Arc a = new Arc(center,0,center,center,180, angle);
        a.setType(ArcType.ROUND);
        return a;
    }
    
    private Image loadImage(String fileName){
        InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream("games/" + fileName);
        return new Image(imageStream);
    }
    
}
