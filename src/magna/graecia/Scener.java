/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package magna.graecia;

import GameEngine.GM;
import GameEngine.Game;
import GameEngine.Province;
import GameEngine.Settings;
import GameEngine.Terra;
import GameEngine.Layout;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.awt.Point;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author onur
 */
public class Scener {

    private static int smallMapEdgeLength = 36;
    private static int largeMapEdgeLength = 36;
    
    private static ScrollPane smallMapScrollPane;
    private static ScrollPane largeMapScrollPane;
    
    /*
     first is show resources, second is show borders, third is show armies, forth is population
     */
    private final static boolean[] smallMapDisplaySettings = new boolean[4];

    /*
     first is show resources, second is show territories, third is show small map regions
     */
    private final static boolean[] largeMapDisplaySettings = new boolean[3];

    private static Layout largeMapHexLayout;
    private static Layout smallMapHexLayout;
    private final static int paddingCoefficient = 2;
    private final static String menuBackground = "/files/bg/menu.jpg";
    private final static String newGameBackground = "/files/bg/newgame.jpg";
    private final static String switchSmallMapLayer = "/files/smallTextures/smallMap.jpg";
    private final static String switchLargeMapLayer = "/files/smallTextures/grand.jpg";
    private final static String switchTrade = "/files/smallTextures/trade.jpg";
    private final static String switchTech = "/files/smallTextures/tech.jpg";
    private final static String switchPolitics = "/files/smallTextures/politics.jpg";
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
                        (int) settings[5].getValue()
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
            settingDescriptions[i].setFont(Font.font("Albertus Medium", 13));
            settingChoices[i].setFont(Font.font("Albertus Medium", 13));
            HBox t = new HBox(settingDescriptions[i], settingChoices[i]);
            t.setMaxWidth(s.getWidth() / 4);
            // @TODO bunu düzelt aq css mi yaparsın ne yaparsın artık
            t.setStyle("-fx-border-color:  #545454; -fx-background-color: #EE9900;");
            t.setAlignment(Pos.CENTER);
            root.getChildren().add(t);
            root.getChildren().add(settings[i]);
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

        Button switchSmall = new MenuImageButton(switchSmallMapLayer);
        Button switchLarge = new MenuImageButton(switchLargeMapLayer);
        Button switchTechLayer = new MenuImageButton(Scener.switchTech);
        Button switchTradeLayer = new MenuImageButton(Scener.switchTrade);
        Button switchPoliticsLayer = new MenuImageButton(Scener.switchPolitics);
        Button switchMenu = new MenuImageButton(quitGameLayer);

        HBox tabs = new HBox((s.getWidth() - 6 * switchSmall.getWidth()) / 20);
        tabs.setAlignment(Pos.CENTER);
        tabs.getChildren().addAll(switchSmall, switchLarge, switchTradeLayer, switchTechLayer, switchPoliticsLayer, switchMenu);

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
        switchTechLayer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switchTechLayer(openGame, bp);
            }
        });
        switchTradeLayer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switchTradeLayer(openGame, bp);
            }
        });
        switchPoliticsLayer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switchPoliticsLayer(openGame, bp);
            }
        });
        return tabs;
    }
    
    private static void switchSmallMap(Game openGame, BorderPane bp) {
        bp.setRight(null);
        bp.setLeft(null);
        
        if (smallMapScrollPane == null){
            smallMapScrollPane = new ScrollPane();
            smallMapScrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
            smallMapScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        }
        Canvas canvas = new Canvas(0, 0);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawSmallMapCanvas(openGame, gc);
        smallMapScrollPane.setContent(canvas);
        bp.setCenter(smallMapScrollPane);
        HBox bottomPanel;
        Label l = getTerrainInfoLabel();
        
        canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
                Point coords = smallMapHexLayout.pixelToHex(e.getX(), e.getY());

                //debug part
                System.out.println(e.getX() + " " + e.getY());
                System.out.println(coords.x);
                System.out.println(coords.y);

                if (GM.getMatrix(openGame.smallMap, coords.y, coords.x)!=null)
                    bp.setRight(openGame.smallMap[coords.y][coords.x].getConstructionPanel());
            }
        });

        canvas.setOnMouseMoved(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
                Point coords = smallMapHexLayout.pixelToHex(e.getX(), e.getY());
                if (GM.getMatrix(openGame.smallMap, coords.y, coords.x)!=null) {
                    l.setText(openGame.smallMap[coords.y][coords.x].getInfo());
                }
            }
        });

        Slider zoom = new Slider(18, 360, smallMapEdgeLength);
        zoom.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2) {
                if (smallMapEdgeLength == (int) zoom.getValue()) {
                    return;
                }
                zoom.setValue((int) zoom.getValue() / 18 * 18);
                smallMapEdgeLength = (int) zoom.getValue();
                drawSmallMapCanvas(openGame, gc);
            }
        });

        zoom.setMaxWidth(s.getWidth() / 4);
        CheckBox[] displayChoices = new CheckBox[]{
            new CheckBox("display resources"), new CheckBox("display borders"),
            new CheckBox("display armies"), new CheckBox("display populations")};
        for (int i = 0; i < displayChoices.length; i++) {
            displayChoices[i].setSelected(smallMapDisplaySettings[i]);
            displayChoices[i].selectedProperty().addListener(new ChangeListener<Boolean>() {
                public void changed(ObservableValue<? extends Boolean> ov,
                        Boolean old_val, Boolean new_val) {
                    refreshDisplayChoices(displayChoices, smallMapDisplaySettings);
                    drawSmallMapCanvas(openGame, gc);
                }
            });
        }
        VBox checkboxes = new VBox(displayChoices);
        bottomPanel = new HBox(20, l, zoom, checkboxes);
        bp.setBottom(bottomPanel);
    }

    private static Label getTerrainInfoLabel() {
        Label terrainInfo = new Label("");
        Image i = new Image("/files/smallTextures/buttonTexture.jpg");
        BackgroundImage bi = new BackgroundImage(i, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        Background b = new Background(bi);
        terrainInfo.setBackground(b);
        terrainInfo.setPrefSize(250, 150);
        terrainInfo.setFont(Font.font("Albertus Medium", 15));
        return terrainInfo;
    }

    private static void drawSmallMapCanvas(Game openGame, GraphicsContext gc) {
        //refresh layout
        smallMapHexLayout = new Layout(Layout.flat, smallMapEdgeLength, smallMapEdgeLength * 2 / Math.sqrt(3),
                smallMapEdgeLength * paddingCoefficient / 2, smallMapEdgeLength * paddingCoefficient / 2);

        //draw map
        drawMapCanvas(gc, openGame.smallMap, smallMapEdgeLength, smallMapDisplaySettings);

        //draw borders using wall graphics
        if (smallMapDisplaySettings[1]) {
            drawBorders(openGame, gc);
        }
    }

    private static void drawMapCanvas(GraphicsContext gc, Terra[][] map, int edgeLength, boolean[] displaySettings) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        double graphicalWidth = (map[0].length * 1.5 + 0.5 + paddingCoefficient) * edgeLength;
        double graphicalHeight = (map.length * 2 + 1 + paddingCoefficient) * edgeLength;
        gc.getCanvas().setWidth(graphicalWidth);
        gc.getCanvas().setHeight(graphicalHeight);

        //add padding
        gc.getCanvas().setTranslateX(edgeLength * paddingCoefficient / 2);
        gc.getCanvas().setTranslateY(edgeLength * paddingCoefficient / 2);

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                int x, y;
                x = edgeLength * j * 3 / 2;
                y = edgeLength * (i * 2);
                if (j % 2 == 1) {
                    y += edgeLength;
                }
                map[i][j].draw(gc, x, y, edgeLength, displaySettings);
            }
        }
    }

    private static void drawBorders(Game openGame, GraphicsContext gc) {
        for (int row = -1; row < openGame.smallMap.length + 1; row++) {
            for (int col = 0; col < openGame.smallMap[0].length + 1; col += 2) {
                drawHexBorder(row, col, openGame, gc);
            }
            for (int col = -1; col < openGame.smallMap[0].length + 1; col += 2) {
                drawHexBorder(row, col, openGame, gc);
            }
        }
    }

    private static void drawHexBorder(int row, int col, Game openGame, GraphicsContext gc) {
        Point[] neighbourCoords = Terra.getNeighbourArrayIndecesNoNulls(row, col, openGame.smallMap);
        drawSpecificEdge(col, row, openGame, 0, gc);
        drawSpecificEdge(col, row, openGame, 5, gc);
        drawSpecificEdge(neighbourCoords[0].y, neighbourCoords[0].x, openGame, 3, gc);
        drawSpecificEdge(neighbourCoords[0].y, neighbourCoords[0].x, openGame, 2, gc);
        drawSpecificEdge(neighbourCoords[1].y, neighbourCoords[1].x, openGame, 4, gc);
        drawSpecificEdge(neighbourCoords[5].y, neighbourCoords[5].x, openGame, 1, gc);
    }

    private static void drawSpecificEdge(int col, int row, Game openGame, int borderEdgeID, GraphicsContext gc) {
        int x = smallMapEdgeLength * col * 3 / 2;
        int y = smallMapEdgeLength * (row * 2);
        if ((col & 1) == 1) {
            y += smallMapEdgeLength;
        }
        if (Province.drawGivenBorders(Terra.getNeighbours(col, row, openGame.smallMap),
                GM.getMatrix(openGame.smallMap, row, col), borderEdgeID, gc, smallMapEdgeLength, x, y));
        //System.out.println(col +" "+ row +" "+ borderEdgeID);
    }

    private static void switchLargeMap(Game openGame, BorderPane bp) {
        bp.setRight(null);
        bp.setLeft(null);

        if (largeMapScrollPane == null){
            largeMapScrollPane = new ScrollPane();
            largeMapScrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
            largeMapScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        }
        Canvas canvas = new Canvas(0, 0);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawLargeMapCanvas(openGame, gc);
        largeMapScrollPane.setContent(canvas);
        bp.setCenter(largeMapScrollPane);
        HBox bottomPanel;
        Label l = getTerrainInfoLabel();

        canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
                Point coords = largeMapHexLayout.pixelToHex(e.getX(), e.getY());

                //debug part
                System.out.println(e.getX() + " " + e.getY());
                System.out.println(coords.x);
                System.out.println(coords.y);

                //there will be diplomacy screen instead
                //bp.setRight(openGame.largeMap[coords.y][coords.x].getConstructionPanel());
            }
        });

        canvas.setOnMouseMoved(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
                Point coords = largeMapHexLayout.pixelToHex(e.getX(), e.getY());
                if (GM.getMatrix(openGame.largeMap, coords.y, coords.x)!=null) {
                    l.setText(openGame.largeMap[coords.y][coords.x].getInfo());
                }
            }
        });

        Slider zoom = new Slider(18, 360, largeMapEdgeLength);
        zoom.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2) {
                if (largeMapEdgeLength == (int) zoom.getValue()) {
                    return;
                }
                zoom.setValue((int) zoom.getValue() / 18 * 18);
                largeMapEdgeLength = (int) zoom.getValue();
                drawLargeMapCanvas(openGame, gc);
            }
        });

        zoom.setMaxWidth(s.getWidth() / 4);
        // do faction coloring..
        CheckBox[] displayChoices = new CheckBox[]{
            new CheckBox("display resources"), new CheckBox("display factions"),
            new CheckBox("display small map regions")};
        for (int i = 0; i < displayChoices.length; i++) {
            displayChoices[i].setSelected(largeMapDisplaySettings[i]);
            displayChoices[i].selectedProperty().addListener(new ChangeListener<Boolean>() {
                public void changed(ObservableValue<? extends Boolean> ov,
                        Boolean old_val, Boolean new_val) {
                    refreshDisplayChoices(displayChoices, largeMapDisplaySettings);
                    drawLargeMapCanvas(openGame, gc);
                }
            });
        }
        VBox checkboxes = new VBox(displayChoices);
        bottomPanel = new HBox(20, l, zoom, checkboxes);
        bp.setBottom(bottomPanel);
    }

    private static void switchTechLayer(Game openGame, BorderPane bp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void switchTradeLayer(Game openGame, BorderPane bp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void switchPoliticsLayer(Game openGame, BorderPane bp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void refreshDisplayChoices(CheckBox[] displayChoices, boolean[] settings) {
        for (int i = 0; i < settings.length; i++) {
            settings[i] = displayChoices[i].isSelected();
        }
    }

    private static void drawLargeMapCanvas(Game openGame, GraphicsContext gc) {
        //refresh layout
        largeMapHexLayout = new Layout(Layout.flat, largeMapEdgeLength, largeMapEdgeLength * 2 / Math.sqrt(3),
                largeMapEdgeLength * paddingCoefficient / 2, largeMapEdgeLength * paddingCoefficient / 2);

        //draw map
        drawMapCanvas(gc, openGame.largeMap, largeMapEdgeLength, largeMapDisplaySettings);
    }
}
