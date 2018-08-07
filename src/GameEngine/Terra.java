/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameEngine;

import java.util.Random;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.function.Function;
import java.util.ArrayList;
import java.awt.Point;
import java.util.Arrays;
import oracle.jrockit.jfr.events.Bits;

/**
 *
 * @author onur
 */
public class Terra {

    //things are a little bit fucked up with functions below, so instead of p.x,p.y reverse is used for now
    private static int calculateMinHeuristicDistance(Point p1, Point p2) {
        return OffsetCoord.qoffsetToCube(new OffsetCoord(p1.y, p1.x))
                .distance(OffsetCoord.qoffsetToCube(new OffsetCoord(p2.y, p2.x)));
    }
    
    private Faction owner;
    private String name;
    private Resource producedResource;
    private TerrainType terrain;
    protected static double[] terrainProbs;
    public OffsetCoord coord;
    
    protected final static double[] noResourceProbability = {
        1, 0.33, 0, 0, 0.25, 0.1 
    };
    public enum TerrainType{SEA, DESERT, PLAIN, GRASS, FOREST, HILL};

    public Terra(int x, int y) {
        coord = new OffsetCoord(x, y);
    }
    
    public static ArrayList<Point> randomBreadthFirstSearchWithObstacles(
            Function<Terra , Boolean> searchCondition,
            Function<Terra , Boolean> passableCondition,
            Terra[][] map, Point startingPoint){
        Random r = new Random();
        Point[][] resultConstructor = new Point[map.length][map[0].length];
        boolean[][] visitBitMap = new boolean[map.length][map[0].length];
        ArrayList<Point> visitQueue = new ArrayList<>();
        ArrayList<Point> resultPath = new ArrayList<>();
        GM.swapPoint(startingPoint);
        visitQueue.add(startingPoint);
        visitBitMap[startingPoint.y][startingPoint.x] = true;
        int breadthFirstBorder = 1;
        
        do{
            //picks a random path from within the nodes with same distance to the source
            Point p = visitQueue.remove(r.nextInt(breadthFirstBorder--));
            if(searchCondition.apply(map[p.y][p.x])){
                resultPath.add(p);
                break;
            }
            Point[] neighbours = getNeighbourArrayIndeces(p.y, p.x, map);
            for(Point n: neighbours){
                if(n!=null)
                    GM.swapPoint(n);
                if(n!=null && !visitBitMap[n.y][n.x] && passableCondition.apply(map[n.y][n.x])){
                    visitQueue.add(n);
                    visitBitMap[n.y][n.x] = true;
                    resultConstructor[n.y][n.x] = p;
                }
            }
            if(breadthFirstBorder==0)
                breadthFirstBorder = visitQueue.size();
        }while(!visitQueue.isEmpty());
        if(resultPath.isEmpty()) //could not reach any path
            return null;
        else{
            reconstructResult(resultConstructor, resultPath);
        }
        return resultPath;
    }
    
    public static ArrayList<Point> randomAStarWithObstacles(
            Function<Terra , Boolean> passableCondition,
            Terra[][] map, Point startingPoint, Point destination){
        Random r = new Random();
        Point[][] resultConstructor = new Point[map.length][map[0].length];
        int[][] distanceMap = new int[map.length][map[0].length];
        ArrayList<Point> visitQueue = new ArrayList<>();
        ArrayList<Point> resultPath = new ArrayList<>();
        GM.swapPoint(startingPoint);
        visitQueue.add(startingPoint);
        distanceMap[startingPoint.y][startingPoint.x] = 1;
        //starting point starts with an arbitrary distance of 1
        //because 0 is saved for unvisited nodes
        
        //p.y is assumed to be row, p.x is column
        do{
            //picks a random path from within the nodes with same distance to the source
            int equalDistancedPathsCount = 0;
            for(Point temp: visitQueue){
                if(calculateMinHeuristicDistance(visitQueue.get(0),destination) + 
                        distanceMap[visitQueue.get(0).y][visitQueue.get(0).x]
                        == calculateMinHeuristicDistance(temp,destination) + distanceMap[temp.y][temp.x])
                    equalDistancedPathsCount++;
            }
            Point p = visitQueue.remove(r.nextInt(equalDistancedPathsCount));
            if(destination.equals(p)){
                resultPath.add(p);
                break;
            }
            Point[] neighbours = getNeighbourArrayIndeces(p.y, p.x, map);
            for(Point n: neighbours){
                if(n!=null)
                    GM.swapPoint(n);
                if(n!=null && distanceMap[n.y][n.x]==0 && passableCondition.apply(map[n.y][n.x])){
                    boolean insertedFlag = false;
                    for(int i = 0; i < visitQueue.size(); i++){
                        if(calculateMinHeuristicDistance(visitQueue.get(i),destination) + 
                                distanceMap[visitQueue.get(i).y][visitQueue.get(i).x]
                        > calculateMinHeuristicDistance(n,destination) + distanceMap[n.y][n.x])
                        {
                            visitQueue.add(i, n);
                            insertedFlag = true;
                            break;
                        }
                    }
                    if(!insertedFlag)
                        visitQueue.add(n);
                    distanceMap[n.y][n.x] = 1 + distanceMap[p.y][p.x];
                    resultConstructor[n.y][n.x] = p;
                }
            }
        }while(!visitQueue.isEmpty());
        if(resultPath.isEmpty()) //could not reach any path
            return null;
        else{
            reconstructResult(resultConstructor, resultPath);
        }
        return resultPath;
    }

    private static void reconstructResult(Point[][] resultConstructor, ArrayList<Point> resultPath) {
        //last element becomes the original starting point, first element is the found result
        while(resultConstructor[resultPath.get(resultPath.size()-1).y][resultPath.get(resultPath.size()-1).x] != null)
            resultPath.add(resultConstructor[resultPath.get(resultPath.size()-1).y][resultPath.get(resultPath.size()-1).x]);
    }
    
    public void draw(GraphicsContext gc, int x, int y, int edgeLength, boolean[] displaySettings){
        drawTerrain(gc, x , y, edgeLength); 
        if(displaySettings[0] && producedResource!=null){
            producedResource.draw(gc, x, y, edgeLength);
        }
    }

    protected void drawTerrain(GraphicsContext gc, int x, int y, int edgeLength) {
        gc.drawImage(new Image(
                "files/terrain/" + getTerrain().toString().toLowerCase()+ ".png", edgeLength*2, edgeLength*2, true, true),
                x, y );
    }

    public Faction getOwner() {
        return owner;
    }

    public void setOwner(Faction owner) {
        this.owner = owner;
    }

    public Resource getResource() {
        return producedResource;
    }

    protected void setProducedResource(Resource producedResource) {
        if(terrain != TerrainType.SEA)
            this.producedResource = producedResource;
        else this.producedResource = null;
    }

    public TerrainType getTerrain() {
        return terrain;
    }

    protected void setTerrain(TerrainType terrain) {
        this.terrain = terrain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(terrain == TerrainType.SEA)
            this.name = "";
        else this.name = name;
    }
    
    protected void latitudeConfiguration(double latitude) {
        GM.normalizeArray(terrainProbs);
        terrainProbs[1] *= Math.pow(1/terrainProbs[1] , latitude - 0.8); //experimentality desert prob
        terrainProbs[4] *= 0.75*2*(1-latitude) + 0.25; 
        //becomes default value at middle. even at the southernmost parts, there can be forests
        terrainProbs[3] *= 2*(1-latitude); //becomes default value at middle
        GM.normalizeArray(terrainProbs);
    }
    
    protected void decideResource(Terra[] neighbours, double resourceSimilarityCoefficient, Resources resources, Random seed){
        int n = 0;
        for (Terra neighbour : neighbours) {
            if (neighbour != null && neighbour.producedResource != null) {
                n++;
            }
        }
        if(seed.nextDouble() < resourceSimilarityCoefficient * n / neighbours.length
                * (1 - noResourceProbability[terrain.ordinal()])){
            if (!setNonNullNeighbourResource(seed, n, neighbours)) {
                System.out.println("non null neighbour resource has problems");
            }
        }
        else setProducedResource(getRandomResorceForTerrain(terrain, seed, resources));
    }

    private boolean setNonNullNeighbourResource(Random seed, int n, Terra[] neighbours) {
        //no resource probabilty has priority over similarities..
        //also null neighbours are excluded
        int rIndex = seed.nextInt(n);
        for (int i = 0, j = -1; i < neighbours.length; i++) {
            if (neighbours[i] != null && neighbours[i].producedResource != null) {
                if (++j == rIndex) {
                    setProducedResource(neighbours[i].producedResource);
                    return true;
                }
            }
        }
        return false;
    }
    
    protected Resource getRandomResorceForTerrain(TerrainType t, Random seed, Resources r) {
        if(seed.nextDouble()<noResourceProbability[t.ordinal()])
            return null;
        else return r.getResourcesByTerrain(t)[seed.nextInt(r.getResourcesByTerrain(t).length)];
    }
    
    //be toooo careful with this fuckin method
    public static Point[] getNeighbourArrayIndeces(int row, int col, Terra[][] map){
        Point[] nb = new Point[6];
        if(row > 0) nb[0] = new java.awt.Point(row-1,col);
        if(row < map.length-1) nb[3] = new java.awt.Point(row+1,col);
        
        if ((col&1) == 0){
            if(row > 0 && col < map[0].length-1) nb[1] = new java.awt.Point(row-1,col+1);
            if(col < map[0].length-1) nb[2] = new java.awt.Point(row,col+1);
            if(col > 0) nb[4] = new java.awt.Point(row,col-1);
            if(col > 0 && row > 0) nb[5] = new java.awt.Point(row-1,col-1);
        }
        else{
            if(col < map[0].length-1) nb[1] = new java.awt.Point(row,col+1);
            if(row < map.length-1 && col < map[0].length-1) nb[2] = new java.awt.Point(row+1,col+1);
            if(row < map.length-1 && col > 0) nb[4] = new java.awt.Point(row+1,col-1);
            if(col > 0) nb[5] = new java.awt.Point(row,col-1);
        }
        
        return nb;
    }
    public static Terra[] getNeighbours(int col, int row, Terra[][] map){
        Terra[] nb = new Terra[6];
        nb[0] = GM.getMatrix(map, row-1, col);
        nb[3] = GM.getMatrix(map, row+1, col);
        
        if (col%2 == 0){
            nb[1] = GM.getMatrix(map, row-1,col+1);
            nb[2] = GM.getMatrix(map, row,col+1);
            nb[4] = GM.getMatrix(map, row,col-1);
            nb[5] = GM.getMatrix(map, row-1,col-1);
        }
        else{
            nb[1] = GM.getMatrix(map, row,col+1);
            nb[2] = GM.getMatrix(map, row+1,col+1);
            nb[4] = GM.getMatrix(map, row+1,col-1);
            nb[5] = GM.getMatrix(map, row,col-1);
        }
        
        return nb;
    }
    
    public static boolean isTerraIncognita(Terra t){
        return t == null || t.getOwner() == null;
    }
    
    public static Point[] getNeighbourArrayIndecesNoNulls(int row, int col, Terra[][] map){
        /*hacks and hacks
        this method pushes the inputs to an acceptable region in the array
        so it never returns null points from the get neighbour indices method
        
        however adding column number with a random nnumber was problematic
        because it was unstable. 
        forcing the added number to be always even was necessary
        otherwise it changed the returned neighbours
        */
        Point[] nb = getNeighbourArrayIndeces(map.length/2, (col&1) + 2, map);
        for(Point p: nb)
        {   
            p.x -=  map.length/2 - row;
            p.y -= ((col&1) + 2) - col;
        }
        return nb;
    }
}
