/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package magna.graecia;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author onur
 */
public class MagnaGraecia extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setFullScreenExitHint("");
        //primaryStage.setFullScreen(true);
        Scener.createAndSetMenu(primaryStage);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
