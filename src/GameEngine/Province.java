/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameEngine;

import java.util.Random;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

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

    public Province(int x, int y) {
        super(x, y);
        setName(provinceNamesList[privateSeed.nextInt(provinceNamesList.length)]);
        setCapital(null);
        setPopulation(0);
    }

    public Province(TerrainType t, Resource r, int pop, int x, int y, City c) {
        //initial settlements around the city
        super(x, y);
        setTerrain(t);
        setProducedResource(r);
        setPopulation(pop);
        setCapital(c);
        setName(provinceNamesList[privateSeed.nextInt(provinceNamesList.length)]);
    }

    public Province(TerrainType t, Random seed, Resources r, int x, int y) {
        //newly created province, without neighbour effect
        super(x, y);
        setTerrain(t);
        setProducedResource(getRandomResorceForTerrain(t, seed, r));
        setPopulation(0);
        setCapital(null);
        setName(provinceNamesList[privateSeed.nextInt(provinceNamesList.length)]);
    }

    public Province(Terra[] neighbours, TerrainType regionTerrain, Resource regionResource,
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
        terrainProbs[regionTerrain.ordinal()] += provinceRegionTerrainSim;
        for (Terra neighbour : neighbours) {
            if (neighbour != null) {
                terrainProbs[neighbour.getTerrain().ordinal()] += provinceNeighboursTerrainSim / neighbours.length;
            }
        }
        GM.normalizeArray(terrainProbs);
        setTerrain(GM.getSample(TerrainType.values(), terrainProbs));
        setName(name);

        if (seed.nextDouble() < provinceRegionResourceSim) {
            setProducedResource(regionResource);
        } else {
            decideResource(neighbours, provinceNeighboursResourceSim, resources, seed);
        }
        setPopulation(0);
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
        if (displaySettings[3]) {
            drawPopulation(gc, x, y, edgeLength);
        }
        if (displaySettings[2] && military != null) {
            military.drawStrategicMap(gc, x, y, edgeLength);
        }
    }

    protected void drawPopulation(GraphicsContext gc, int x, int y, int edgeLength) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
}
