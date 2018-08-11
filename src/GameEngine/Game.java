/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameEngine;

import java.util.ArrayList;
import java.util.Random;
import java.awt.Point;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
/**
 *
 * rivers?
 * wave function collapse in github can be used to create additional terrains
 * @author onur
 */
public class Game {

    public Region[][] largeMap;
    public Province[][] smallMap;
    private AIFaction[] rivalFactions;
    private Faction player;
    private Settings setup;
    private final Resources resources;
    private Unit[] allUnits;
    private Building[] buildings;
    private String[] regionNames, provinceNames; //implement these
    private Random seed;
    private int smallLargeProportion;
    
    public Game(Settings gameSetup) throws FileNotFoundException {
        smallLargeProportion = 2* (1+gameSetup.getSmallMapSize());
        setup = gameSetup;
        seed = new Random();
        resources = new Resources();
        readNames();
        initializeMap();
    }

    private void initializeMap() {
        int largeMapSize = setup.getLargeMapSize() + 6;
        largeMap = new Region[largeMapSize][largeMapSize*2];
        initializeLargerMap();
        initializeSmallerMap();
    }

    private void initializeSmallerMap() {
        Point p = new Point();
        int smallMapDesiredCityCount = setup.getSmallMapSize()+2;
        int cityCount = findSmallerMapArea(smallMapDesiredCityCount , p); 
        //so parameter is a minimum of 3 always
        //small maps large map equivalent's top left corner is stored in p
        
        //omitted for a while to see actual large maps
        if(cityCount == -1){
            System.out.println("err couldn't file appropriate place");
            initializeMap();
            //if no appropriate large map is created, tries to create maps again
            return;
        }    
                
        int largeMapEdge = getEdgeLengthFromCityCount(cityCount);
        
        int smallMapEdge = largeMapEdge*smallLargeProportion; 
        //assuming every hex on strategy map corresponds to 4*4 hexes on small map
        smallMap = new Province[smallMapEdge][smallMapEdge];
        
        //these two liens are for random generation of names
        //even if they are not explicitly created in this class
        Province.setPrivateSeed(seed); 
        Province.setProvinceNamesList(provinceNames);
        
        createSmallMapBorderTerrains(smallMapEdge, p, largeMapEdge);
        createCitiesAndSeas(largeMapEdge, p, smallMapEdge);
        for (int i = 0; i < smallMap.length; i++) {
            for (int j = 0; j < smallMap[0].length; j++) {
                if(smallMap[i][j]==null)
                    smallMap[i][j] = new Province(Terra.getNeighbours(j, i, smallMap), 
                            largeMap[i/smallLargeProportion + p.y][j/smallLargeProportion + p.x].getTerrain(),
                            largeMap[i/smallLargeProportion + p.y][j/smallLargeProportion + p.x].getResource(),
                            getProvinceRegionTerrainSimilarity(), getProvinceRegionResourceSimilarity(),
                            getProvinceTerrainNeighbourhoodSimilarity(), getProvinceNeighbourhoodResourceSimilarity(),
                            (i/smallLargeProportion + p.x +.0) / largeMapEdge, seed, 
                            provinceNames[seed.nextInt(provinceNames.length)], resources,
                            j,i);
                //System.out.print(smallMap[i][j].getTerrain()+ "\t");
            }
            //System.out.println();
        }
    }
    private double getProvinceRegionTerrainSimilarity(){
        return 0.3;
    }
    
    private double getProvinceRegionResourceSimilarity(){
        return 0.1;
    }
    
    private double getProvinceTerrainNeighbourhoodSimilarity(){
        return 0.4;
    }
    
    private double getProvinceNeighbourhoodResourceSimilarity(){
        return 0.3;
    }

    private void createSmallMapBorderTerrains(int smallMapSize, Point p, int largeMapEdge) {
        
        for(int i = 1; i < smallMapSize-1; i++){
            int largeMapX = i/smallLargeProportion + p.x;
            if(i%smallLargeProportion < smallLargeProportion/4){
                smallMap[i][0] = new Province(Terra.getNeighbours(largeMapX, p.y, largeMap)[5].getTerrain(), seed, resources,0,i);
                smallMap[i][smallMapSize-1] = 
                        new Province(Terra.getNeighbours(largeMapX, p.y + largeMapEdge-1, largeMap)[4].getTerrain(), seed, resources,smallMapSize-1,i);
            }
                
            else if (i%smallLargeProportion >= smallLargeProportion*3/4){
                smallMap[i][0] = new Province(Terra.getNeighbours(largeMapX, p.y, largeMap)[1].getTerrain(), seed, resources,0,i);
                smallMap[i][smallMapSize-1] = 
                        new Province(Terra.getNeighbours(largeMapX, p.y + largeMapEdge-1, largeMap)[2].getTerrain(), seed, resources,smallMapSize-1,i);
            }
            else {
                smallMap[i][0] = new Province(Terra.getNeighbours(largeMapX, p.y, largeMap)[0].getTerrain(), seed, resources,0,i);
                smallMap[i][smallMapSize-1] = 
                        new Province(Terra.getNeighbours(largeMapX, p.y + largeMapEdge-1, largeMap)[3].getTerrain(), seed, resources,smallMapSize-1,i);
            }
        }
        
        for(int i = 0; i < smallMapSize; i++){
            int largeMapY = i/smallLargeProportion+ p.y;
            if(i%smallLargeProportion < smallLargeProportion/2){
                smallMap[0][i] = new Province(Terra.getNeighbours(p.x, largeMapY, largeMap)[5].getTerrain(), seed, resources,i,0);
                smallMap[smallMapSize-1][i] = 
                        new Province(Terra.getNeighbours(p.x + largeMapEdge-1, largeMapY, largeMap)[4].getTerrain(), seed, resources,i,smallMapSize-1);
            }
            else {
                smallMap[0][i] = new Province(Terra.getNeighbours(p.x, largeMapY, largeMap)[1].getTerrain(), seed, resources,i,0);
                smallMap[smallMapSize-1][i] = 
                        new Province(Terra.getNeighbours(p.x + largeMapEdge-1, largeMapY, largeMap)[2].getTerrain(), seed, resources,i,smallMapSize-1);
            }
        }
    }

    private void createCitiesAndSeas(int largeMapEdge, Point p, int smallMapSize) {
        for(int i = 0; i < largeMapEdge; i++)
            for(int j = 0; j < largeMapEdge; j++){
                if(isLand(largeMap[i+p.y][j+p.x])){
                    int cityY = (int) Math.round((smallLargeProportion) * (i+0.5) + (seed.nextGaussian()*(smallLargeProportion-4)/2));
                    int cityX = (int) Math.round((smallLargeProportion) * (j+0.5) + (seed.nextGaussian()*(smallLargeProportion-4)/2));
                    
                    if (cityY < 2) cityY = 2;   if (cityX < 2) cityX = 2;
                    if (cityY >= smallMapSize-2) cityY = smallMapSize-3;
                    if (cityX >= smallMapSize-2) cityX = smallMapSize-3;
                    //to be sure cities are not created on the edges
                    City c = new City(largeMap[i+p.y][j+p.x].getTerrain(), cityX, cityY);
                    smallMap[cityY][cityX] = c;
                    c.setName(largeMap[i+p.y][j+p.x].getName());
                    int largeMapMirror = seed.nextInt(6);
                    Point p1 = Terra.getNeighbourArrayIndeces(cityY, cityX, smallMap)[largeMapMirror];
                    while(smallMap[p1.x][p1.y]!=null)
                        p1 = Terra.getNeighbourArrayIndeces(cityY, cityX, smallMap)[(largeMapMirror++)%6];
                    smallMap[p1.x][p1.y]= new Province(largeMap[i+p.y][j+p.x]
                            .getTerrain(), largeMap[i+p.y][j+p.x].getResource(),
                            1, p1.y, p1.x, c);
                    ArrayList<Point> nearestSeaPath = Terra.randomBreadthFirstSearchWithObstacles(
                            (Terra t)->{return t != null && t.getTerrain()==Terra.TerrainType.SEA;}, 
                            (Terra t)->{return t==null || t.getTerrain()==Terra.TerrainType.SEA;}, 
                            smallMap, new Point(cityY, cityX)); 
                    //this methods uses the array indeces instead of coordinates
                    if(nearestSeaPath==null) System.out.println("hiç mi liman yok aq");
                    else{
                        for(int pathIterator = 1; pathIterator < nearestSeaPath.size()-1; pathIterator++)
                            smallMap[nearestSeaPath.get(pathIterator).y][nearestSeaPath.get(pathIterator).x] = 
                                    new Province(Terra.TerrainType.SEA, null, 0, 
                                            nearestSeaPath.get(pathIterator).y, 
                                            nearestSeaPath.get(pathIterator).x, null);
                    }
                    //the resource represented in the large map and the port-sea is always present in the smaller map
                }
                else{ //create small sea groups corresponding to larger map for small map
                    int seaY = (int) Math.round((smallLargeProportion) * (i+0.5) + (seed.nextGaussian()*(smallLargeProportion-4)/2));
                    int seaX = (int) Math.round((smallLargeProportion) * (j+0.5) + (seed.nextGaussian()*(smallLargeProportion-4)/2));
                    smallMap[seaY][seaX] = new Province(Terra.TerrainType.SEA, null, 0, seaX, seaY, null);
                    
                    Point p1 = Terra.getNeighbourArrayIndeces(seaY, seaX, smallMap)[seed.nextInt(6)];
                    Point p2 = Terra.getNeighbourArrayIndeces(seaY, seaX, smallMap)[seed.nextInt(6)];
                    while(p2.equals(p1))
                        p2 = Terra.getNeighbourArrayIndeces(seaY, seaX, smallMap)[seed.nextInt(6)];
                    smallMap[p1.x][p1.y]= new Province(Terra.TerrainType.SEA, null, 0, p1.y, p1.x, null);
                    smallMap[p2.x][p2.y]= new Province(Terra.TerrainType.SEA, null, 0, p2.y, p2.x, null);
                }
            }
    }

    private void initializeLargerMap() {
        for (int i = 0; i < largeMap.length; i++) {
            for (int j = 0; j < largeMap[0].length; j++) {
                largeMap[i][j] = new Region(
                        regionNames[seed.nextInt(regionNames.length)],
                        (double)j / largeMap.length, Terra.getNeighbours(j, i, largeMap), Terra.TerrainType.PLAIN, 
                        getTerrainSimilarityCoefficient(setup), getResourceSimilarityCoefficient(), seed, resources, j, i);
                //System.out.print(largeMap[i][j].getTerrain()+ "\t");
            }
            //System.out.println();
        }
    }

    private static double getResourceSimilarityCoefficient() {
        return 0.2;
    }

    private double getTerrainSimilarityCoefficient(Settings gameSetup) {
        return 1.0 - gameSetup.getLargeMapSize() / 10.0;
    }
    

    private int findSmallerMapArea(int mapSize, Point p) {
        ArrayList<Point> possibleLocs = new ArrayList<>();
        int edgeLength = getEdgeLengthFromCityCount(mapSize);
        if(largeMap.length-3 <= edgeLength )
            return -1;
        
        for(int i = 1; i < largeMap.length - edgeLength-1; i++)
            for(int j = 1; j < largeMap[0].length - edgeLength-1; j++){
                int landTileCount = 0; boolean allSeaside = true;
                for(int xIter = 0; xIter < edgeLength; xIter++)
                    for(int yIter = 0; yIter < edgeLength; yIter++){
                        if(largeMap[i+xIter][j+yIter].getTerrain() != Terra.TerrainType.SEA)
                            landTileCount++;
                        if(!coastalTile(largeMap, j+yIter, i+xIter))
                            allSeaside = false;
                    }
                if(allSeaside && landTileCount == mapSize)
                    possibleLocs.add(new Point(j, i));
            }
        if(possibleLocs.isEmpty())
            return findSmallerMapArea(mapSize+1, p);
        else {
            Point temp = possibleLocs.get(seed.nextInt(possibleLocs.size()));
            p.x = temp.x; p.y = temp.y;
            return mapSize;
        }
    }

    private int getEdgeLengthFromCityCount(int mapSize) {
        return (int) Math.round(Math.sqrt(mapSize+1));
    }

    private boolean coastalTile(Region[][] largeMap, int x, int y) {
        if(largeMap[y][x].getTerrain() == Terra.TerrainType.SEA)
            return true;
        Terra[] neighbours = Terra.getNeighbours(x, y, largeMap);
        for(Terra t: neighbours)
            if(t != null && t.getTerrain() == Terra.TerrainType.SEA)
                return true;
        return false;
    }

    private boolean isLand(Terra r) {
        return r.getTerrain() != Terra.TerrainType.SEA;
    }

    private void readNames() throws FileNotFoundException {
        Scanner placesScanner = new Scanner(new File("src/files/placesList.txt"));
        int regionCount = -3, provinceCount = -2; 
        boolean countingModeRegions = true;
        while (placesScanner.hasNextLine()) {
            if(placesScanner.nextLine().equals("Province Names"))
                countingModeRegions = false;
            if(countingModeRegions)
                regionCount++;
            else provinceCount++;
        }
        regionNames = new String[regionCount];
        provinceNames = new String[provinceCount];
        
        placesScanner.close();
        placesScanner = new Scanner(new File("src/files/placesList.txt"));
        
        countingModeRegions = true;
        while (placesScanner.hasNextLine()) {
            String thisLine = placesScanner.nextLine();
            if(thisLine.equals("Province Names"))
                countingModeRegions = false;
            if(countingModeRegions){
                if(!(thisLine.equals("")||thisLine.equals("Region Names")))
                    regionNames[--regionCount] = thisLine;
            }
            else if(!(thisLine.equals("")||thisLine.equals("Province Names")))
                provinceNames[--provinceCount] = thisLine;
        }
        placesScanner.close();
    }
    
    
}
