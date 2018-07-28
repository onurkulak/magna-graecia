/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameEngine;

import java.util.Random;
import javafx.scene.canvas.GraphicsContext;
/**
 *
 * @author onur
 */
public class Province extends Terra{
    private Force military;
    private int population;

    public Province(int x, int y) {
        super(x,y);
        setPopulation(0);
    }

    public Province(TerrainType t, Resource r, int pop, int x, int y) {
        //initial settlements around the city
        super(x,y);
        setTerrain(t);
        setProducedResource(r);
        setPopulation(pop);
    }
    
    public Province(TerrainType t, Random seed, Resources r, int x, int y) {
        //newly created province, without neighbour effect
        super(x,y);
        setTerrain(t);
        setProducedResource(getRandomResorceForTerrain(t, seed, r));
        setPopulation(0);
    }
    
    public void draw(GraphicsContext gc, int x, int y, int edgeLength){
        super.draw(gc, x, y, edgeLength);
    }
    
    public Province(Terra[] neighbours, TerrainType regionTerrain, Resource regionResource, 
            double provinceRegionTerrainSim, double provinceRegionResourceSim, 
            double provinceNeighboursTerrainSim, double provinceNeighboursResourceSim, 
            double latitude, Random seed, String name, Resources resources, int x, int y) {
        super(x,y);
        terrainProbs = new double[]{
        //SEA, DESERT, PLAIN, GRASS, FOREST, HILL
        //default probabilities
        1.2, 0.1, 0.1, 0.15, 0.15, 0.20
        };
        latitudeConfiguration(latitude);
        //procedural province generation
        for(int i = 0; i < terrainProbs.length; i++)
            terrainProbs[i] *= 1 - provinceRegionTerrainSim - provinceNeighboursTerrainSim;
        terrainProbs[regionTerrain.ordinal()] += provinceRegionTerrainSim;
        for (Terra neighbour : neighbours) {
            if (neighbour != null) {
                terrainProbs[neighbour.getTerrain().ordinal()] += provinceNeighboursTerrainSim / neighbours.length;
            }
        }
        GenericMath.normalizeArray(terrainProbs);
        setTerrain(GenericMath.getSample(TerrainType.values(), terrainProbs));
        setName(name);
        
        if(seed.nextDouble() < provinceRegionResourceSim)
            setProducedResource(regionResource);
        else decideResource(neighbours, provinceNeighboursResourceSim, resources, seed);
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
        if(TerrainType.SEA == getTerrain()){
            this.population = 0;
            if(population > 0)
                System.out.println("trying to set positive sea population");
        }
        else    this.population = population;
    }

    
}
