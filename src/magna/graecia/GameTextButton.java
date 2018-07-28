/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package magna.graecia;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.text.Font;

/**
 *
 * @author onur
 */
public class GameTextButton extends Button{
    public GameTextButton(String text){
        super(text);
        doStyle();
    }

    private void doStyle() {
        Image i = new Image("/files/smallTextures/buttonTexture.jpg");
        BackgroundImage bi = new BackgroundImage(i, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        Background b = new Background(bi);
        setBackground(b);
        setMinSize(100, 35);
        setFont(Font.font("Albertus Medium", 20));
    }
}
