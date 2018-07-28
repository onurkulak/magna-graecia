/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package magna.graecia;

import GameEngine.Game;
import GameEngine.Settings;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 *
 * @author onur
 */
public class Scener {
    private final static int edgeLength = 36;
    private final static String menuBackground = "/files/bg/menu.jpg";
    private final static String newGameBackground = "/files/bg/newgame.jpg";
    private final static String switchSmallMapLayer = "/files/smallTextures/smallMap.jpg";
    private final static String switchLargeMapLayer = "/files/smallTextures/grand.jpg";
    private final static String quitGameLayer = "/files/smallTextures/carthage.jpg";

    private static Scene s;

    public static void createAndSetMenu(Stage st) {
        Button btn = new GameTextButton("New Game");

        VBox root = new VBox(15);
        root.setCenterShape(true);
        root.setAlignment(Pos.CENTER);
        setBackground(root, new Image(menuBackground));
        if (s == null) {
            s = new Scene(root);
            st.setScene(s);
        } else {
            s.setRoot(root);
        }

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Scener.createAndSetNewGame(root);
            }
        });

        Button quitButton = new GameTextButton("Quit Game");
        quitButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                st.close();
            }
        });
        root.getChildren().add(btn);
        root.getChildren().add(quitButton);
    }

    private static void setBackground(VBox root, Image i) {
        BackgroundImage bi = new BackgroundImage(i, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        Background b = new Background(bi);
        root.setBackground(b);
    }

    private static void createAndSetNewGame(Parent menu) {
        Button createGameButton = new GameTextButton("Create Game");
        VBox root = new VBox(10);
        setBackground(root, new Image(newGameBackground));
        root.setCenterShape(true);
        root.setAlignment(Pos.CENTER);
        Label[] settingChoices = new Label[Settings.numberOfSettings];
        Slider[] settings = new Slider[Settings.numberOfSettings];
        for (int i = 0; i < Settings.numberOfSettings; i++) {
            settings[i] = new Slider(1, 9, 1);
            Slider slid = settings[i];
            settingChoices[i] = new Label("1");
            Label slidResult = settingChoices[i];
            slid.setMaxWidth(s.getWidth() / 2);
            slid.setBlockIncrement(s.getWidth() / 2);
            slid.valueProperty().addListener((obs, oldval, newVal) -> {
                slid.setValue(Math.round(newVal.doubleValue()));
                slidResult.setText("" + Math.round(newVal.doubleValue()));
            });
        }
        Label[] settingDescriptions = new Label[]{
            new Label("small map size\t:\t"),
            new Label("large map size\t:\t"),
            new Label("number of city states\t:\t"),
            new Label("number of hellenic factions\t:\t"),
            new Label("number of intervener powers\t:\t"),
            new Label("battle difficulty\t:\t"),
            new Label("general difficulty\t:\t")
        };

        s.setRoot(root);

        createGameButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Settings gameSetup = new Settings(
                        (int) settings[0].getValue(),
                        (int) settings[1].getValue(),
                        (int) settings[2].getValue(),
                        (int) settings[3].getValue(),
                        (int) settings[4].getValue(),
                        (int) settings[5].getValue(),
                        (int) settings[6].getValue()
                );
                Game openGame;
                try {
                    openGame = new Game(gameSetup);
                    Scener.enterGame(openGame, menu);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Scener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        Button returnButton = new GameTextButton("Go Back");
        returnButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                s.setRoot(menu);
            }
        });
        for (int i = 0; i < Settings.numberOfSettings; i++) {
            root.getChildren().add(settings[i]);
            settingDescriptions[i].setFont(Font.font("Albertus Medium", 13));
            settingChoices[i].setFont(Font.font("Albertus Medium", 13));
            HBox t = new HBox(settingDescriptions[i], settingChoices[i]);
            t.setMaxWidth(s.getWidth() / 4);
            //bunu düzelt aq css mi yaparsın ne yaparsın artık
            t.setStyle("-fx-border-color:  #545454; -fx-background-color: #EE9900;");
            t.setAlignment(Pos.CENTER);
            root.getChildren().add(t);
        }

        root.getChildren().add(createGameButton);
        root.getChildren().add(returnButton);
    }

    private static void enterGame(Game openGame, Parent menu) {
        BorderPane mainLayout = new BorderPane();
        HBox tabs = handleMenuBar(openGame, menu, mainLayout);
        mainLayout.setTop(tabs);
        switchSmallMap(openGame, mainLayout);
        s.setRoot(mainLayout);
    }

    private static HBox handleMenuBar(Game openGame, Parent menu, BorderPane bp) {
        HBox tabs = new HBox(10);
        Button switchSmall = new MenuImageButton(switchSmallMapLayer);
        Button switchLarge = new MenuImageButton(switchLargeMapLayer);
        Button switchMenu = new MenuImageButton(quitGameLayer);
        tabs.getChildren().addAll(switchSmall, switchLarge, switchMenu);
        switchSmall.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switchSmallMap(openGame, bp);
            }
        });
        switchLarge.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switchLargeMap(openGame, bp);
            }
        });
        switchMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                s.setRoot(menu);
            }
        });
        return tabs;
    }

    private static void switchSmallMap(Game openGame, BorderPane bp) {
        ScrollPane sp = new ScrollPane();
        sp.setVbarPolicy(ScrollBarPolicy.NEVER);
        sp.setHbarPolicy(ScrollBarPolicy.NEVER);
        double graphicalWidth = (openGame.smallMap.length * 1.5 + 0.5) * edgeLength;
        double graphicalHeight = (openGame.smallMap[0].length * 2 + 1) * edgeLength;
        Canvas canvas = new Canvas(graphicalWidth, graphicalHeight);
        sp.setContent(canvas); bp.setCenter(sp);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        for(int i = 0; i < openGame.smallMap.length; i++)
            for(int j = 0; j < openGame.smallMap[0].length; j++){
                int x, y;
                x = edgeLength * j * 3 / 2;
                y = edgeLength * (i * 2);
                if(j%2 == 1){
                    y += edgeLength;
                }
                openGame.smallMap[i][j].draw(gc, x, y, edgeLength);
            }
    }
    private static void switchLargeMap(Game openGame, BorderPane bp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
