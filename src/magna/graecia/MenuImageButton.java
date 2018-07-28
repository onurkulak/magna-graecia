/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package magna.graecia;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author onur
 */
public class MenuImageButton extends Button{
    
    private final static int menuButtonsSize = 48;
    public MenuImageButton(String url){
        super("",new ImageView(new Image(url, menuButtonsSize, menuButtonsSize, false, true)));
    }
}
