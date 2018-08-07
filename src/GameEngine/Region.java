/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameEngine;

import java.util.Random;
/**
 *
 * @author onur
 */
public class Region extends Terra{
    
    
    
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
    }
    
}
