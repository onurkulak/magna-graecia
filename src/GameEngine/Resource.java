/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameEngine;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 *
 * @author onur
 */
class Resource {
    private String name;
    private final static int resourceEdgeLength = 32;
    void draw(GraphicsContext gc, int x, int y, int edgeLength) {
        gc.drawImage(new Image("files/resources/" + name + ".png"), 
                x + (edgeLength*2 - resourceEdgeLength) / 2, y + edgeLength*2 - resourceEdgeLength - 5);
        //-5 is for the padding...
    }
    public enum ResourceTypes{MATERIAL, EDIBLE, SLAVE, LUXURY};
    private final ResourceTypes type;
    private double baseTradeValue;
    private ProductionBuilding associatedBuilding;

    public Resource(String name, double baseTradeValue, ResourceTypes r) {
        this.name = name;
        this.baseTradeValue = baseTradeValue;
        type = r;
    }

    public void setBaseTradeValue(double baseTradeValue) {
        this.baseTradeValue = baseTradeValue;
    }

    public void setAssociatedBuilding(ProductionBuilding associatedBuilding) {
        this.associatedBuilding = associatedBuilding;
    }

    public String getName() {
        return name;
    }

    public double getBaseTradeValue() {
        return baseTradeValue;
    }

    public ProductionBuilding getAssociatedBuilding() {
        return associatedBuilding;
    }
}
