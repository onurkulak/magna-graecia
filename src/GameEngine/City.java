/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameEngine;

import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
/**
 *
 * @author onur
 */
public class City extends Province{
    private Force garrison;
    private ArrayList<Building> buildings;
    private ArrayList<Unit> trainQueue;

    public City(TerrainType t, int x, int y) {
        super(x,y);
        setTerrain(t);
        buildings = new ArrayList<>();
        trainQueue = new ArrayList<>();
    }

    @Override
    public void draw(GraphicsContext gc, int x, int y, int edgeLength, boolean[] displaySettings) {
        super.draw(gc, x, y, edgeLength, displaySettings);
        gc.drawImage(new Image("files/terrain/city/" + getTerrain().toString().toLowerCase()+ ".png", 
                edgeLength*2, edgeLength*2, false, true), x, y ); 
    }
    
    protected void drawTerrain(GraphicsContext gc, int x, int y) {
        if(getTerrain()==TerrainType.FOREST)
            gc.drawImage(new Image("files/terrain/" + "grass"+ ".png"), x, y );
        else if(getTerrain()==TerrainType.HILL)
            gc.drawImage(new Image("files/terrain/" + "plain"+ ".png"), x, y );
        else
            gc.drawImage(new Image("files/terrain/" + getTerrain().toString().toLowerCase()+ ".png"), x, y );
    }
}
