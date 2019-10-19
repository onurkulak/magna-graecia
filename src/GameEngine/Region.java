/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameEngine;

import java.util.Random;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
/**
 *
 * @author onur
 */
public class Region extends Terra{
    
    private boolean appearsInSmallMap;
    private City smallMapCity;
    
    //by latitude I mean 1 if it's the bottom row, 0 if top
    Region(String name, double latitude, Terra[] neighbours, 
            TerrainType terrainType, double terrainSimilarityCoefficient, 
            double resourceSimilarityCoefficient, Random seed, Resources resources,
            int x, int y) {
        
        super(x,y);
        terrainProbs = new double[]{
        //SEA, DESERT, PLAIN, GRASS, FOREST, HILL
        //default probabilities
        0.9, 0.1, 0.1, 0.15, 0.15, 0.20
        };
        latitudeConfiguration(latitude);
        for(int i = 0; i < neighbours.length; i++){
            if(neighbours[i] == null){
                terrainProbs[terrainType.ordinal()] += terrainSimilarityCoefficient / neighbours.length;
            }
            else terrainProbs[neighbours[i].getTerrain().ordinal()] += terrainSimilarityCoefficient / neighbours.length;
        }
        GM.normalizeArray(terrainProbs);
        setTerrain(GM.getSample(TerrainType.values(), terrainProbs, seed));
        setName(name);
        
        decideResource(neighbours, resourceSimilarityCoefficient, resources, seed);
        
        appearsInSmallMap = false;
    }
    
    // handy constructor for sea regions
    Region (int col, int row) {
        super(col, row);
        appearsInSmallMap = false;
        setTerrain(TerrainType.SEA);
        setName("");
    } 

    public void setAppearsInSmallMap(boolean appearsInSmallMap) {
        this.appearsInSmallMap = appearsInSmallMap;
    }

    @Override
    public boolean doesAppearInSmallMap() {
        return appearsInSmallMap;
    }

    @Override
    public void draw(GraphicsContext gc, int x, int y, int edgeLength, boolean[] displaySettings) {
        super.draw(gc, x, y, edgeLength, displaySettings);
        if(displaySettings[2] && appearsInSmallMap) {
            drawLargeSmallMapCorrespondence(gc, x, y, edgeLength);
        }
        if(displaySettings[1] && getOwner() != null){
            drawFactionBorders(gc, x, y, edgeLength);
        }
    }
    
    private void drawLargeSmallMapCorrespondence(GraphicsContext gc, int x, int y, int edgeLength) {
        gc.drawImage(new Image(
                "files/terrain/mask.png", edgeLength * 2, edgeLength * 2, true, true),
                x, y);
    }

    public City getSmallMapCity() {
        return smallMapCity;
    }

    public void setSmallMapCity(City smallMapCity) {
        this.smallMapCity = smallMapCity;
    }

    private void drawFactionBorders(GraphicsContext gc, int x, int y, int edgeLength) {
        // 0.75 * current alpha
        Color colorWithReducedAlpha = getOwner().getColor().deriveColor(0, 1, 1, 0.75);
        // 0.53 and 0.85 are for calculating the inner hex distance, cos and sine of 58 degree
        int strokeWidth = 3;
        double xPoints[] = new double[]{
            x+edgeLength/2+0.53*strokeWidth,
            x+edgeLength*3/2-0.53*strokeWidth,
            x+edgeLength*2-strokeWidth,
            x+edgeLength*3/2-0.53*strokeWidth,
            x+edgeLength/2+0.53*strokeWidth,
            x+strokeWidth
        };
        double yPoints[] = new double[]{
            y+strokeWidth*0.85,
            y+strokeWidth*0.85,
            y+edgeLength,
            y+edgeLength*2-strokeWidth*0.85,
            y+edgeLength*2-strokeWidth*0.85,
            y+edgeLength
        };
        
        gc.setLineWidth(strokeWidth*2);
        gc.setStroke(colorWithReducedAlpha);
        gc.strokePolygon(xPoints, yPoints, 6);
    }
    
    @Override
    public void setOwner(Faction owner) {
        super.setOwner(owner);
        owner.getRegions().add(this);
    }
    
    @Override
    public void freeOwner() {
        getOwner().getRegions().remove(this);
        setOwner(null);
    }
}
