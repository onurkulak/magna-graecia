/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameEngine;

import java.util.Random;
import java.util.ArrayList;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.awt.Point;
import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author onur
 */
public class Province extends Terra {

    private City capital;
    private Force military;
    private int population;
    private static String[] provinceNamesList;
    private static Random privateSeed;

    private static final String[] borderPostfixes = new String[]{
        "tr", "r", "br", "bl", "l", "tl"
    };
    //these values should be multipleied with edgelength to get true x-y drawing coordinate offsets
    //they should be subtracted from the given x,y pixels of the hex while drawing walls
    private static final double[][] edgeOffsets = new double[][]{
        {0, 2}, {0, 2}, {0, 0}, {1.5, 1}, {1.5, 1}, {1.5, 3}
    };
    private static final String pathtoBorderGraphics = "files/terrain/wall/castle-";

    public Province(int col, int row) {
        super(col, row);
        setName(provinceNamesList[privateSeed.nextInt(provinceNamesList.length)]);
        setCapital(null);
        setPopulation(1);
    }

    public Province(TerrainType t, Resource r, int pop, int row, int col, City c, double altitude) {
        //initial settlements around the city
        super(col, row);
        setTerrain(t);
        setProducedResource(r);
        setPopulation(pop);
        setPseudoAltitude(altitude);
        setCapital(c);
        setName(provinceNamesList[privateSeed.nextInt(provinceNamesList.length)]);
    }

    public Province(Terra t, Random seed, Resources r, int row, int col) {
        //newly created province, without neighbour effect
        super(col, row);
        setTerrain(t.getTerrain());
        setProducedResource(getRandomResorceForTerrain(t.getTerrain(), seed, r));
        setPopulation(0);
        setCapital(null);
        setName(provinceNamesList[privateSeed.nextInt(provinceNamesList.length)]);
        if(!t.isLake)
            setIsLake(t.isLake);
        setPseudoAltitude(t.getPseudoAltitude());
    }

    public Province(Terra[] neighbours, Region r,
            double provinceRegionTerrainSim, double provinceRegionResourceSim,
            double provinceNeighboursTerrainSim, double provinceNeighboursResourceSim,
            double latitude, Random seed, String name, Resources resources, int x, int y) {
        super(x, y);
        terrainProbs = new double[]{
            //SEA, DESERT, PLAIN, GRASS, FOREST, HILL
            //default probabilities
            1.2, 0.1, 0.1, 0.15, 0.15, 0.20
        };
        latitudeConfiguration(latitude);
        //procedural province generation
        for (int i = 0; i < terrainProbs.length; i++) {
            terrainProbs[i] *= 1 - provinceRegionTerrainSim - provinceNeighboursTerrainSim;
        }
        terrainProbs[r.getTerrain().ordinal()] += provinceRegionTerrainSim;
        for (Terra neighbour : neighbours) {
            if (neighbour != null) {
                terrainProbs[neighbour.getTerrain().ordinal()] += provinceNeighboursTerrainSim / neighbours.length;
            }
        }
        GM.normalizeArray(terrainProbs);
        setTerrain(GM.getSample(TerrainType.values(), terrainProbs));
        setName(name);

        if (seed.nextDouble() < provinceRegionResourceSim) {
            setProducedResource(r.getResource());
        } else {
            decideResource(neighbours, provinceNeighboursResourceSim, resources, seed);
        }
        setPopulation(0);
        setPseudoAltitude(r.getPseudoAltitude());
    }

    public Force getMilitary() {
        return military;
    }

    public void setMilitary(Force military) {
        this.military = military;
    }

    public int getPopulation() {
        return population;
    }

    private void setPopulation(int population) {
        if (TerrainType.SEA == getTerrain()) {
            this.population = 0;
            if (population > 0) {
                System.out.println("trying to set positive sea population");
            }
        } else {
            this.population = population;
        }
    }

    //i = 0 is top right, 1 is right etc. clockwise
    public static boolean drawGivenBorders(Terra[] nb, Province centerProvince,
            int checkedNeighbours, GraphicsContext gc, int edgeLength, int x, int y) {
        String p = null;
        if (!isTerraIncognita(nb[checkedNeighbours]) && !isTerraIncognita(nb[(checkedNeighbours + 1) % 6])
                && nb[checkedNeighbours].getOwner() == nb[(checkedNeighbours + 1) % 6].getOwner()
                && (isTerraIncognita(centerProvince) || nb[checkedNeighbours].getOwner() != centerProvince.getOwner())) {
            p = "concave-";
        } else if (!isTerraIncognita(centerProvince)
                && (isTerraIncognita(nb[checkedNeighbours]) || centerProvince.getOwner() != nb[checkedNeighbours].getOwner())
                && (isTerraIncognita(nb[(checkedNeighbours + 1) % 6]) || centerProvince.getOwner() != nb[(checkedNeighbours + 1) % 6].getOwner())) {
            p = "convex-";
        }
        if (p == null) {
            return false;
        }
        p += borderPostfixes[checkedNeighbours];
        p = pathtoBorderGraphics + p + ".png";
        gc.drawImage(new Image(
                p, edgeLength / 18 * 63, 0, true, true),
                x - edgeLength * edgeOffsets[checkedNeighbours][0], y - edgeLength * edgeOffsets[checkedNeighbours][1]);
        return true;
    }

    public void draw(GraphicsContext gc, int x, int y, int edgeLength, boolean[] displaySettings) {
        super.draw(gc, x, y, edgeLength, displaySettings);
        if (displaySettings[2] && military != null) {
            military.drawStrategicMap(gc, x, y, edgeLength);
        }
    }

    public void drawPopulation(GraphicsContext gc, int x, int y, int edgeLength) {
        // only draw population if it's a city or has population
        if(population == 0 && capital != this)
            return;
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);
        gc.setFill(getOwner().getColor());
        gc.fillOval(x+edgeLength/2, y+edgeLength*3/2, edgeLength/2, edgeLength/2);
        gc.strokeOval(x+edgeLength/2, y+edgeLength*3/2, edgeLength/2, edgeLength/2);
        gc.setFont(new Font(edgeLength/4));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.strokeText(population+"", x+edgeLength*3/4, y+edgeLength*7/4, edgeLength/4);
    }

    public Node getConstructionPanel() {
        return null;
    }

    public static void setPrivateSeed(Random privateSeed) {
        Province.privateSeed = privateSeed;
    }

    public City getCapital() {
        return capital;
    }

    public void setCapital(City capital) {
        this.capital = capital;
        if(capital == null)
            setOwner(null);
        else {
            setOwner(capital.getOwner());
            capital.getDominions().add(this);
        }
    }

    public String getInfo() {
        return "Province Name:\t" + (getName() == null || getName().equals("")
                ? "Coast" : getName())
                + "\nPart Of:\t\t" + (getCapital() == null ? "Wilderness" : getCapital().getName())
                + "\nPopulation:\t\t" + getPopulation() + "\n" + super.getInfo();
    }

    public static void setProvinceNamesList(String[] provinceNamesList) {
        Province.provinceNamesList = provinceNamesList;
    }
    
    /**
     *
     *  if the tile is a water tile but not connected to a sea,
     *  it's considered a lake
     */
    @Override
    public boolean isLake(Terra[][] map) {
        if (!simpleLakeCheckBase()) {
            isLake = randomBreadthFirstSearchWithObstacles(
                    (Terra t) -> { return t != null && t.getTerrain() == Terra.TerrainType.SEA
                            && t.simpleLakeCheckBase() && !t.isLake;},
                    (Terra t) -> {
                        return t != null && t.getTerrain() == Terra.TerrainType.SEA;}
                    , map, new Point(coord.row, coord.col)) == null;
            lakeCalculationMade = true;
            return isLake;
        } else {
            return isLake;
        }
    }

    public void setIsLake(boolean isLake) {
        this.lakeCalculationMade = true;
        this.isLake = isLake;
    }

    @Override
    boolean doesAppearInSmallMap() {
        return false;
    }

    @Override
    public void setOwner(Faction owner) {
        super.setOwner(owner);
        owner.getProvinces().add(this);
    }

    @Override
    public void freeOwner() {
        getOwner().getProvinces().remove(this);
        setOwner(null);
    }
}
