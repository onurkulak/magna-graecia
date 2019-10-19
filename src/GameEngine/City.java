/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameEngine;

import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author onur
 */
public class City extends Province {

    private ArrayList<BuildingInstance> buildings;
    private ArrayList<Unit> trainQueue;
    private ArrayList<Province> dominions;
    private CityOrder co = null;
    private final Region regionEquivalent;

    public City(Region r, int x, int y) {
        super(x, y);
        setTerrain(r.getTerrain());
        buildings = new ArrayList<>();
        trainQueue = new ArrayList<>();
        setCapital(this);
        setPseudoAltitude(r.getPseudoAltitude());
        regionEquivalent = r;
        dominions = new ArrayList<>();
        regionEquivalent.setSmallMapCity(this);
    }
    
    @Override
    public void setOwner(Faction f) {
        super.setOwner(f);
        for(Province p:dominions)
            p.setOwner(f);
        f.getCities().add(this);
    }
    
    @Override
    public void freeOwner() {
        super.freeOwner();
        for(Province p:dominions)
            p.freeOwner();
    }

    @Override
    public void draw(GraphicsContext gc, int x, int y, int edgeLength, boolean[] displaySettings) {
        drawTerrain(gc, x, y, edgeLength);
        gc.drawImage(new Image("files/terrain/city/" + getTerrain().toString().toLowerCase() + ".png",
                edgeLength * 2, edgeLength * 2, false, true), x, y);
        if (displaySettings[3]) {
            drawPopulation(gc, x, y, edgeLength);
        }
    }

    protected void drawTerrain(GraphicsContext gc, int x, int y) {
        if (getTerrain() == TerrainType.FOREST) {
            gc.drawImage(new Image("files/terrain/" + "grass" + ".png"), x, y);
        } else if (getTerrain() == TerrainType.HILL) {
            gc.drawImage(new Image("files/terrain/" + "plain" + ".png"), x, y);
        } else {
            gc.drawImage(new Image("files/terrain/" + getTerrain().toString().toLowerCase() + ".png"), x, y);
        }
    }

    
    @Override
    public Node getConstructionPanel() {
        Button built = new Button("", new ImageView(new Image("/files/smallTextures/buildings-built.png")));
        Button buildable = new Button("", new ImageView(new Image("/files/smallTextures/buildings-available.png")));

        HBox h = new HBox(20, built, buildable);
        VBox v = new VBox(10, h);
        Node orderNode = getActiveConstructionOrder(built);
        v.getChildren().add(orderNode);

        built.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                buildable.setEffect(null);
                built.setEffect(new DropShadow());
                initializeBuiltBuildingsList(v, built);
            }
        });
        buildable.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                built.setEffect(null);
                buildable.setEffect(new DropShadow());
                initializeBuildableBuildingsList(v, buildable);
            }
        });
        v.setAlignment(Pos.TOP_CENTER);
        built.fire();
        return v;
    }

    private void initializeBuildableBuildingsList(VBox v, Button buildable) {
        v.getChildren().remove(2, v.getChildren().size());
        ArrayList<Building> buildables = this.getOwner().getAvailableBuildings();
        for (BuildingInstance building : buildings) {
            buildables.remove(building.getParent());
        }
        if (co != null && co.buildOrRemove) {
            buildables.remove(co.b.getParent());
        }
        for (Building b : buildables) {
            BufferedImage finishedBuildingGraph = new BufferedImage(150, 60, BufferedImage.TYPE_INT_RGB);
            Image buildingIcon = new Image("/files/buildingIcons/" + co.b.getParent().getName() + ".png");
            //TODO find building icons..

            finishedBuildingGraph.getGraphics().drawImage(SwingFXUtils.fromFXImage(buildingIcon, null), 0, 0, null);
            finishedBuildingGraph.getGraphics().setFont(new java.awt.Font("Albertus Medium", java.awt.Font.LAYOUT_LEFT_TO_RIGHT, 13));
            String infoString = b.getName() + "\tCost: " + b.getCost() + "\nConstruction Time: " + b.getConstructionTime();
            finishedBuildingGraph.getGraphics().drawString(infoString, 60, 10);

            Image emptyWorkerSpace = new Image("/files/buildingIcons/emptyPop.png");
            int popheadsize = 0;
            //fix this size after finding image

            //draws how many workers wok in this place
            for (int i = 1; i <= b.getMaxWorkerCapacity(); i++) {
                finishedBuildingGraph.getGraphics().drawImage(SwingFXUtils.fromFXImage(
                        emptyWorkerSpace, null), 150 - popheadsize * i, 60 - popheadsize, null);
            }

            Button constructBuilding = new Button(null, new ImageView(SwingFXUtils.toFXImage(finishedBuildingGraph, null)));
            constructBuilding.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    co = new CityOrder(b);
                    buildable.fire();
                }
            });
        }
    }

    private void initializeBuiltBuildingsList(VBox v, Button built) {
        v.getChildren().remove(2, v.getChildren().size());
        for (BuildingInstance b : buildings) {
            if (co == null || co.b.getParent() != b.getParent()) {
                BufferedImage finishedBuildingGraph = new BufferedImage(150, 60, BufferedImage.TYPE_INT_RGB);
                Image buildingIcon = new Image("/files/buildingIcons/" + co.b.getParent().getName() + ".png");
                //TODO find building icons..

                finishedBuildingGraph.getGraphics().drawImage(SwingFXUtils.fromFXImage(buildingIcon, null), 0, 0, null);
                
                finishedBuildingGraph.getGraphics().setFont(new java.awt.Font("Albertus Medium", java.awt.Font.LAYOUT_LEFT_TO_RIGHT, 13));
                String infoString = b.getParent().getName() + "\tCost: " + b.getParent().getCost() + "\n";
                finishedBuildingGraph.getGraphics().drawString(infoString, 60, 10);
                
                Image filledWorkerSpace = new Image("/files/buildingIcons/greenPop.png");
                Image emptyWorkerSpace = new Image("/files/buildingIcons/emptyPop.png");

                int popheadsize = 0;
                //fix this size after finding image

                for (int i = 1; i <= b.getParent().getMaxWorkerCapacity(); i++) {
                    if (i <= b.getParent().getMaxWorkerCapacity() - b.getWorkerCount()) {
                        finishedBuildingGraph.getGraphics().drawImage(SwingFXUtils.fromFXImage(
                                emptyWorkerSpace, null), 150 - popheadsize * i, 60 - popheadsize, null);
                    } else {
                        finishedBuildingGraph.getGraphics().drawImage(SwingFXUtils.fromFXImage(
                                filledWorkerSpace, null), 150 - popheadsize * i, 60 - popheadsize, null);
                    }
                }

                Button destructBuilding = new Button(null, new ImageView(SwingFXUtils.toFXImage(finishedBuildingGraph, null)));
                destructBuilding.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        co = new CityOrder(b);
                        built.fire();
                    }
                });
            }
        }
    }

    private Node getActiveConstructionOrder(Button built) {
        if (co == null) {
            Label l = new Label("There isn't any\nConstruction Ordered");
            Image i = new Image("/files/smallTextures/buttonTexture.jpg");
            BackgroundImage bi = new BackgroundImage(i, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
            Background b = new Background(bi);
            l.setBackground(b);
            l.setPrefSize(150, 60);
            l.setFont(Font.font("Albertus Medium", 15));
            l.setTextAlignment(TextAlignment.JUSTIFY);
            return l;
        } else {
            BufferedImage orderImage = new BufferedImage(150, 60, BufferedImage.TYPE_INT_RGB);
            Image buildingIcon = new Image("/files/buildingIcons/" + co.b.getParent().getName() + ".png");
            //TODO find building icons.. they should be 120x60 or something like that
            Image orderType = null;
            if (co.buildOrRemove) {
                orderType = new Image("/files/buildingIcons/construct.png", 50, 50, false, true);
            } else {
                orderType = new Image("/files/buildingIcons/deconstruct.png", 50, 50, false, true);
            }
            orderImage.getGraphics().drawImage(SwingFXUtils.fromFXImage(buildingIcon, null), 0, 0, null);
            orderImage.getGraphics().drawImage(SwingFXUtils.fromFXImage(orderType, null), 100, 10, null);
            Button cancelOrderButton = new Button(null, new ImageView(SwingFXUtils.toFXImage(orderImage, null)));
            cancelOrderButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    co = null;
                    built.fire();
                }
            });
            return cancelOrderButton;
        }
    }

    private static class CityOrder {

        BuildingInstance b;
        boolean buildOrRemove; //true if a build order

        public CityOrder(Building b) {
            this.b = new BuildingInstance(b);
            this.buildOrRemove = true;
        }

        public CityOrder(BuildingInstance b) {
            this.b = b;
            this.buildOrRemove = false;
        }
    }

    public ArrayList<Province> getDominions() {
        return dominions;
    }

    public Region getRegionEquivalent() {
        return regionEquivalent;
    }
}
