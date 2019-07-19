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

/**
 *
 * rivers? wave function collapse in github can be used to create additional
 * terrains
 *
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
    private String[] regionNames, provinceNames;
    private Random seed;
    private int smallLargeProportion, largeMapEdge, cityCountInSmallMap;

    // p.x is col, p.y is row
    private Point cornerLargeMapCoords;

    public Game(Settings gameSetup) throws FileNotFoundException {
        smallLargeProportion = 2 * (1 + gameSetup.getSmallMapSize());
        setup = gameSetup;
        seed = new Random();
        resources = new Resources();
        readNames();
        cornerLargeMapCoords = new Point();
        initializeMap();
        initializeFactions();
    }

    private void initializeMap() {
        int largeMapSize = setup.getLargeMapSize() + 6;
        largeMap = new Region[largeMapSize][largeMapSize * 2];
        initializeLargerMap();
        initializeSmallerMap();
    }

    private void initializeSmallerMap() {
        int smallMapDesiredCityCount = setup.getSmallMapSize() + 2;
        cityCountInSmallMap = findSmallerMapArea(smallMapDesiredCityCount, cornerLargeMapCoords);
        //so parameter is a minimum of 3 always
        //small maps large map equivalent's top left corner is stored in p

        //omitted for a while to see actual large maps
        if (cityCountInSmallMap == -1) {
            System.out.println("err couldn't file appropriate place");
            initializeMap();
            //if no appropriate large map is created, tries to create maps again
            return;
        }

        largeMapEdge = getEdgeLengthFromCityCount(cityCountInSmallMap);

        int smallMapEdge = largeMapEdge * smallLargeProportion;
        //assuming every hex on strategy map corresponds to 4*4 hexes on small map
        smallMap = new Province[smallMapEdge][smallMapEdge];

        //these two lines are for random generation of names
        //even if they are not explicitly created in this class
        Province.setPrivateSeed(seed);
        Province.setProvinceNamesList(provinceNames);

        createSmallMapBorderTerrains(smallMapEdge, cornerLargeMapCoords, largeMapEdge);
        createCitiesAndSeas(largeMapEdge, cornerLargeMapCoords, smallMapEdge);
        fillRestSmallMap(cornerLargeMapCoords, largeMapEdge);
        setInitialAltitudes(smallMap);
        powerIterateAltitudes(smallMap);
        createSmallMapRivers(largeMapEdge);
    }

    private void fillRestSmallMap(Point p, int largeMapEdge) {
        for (int i = 0; i < smallMap.length; i++) {
            for (int j = 0; j < smallMap[0].length; j++) {
                if (smallMap[i][j] == null) {
                    smallMap[i][j] = new Province(Terra.getNeighbours(j, i, smallMap),
                            largeMap[i / smallLargeProportion + p.y][j / smallLargeProportion + p.x],
                            getProvinceRegionTerrainSimilarity(), getProvinceRegionResourceSimilarity(),
                            getProvinceTerrainNeighbourhoodSimilarity(), getProvinceNeighbourhoodResourceSimilarity(),
                            (i / smallLargeProportion + p.y + .0) / largeMapEdge, seed,
                            provinceNames[seed.nextInt(provinceNames.length)], resources,
                            j, i);
                }
                //System.out.print(smallMap[i][j].getTerrain()+ "\t");
            }
            //System.out.println();
        }
    }

    private double getProvinceRegionTerrainSimilarity() {
        return 0.3;
    }

    private double getProvinceRegionResourceSimilarity() {
        return 0.1;
    }

    private double getProvinceTerrainNeighbourhoodSimilarity() {
        return 0.4;
    }

    private double getProvinceNeighbourhoodResourceSimilarity() {
        return 0.3;
    }

    private void createSmallMapBorderTerrains(int smallMapSize, Point p, int largeMapEdge) {
        for (int i = 1; i < smallMapSize - 1; i++) {
            int largeMapX = i / smallLargeProportion + p.x;
            if (i % smallLargeProportion < smallLargeProportion / 4) {
                smallMap[0][i] = new Province(Terra.getNeighbours(largeMapX, p.y, largeMap)[5], seed, resources, 0, i);
                smallMap[smallMapSize - 1][i]
                        = new Province(Terra.getNeighbours(largeMapX, p.y + largeMapEdge - 1, largeMap)[4], seed, resources, smallMapSize - 1, i);
            } else if (i % smallLargeProportion >= smallLargeProportion * 3 / 4) {
                smallMap[0][i] = new Province(Terra.getNeighbours(largeMapX, p.y, largeMap)[1], seed, resources, 0, i);
                smallMap[smallMapSize - 1][i]
                        = new Province(Terra.getNeighbours(largeMapX, p.y + largeMapEdge - 1, largeMap)[2], seed, resources, smallMapSize - 1, i);
            } else {
                smallMap[0][i] = new Province(Terra.getNeighbours(largeMapX, p.y, largeMap)[0], seed, resources, 0, i);
                smallMap[smallMapSize - 1][i]
                        = new Province(Terra.getNeighbours(largeMapX, p.y + largeMapEdge - 1, largeMap)[3], seed, resources, smallMapSize - 1, i);
            }
        }

        for (int i = 0; i < smallMapSize; i++) {
            int largeMapY = i / smallLargeProportion + p.y;
            if (i % smallLargeProportion < smallLargeProportion / 2) {
                smallMap[i][0] = new Province(Terra.getNeighbours(p.x, largeMapY, largeMap)[5], seed, resources, i, 0);
                smallMap[i][smallMapSize - 1]
                        = new Province(Terra.getNeighbours(p.x + largeMapEdge - 1, largeMapY, largeMap)[1], seed, resources, i, smallMapSize - 1);
            } else {
                smallMap[i][0] = new Province(Terra.getNeighbours(p.x, largeMapY, largeMap)[4], seed, resources, i, 0);
                smallMap[i][smallMapSize - 1]
                        = new Province(Terra.getNeighbours(p.x + largeMapEdge - 1, largeMapY, largeMap)[2], seed, resources, i, smallMapSize - 1);
            }
        }
    }

    private void createCitiesAndSeas(int largeMapEdge, Point p, int smallMapSize) {
        for (int i = 0; i < largeMapEdge; i++) {
            for (int j = 0; j < largeMapEdge; j++) {
                largeMap[i + p.y][j + p.x].setAppearsInSmallMap(true);
                if (isLand(largeMap[i + p.y][j + p.x])) {
                    int cityY = (int) Math.round((smallLargeProportion) * (i + 0.5) + (seed.nextGaussian() * (smallLargeProportion - 4) / 2));
                    int cityX = (int) Math.round((smallLargeProportion) * (j + 0.5) + (seed.nextGaussian() * (smallLargeProportion - 4) / 2));

                    if (cityY < 2) {
                        cityY = 2;
                    }
                    if (cityX < 2) {
                        cityX = 2;
                    }
                    if (cityY >= smallMapSize - 2) {
                        cityY = smallMapSize - 3;
                    }
                    if (cityX >= smallMapSize - 2) {
                        cityX = smallMapSize - 3;
                    }
                    //to be sure cities are not created on the edges
                    City c = new City(largeMap[i + p.y][j + p.x], cityX, cityY);
                    smallMap[cityY][cityX] = c;
                    c.setName(largeMap[i + p.y][j + p.x].getName());
                    int largeMapMirror = seed.nextInt(6);
                    Point p1 = Terra.getNeighbourArrayIndeces(cityY, cityX, smallMap)[largeMapMirror];
                    while (smallMap[p1.x][p1.y] != null) {
                        p1 = Terra.getNeighbourArrayIndeces(cityY, cityX, smallMap)[(largeMapMirror++) % 6];
                    }
                    smallMap[p1.x][p1.y] = new Province(largeMap[i + p.y][j + p.x].getTerrain(),
                            largeMap[i + p.y][j + p.x].getResource(),
                            1, p1.x, p1.y, c, largeMap[i + p.y][j + p.x].getPseudoAltitude());
                    ArrayList<Point> nearestSeaPath = Terra.randomBreadthFirstSearchWithObstacles(
                            (Terra t) -> {
                                return t != null && t.getTerrain() == Terra.TerrainType.SEA;
                            },
                            (Terra t) -> {
                                return t == null || t.getTerrain() == Terra.TerrainType.SEA;
                            },
                            smallMap, new Point(cityY, cityX));
                    //this methods uses the array indeces instead of coordinates
                    if (nearestSeaPath == null) {
                        System.out.println("hiç mi liman yok aq");
                    } else {
                        for (int pathIterator = 1; pathIterator < nearestSeaPath.size() - 1; pathIterator++) {
                            smallMap[nearestSeaPath.get(pathIterator).y][nearestSeaPath.get(pathIterator).x]
                                    = new Province(Terra.TerrainType.SEA, null, 0,
                                            nearestSeaPath.get(pathIterator).y,
                                            nearestSeaPath.get(pathIterator).x, null,
                                            smallMap[nearestSeaPath.get(0).y][nearestSeaPath.get(0).x].getPseudoAltitude());
                        }
                    }
                    //the resource represented in the large map and the port-sea is always present in the smaller map
                } else { //create small sea groups corresponding to larger map for small map
                    int seaY = (int) Math.round((smallLargeProportion) * (i + 0.5) + (seed.nextGaussian() * (smallLargeProportion - 4) / 2));
                    int seaX = (int) Math.round((smallLargeProportion) * (j + 0.5) + (seed.nextGaussian() * (smallLargeProportion - 4) / 2));
                    if (seaY < 2) {
                        seaY = 2;
                    }
                    if (seaX < 2) {
                        seaX = 2;
                    }
                    if (seaY >= smallMapSize - 2) {
                        seaY = smallMapSize - 3;
                    }
                    if (seaX >= smallMapSize - 2) {
                        seaX = smallMapSize - 3;
                    }
                    smallMap[seaY][seaX] = new Province(Terra.TerrainType.SEA, null, 0, seaY, seaX, null, largeMap[i + p.y][j + p.x].getPseudoAltitude());

                    Point p1 = Terra.getNeighbourArrayIndeces(seaY, seaX, smallMap)[seed.nextInt(6)];
                    Point p2 = Terra.getNeighbourArrayIndeces(seaY, seaX, smallMap)[seed.nextInt(6)];
                    while (p2.equals(p1)) {
                        p2 = Terra.getNeighbourArrayIndeces(seaY, seaX, smallMap)[seed.nextInt(6)];
                    }
                    smallMap[p1.x][p1.y] = new Province(Terra.TerrainType.SEA, null, 0, p1.x, p1.y, null, largeMap[i + p.y][j + p.x].getPseudoAltitude());
                    smallMap[p2.x][p2.y] = new Province(Terra.TerrainType.SEA, null, 0, p2.x, p2.y, null, largeMap[i + p.y][j + p.x].getPseudoAltitude());
                }
            }
        }
    }

    private void initializeLargerMap() {
        // create a mediterranean look by adding a huge water area
        int arbitrarySeaRootsCount = 5;
        for (int i = -1; i < arbitrarySeaRootsCount;) {
            int seaY = seed.nextInt(largeMap.length);
            int seaX = seed.nextInt(largeMap[0].length);
            if (largeMap[seaY][seaX] == null) {
                largeMap[seaY][seaX] = new Region(seaX, seaY);
                i++;
            }
            if (i > 0) {
                ArrayList<Point> nearestSeaPath = Terra.randomBreadthFirstSearchWithObstacles(
                        (Terra t) -> {
                            return t != null && t.getTerrain() == Terra.TerrainType.SEA && t != largeMap[seaY][seaX];
                        },
                        (Terra t) -> {
                            return t == null || t.getTerrain() == Terra.TerrainType.SEA;
                        },
                        largeMap, new Point(seaY, seaX));
                for (int pathIterator = 1; pathIterator < nearestSeaPath.size() - 1; pathIterator++) {
                    largeMap[nearestSeaPath.get(pathIterator).y][nearestSeaPath.get(pathIterator).x]
                            = new Region(nearestSeaPath.get(pathIterator).x, nearestSeaPath.get(pathIterator).y);
                }
            }

        }

        for (int i = 0; i < largeMap.length; i++) {
            for (int j = 0; j < largeMap[0].length; j++) {
                if (largeMap[i][j] == null) {
                    largeMap[i][j] = new Region(
                            regionNames[seed.nextInt(regionNames.length)],
                            (double) i / largeMap.length, Terra.getNeighbours(j, i, largeMap), Terra.TerrainType.PLAIN,
                            getTerrainSimilarityCoefficient(setup), getResourceSimilarityCoefficient(), seed, resources, j, i);
                }
                //System.out.print(largeMap[i][j].getTerrain()+ "\t");
            }
            //System.out.println();
        }

        //after all hexes are decided the rivers are created
        //from a pseudo altitude map
        setInitialAltitudes(largeMap);
        powerIterateAltitudes(largeMap);
        createFreeRivers(largeMap);
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
        if (largeMap.length - 3 <= edgeLength) {
            return -1;
        }

        for (int i = 1; i < largeMap.length - edgeLength - 1; i++) {
            for (int j = 1; j < largeMap[0].length - edgeLength - 1; j++) {
                int landTileCount = 0;
                boolean allSeaside = true;
                for (int xIter = 0; xIter < edgeLength; xIter++) {
                    for (int yIter = 0; yIter < edgeLength; yIter++) {
                        if (largeMap[i + xIter][j + yIter].getTerrain() != Terra.TerrainType.SEA) {
                            landTileCount++;
                        }
                        if (!coastalTile(largeMap, j + yIter, i + xIter)) {
                            allSeaside = false;
                        }
                    }
                }
                if (allSeaside && landTileCount == mapSize) {
                    possibleLocs.add(new Point(j, i));
                }
            }
        }
        if (possibleLocs.isEmpty()) {
            return findSmallerMapArea(mapSize + 1, p);
        } else {
            Point temp = possibleLocs.get(seed.nextInt(possibleLocs.size()));
            p.x = temp.x;
            p.y = temp.y;
            return mapSize;
        }
    }

    private int getEdgeLengthFromCityCount(int mapSize) {
        return (int) Math.round(Math.sqrt(mapSize + 1));
    }

    private boolean coastalTile(Region[][] largeMap, int x, int y) {
        if (largeMap[y][x].getTerrain() == Terra.TerrainType.SEA) {
            return true;
        }
        Terra[] neighbours = Terra.getNeighbours(x, y, largeMap);
        for (Terra t : neighbours) {
            if (t != null && t.getTerrain() == Terra.TerrainType.SEA) {
                return true;
            }
        }
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
            if (placesScanner.nextLine().equals("Province Names")) {
                countingModeRegions = false;
            }
            if (countingModeRegions) {
                regionCount++;
            } else {
                provinceCount++;
            }
        }
        regionNames = new String[regionCount];
        provinceNames = new String[provinceCount];

        placesScanner.close();
        placesScanner = new Scanner(new File("src/files/placesList.txt"));

        countingModeRegions = true;
        while (placesScanner.hasNextLine()) {
            String thisLine = placesScanner.nextLine();
            if (thisLine.equals("Province Names")) {
                countingModeRegions = false;
            }
            if (countingModeRegions) {
                if (!(thisLine.equals("") || thisLine.equals("Region Names"))) {
                    regionNames[--regionCount] = thisLine;
                }
            } else if (!(thisLine.equals("") || thisLine.equals("Province Names"))) {
                provinceNames[--provinceCount] = thisLine;
            }
        }
        placesScanner.close();
    }

    private void setInitialAltitudes(Terra[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                Terra t = map[i][j];
                if (t.getTerrain() == Terra.TerrainType.HILL) {
                    t.setPseudoAltitude(1 + t.getPseudoAltitude());
                } else if (t.getTerrain() == Terra.TerrainType.FOREST) {
                    t.setPseudoAltitude(0.75 + t.getPseudoAltitude());
                } else if (t.getTerrain() != Terra.TerrainType.SEA) {
                    t.setPseudoAltitude(0.5 + t.getPseudoAltitude());
                } else if (t.isLake(map)) {
                    t.setPseudoAltitude(0.75 + t.getPseudoAltitude());
                } else {
                    // if a sea but not a lake
                    //default is 0 anyway for sea..
                    t.setPseudoAltitude(0);
                }
            }
        }
    }

    private void powerIterateAltitudes(Terra[][] map) {
        double[][] altitudes = new double[map.length][map[0].length];
        double[][] iterationAltitudes = new double[map.length][map[0].length];

        //a single plain island tile would have an altitude of 0.05 after 19 iterations
        double diffusionRate = 0.05;
        int iterationCount = 15;

        //initialize power iterator..
        for (int i = 0; i < altitudes.length; i++) {
            for (int j = 0; j < altitudes[0].length; j++) {
                altitudes[i][j] = map[i][j].getPseudoAltitude();
            }
        }
        for (int iteration = 0; iteration < iterationCount; iteration++) {
            for (int i = 0; i < altitudes.length; i++) {
                for (int j = 0; j < altitudes[0].length; j++) {
                    double avgNbAltitude = getAverageNeighbourAltitudeDuringIteration(i, j, map, altitudes);

                    //seas will stay seas.. at 0 altitode
                    if (altitudes[i][j] != 0) {
                        iterationAltitudes[i][j] = (1 - diffusionRate) * altitudes[i][j]
                                + diffusionRate * avgNbAltitude;
                    }
                }
            }
            double[][] t = altitudes;
            altitudes = iterationAltitudes;
            iterationAltitudes = t;
        }

        //set iterated altitudes for terrains
        for (int i = 0; i < altitudes.length; i++) {
            for (int j = 0; j < altitudes[0].length; j++) {
                map[i][j].setPseudoAltitude(altitudes[i][j]);
            }
        }
    }

    private double getAverageNeighbourAltitudeDuringIteration(int i, int j, Terra[][] map, double[][] altitudes) {
        double sumNeighbourAltitude = 0;
        int numNb = 0;
        Point[] nb = Terra.getNeighbourArrayIndeces(i, j, map);
        for (Point p : nb) {
            if (p != null) {
                numNb++;
                sumNeighbourAltitude += altitudes[p.x][p.y];
            }
        }
        double avgNbAltitude = sumNeighbourAltitude / numNb;
        return avgNbAltitude;
    }

    private double getAverageNeighbourAltitude(int i, int j, Terra[][] map) {
        double sumNeighbourAltitude = 0;
        int numNb = 0;
        Terra[] nb = Terra.getNeighbours(i, j, map);
        for (Terra p : nb) {
            if (p != null) {
                numNb++;
                sumNeighbourAltitude += p.getPseudoAltitude();
            }
        }
        double avgNbAltitude = sumNeighbourAltitude / numNb;
        return avgNbAltitude;
    }

    private void createFreeRivers(Terra[][] map) {
        for (int row = -1; row <= map.length; row++) {
            for (int col = -1; col <= map[0].length; col++) {
                Terra t = GM.getMatrix(map, row, col);
                if (t == null && getRiverChanceForAltitude(getAverageNeighbourAltitude(row, col, map) + seed.nextDouble())) {
                    startRiver(map, row, col);
                } else if (t == null); else if (t.isLake(map)) {
                    startRiver(map, row, col);
                } else if (getRiverChanceForAltitude(t.getPseudoAltitude())) {
                    startRiver(map, row, col);
                }
            }
        }
    }

    //neeeds to be tuned well
    private boolean getRiverChanceForAltitude(double alt) {
        return alt - 0.55 > seed.nextDouble();
    }

    private void startRiver(Terra[][] map, int row, int col) {
        //check if there's already a river, or if we encountered higher places than riverbed
        double sourceAltitude;
        Terra t = GM.getMatrix(map, row, col);
        if (t == null) {
            sourceAltitude = getAverageNeighbourAltitude(row, col, map) + seed.nextDouble() - 0.8;
        } else {
            sourceAltitude = t.getPseudoAltitude();
        }

        Terra[] nb = Terra.getNeighbours(col, row, map);
        double[] possibleDirectionAltitudes = new double[6];
        int possibleDirectionCount = 0;
        for (int i = 0; i < 6; i++) //if this direction is not possible
        {
            if (nb[i] == null || nb[(i + 1) % 6] == null
                    || nb[i].getPseudoAltitude() <= 0
                    || nb[(i + 1) % 6].getPseudoAltitude() <= 0) {
                possibleDirectionAltitudes[i] = -1;
            } else {
                possibleDirectionAltitudes[i] = (nb[i].getPseudoAltitude() + nb[(i + 1) % 6].getPseudoAltitude()) / 2;
                if (sourceAltitude < possibleDirectionAltitudes[i]) {
                    possibleDirectionAltitudes[i] = -1;
                } else {
                    possibleDirectionCount++;
                }
            }
        }
        if (possibleDirectionCount > 0) {
            double[] slopes = new double[possibleDirectionCount];
            int[] directionIndices = new int[possibleDirectionCount];
            double minSlope = 1;
            for (int i = 0, j = 0; i < 6; i++) {
                if (possibleDirectionAltitudes[i] > 0) {
                    directionIndices[j] = i;
                    slopes[j] = sourceAltitude - possibleDirectionAltitudes[i];
                    if (slopes[j] < minSlope) {
                        minSlope = slopes[j];
                    }
                    j++;
                }
            }

            for (int i = 0; i < slopes.length; i++) {
                if (slopes[i] <= minSlope * (1 + seed.nextGaussian())) {
                    continueRiver(row, col, map, directionIndices[i]);
                }
            }
        }
    }

    /*
     0 is illegal altitudes are encountered and river is not created
     1 is no river created because there's already one, or a sea tile exists
     2 is successfully a river created
     */
    private int continueRiver(int row, int col, Terra[][] map, int directionIndex) {
        try {
            Terra[] nb = Terra.getNeighbours(col, row, map);
            Terra t = GM.getMatrix(map, row, col);

            Terra leftNb = nb[directionIndex];
            Terra rightNb = nb[(directionIndex + 1) % 6];

            // if we end up in complete null stuff there's no river
            if (t == null && (leftNb == null || rightNb == null)) {
                return 0;
            }
            //means we reached to a sea, lake or another river without a problem
            if (leftNb == null || leftNb.getTerrain() == Terra.TerrainType.SEA || leftNb.getRivers()[(directionIndex + 2) % 6]
                    || rightNb == null || rightNb.getTerrain() == Terra.TerrainType.SEA) {
                System.out.println("reached final river at:\n" + row + " " + col + "\n" + directionIndex);
                return 1;
            }

            Terra diagonalNb = Terra.getNeighbours(leftNb.coord.col, leftNb.coord.row, map)[(directionIndex + 1) % 6];
            double lalt = leftNb.getPseudoAltitude();
            double ralt = rightNb.getPseudoAltitude();
            double dalt;
            if (diagonalNb == null) {
                dalt = (lalt + ralt) / 2;
            } else {
                dalt = diagonalNb.getPseudoAltitude();
            }

            // rivers can't go upwards
            int riverCourse;
            if (dalt > ralt && dalt > lalt) {
                return 0;
            } else if (ralt >= dalt && lalt >= dalt) {
                if (ralt > lalt) {
                    riverCourse = continueRiver(rightNb.coord.row, rightNb.coord.col, map, (directionIndex + 5) % 6);
                    if ((lalt - dalt) / (ralt - dalt) > seed.nextDouble()) {
                        riverCourse += continueRiver(leftNb.coord.row, leftNb.coord.col, map, (directionIndex + 1) % 6);
                    }
                } else {
                    riverCourse = continueRiver(leftNb.coord.row, leftNb.coord.col, map, (directionIndex + 1) % 6);
                    if ((ralt - dalt) / (lalt - dalt) > seed.nextDouble()) {
                        riverCourse += continueRiver(rightNb.coord.row, rightNb.coord.col, map, (directionIndex + 5) % 6);
                    }
                }
            } else if (ralt >= dalt) {
                riverCourse = continueRiver(rightNb.coord.row, rightNb.coord.col, map, (directionIndex + 5) % 6);
            } else {
                riverCourse = continueRiver(leftNb.coord.row, leftNb.coord.col, map, (directionIndex + 1) % 6);
            }
            if (riverCourse > 0) {
                System.out.print(riverCourse);
                leftNb.getRivers()[(directionIndex + 2) % 6] = true;
                rightNb.getRivers()[(directionIndex + 5) % 6] = true;
                riverCourse = 2;
                // this is still needed for debugging
                System.out.println("Drawing river at:\n" + row + " " + col + "\n" + directionIndex);
                System.out.println("Branches:\t" + ((leftNb.getRivers()[(directionIndex + 1) % 6]) ? " left branch taken " : "")
                        + ((rightNb.getRivers()[directionIndex]) ? " right branch taken " : ""));
            }
            return riverCourse;
        } catch (StackOverflowError e) {
            return 0;
        }
    }

    private void createSmallMapRivers(int largeMapEdge) {
        createMapEdgeRivers(largeMapEdge);
        for (int row = cornerLargeMapCoords.y; row < cornerLargeMapCoords.y + largeMapEdge; row++) {
            for (int col = cornerLargeMapCoords.x; col < cornerLargeMapCoords.x + largeMapEdge; col++) {
                if (largeMap[row][col].getRivers()[0]) {
                    riverAttempt(smallMap, (row - cornerLargeMapCoords.y) * smallLargeProportion + 1, (int) ((col - cornerLargeMapCoords.x + 0.50) * smallLargeProportion) / 2 * 2, 0, smallLargeProportion / 2);
                }
                if (largeMap[row][col].getRivers()[1]) {
                    riverAttempt(smallMap, (int) ((row - cornerLargeMapCoords.y + 0.25) * smallLargeProportion), ((col - cornerLargeMapCoords.x + 1) * smallLargeProportion) - 1, 1, smallLargeProportion / 4);
                }
                if (largeMap[row][col].getRivers()[5]) {
                    riverAttempt(smallMap, (int) ((row - cornerLargeMapCoords.y + 0.25) * smallLargeProportion), ((col - cornerLargeMapCoords.x) * smallLargeProportion) + 1, 5, smallLargeProportion / 4);
                }
                if (largeMap[row][col].getRivers()[3]) {
                    riverAttempt(smallMap, (row - cornerLargeMapCoords.y + 1) * smallLargeProportion - 1, (int) ((col - cornerLargeMapCoords.x + 0.50) * smallLargeProportion) / 2 * 2, 3, smallLargeProportion / 2);
                }
                if (largeMap[row][col].getRivers()[2]) {
                    riverAttempt(smallMap, (int) ((row - cornerLargeMapCoords.y + 0.75) * smallLargeProportion), ((col - cornerLargeMapCoords.x + 1) * smallLargeProportion) - 1, 2, smallLargeProportion / 4);
                }
                if (largeMap[row][col].getRivers()[4]) {
                    riverAttempt(smallMap, (int) ((row - cornerLargeMapCoords.y + 0.75) * smallLargeProportion), ((col - cornerLargeMapCoords.x) * smallLargeProportion) + 1, 4, smallLargeProportion / 4);
                }
            }
        }
    }

    private void createMapEdgeRivers(int largeMapEdge) {
        for (int row = cornerLargeMapCoords.y; row < cornerLargeMapCoords.y + largeMapEdge; row++) {
            for (int col = cornerLargeMapCoords.x; col < cornerLargeMapCoords.x + largeMapEdge; col++) {
                Terra[] nb = Terra.getNeighbours(col, row, largeMap);
                if (nb[0] != null && nb[0].getRivers()[2] && !nb[0].doesAppearInSmallMap() && nb[1] != null && !nb[1].doesAppearInSmallMap()) {
                    riverAttempt(smallMap, 0, (int) ((col - cornerLargeMapCoords.x + 0.75) * smallLargeProportion) / 2 * 2, 2, 0);
                }
                if (nb[1] != null && nb[1].getRivers()[3] && !nb[1].doesAppearInSmallMap() && nb[2] != null && !nb[2].doesAppearInSmallMap()) {
                    riverAttempt(smallMap, (row - cornerLargeMapCoords.y) / 2 + smallLargeProportion / 2, smallMap[0].length - 1, 3, 0);
                }
                if (nb[2] != null && nb[2].getRivers()[4] && !nb[2].doesAppearInSmallMap() && nb[3] != null && !nb[3].doesAppearInSmallMap()) {
                    riverAttempt(smallMap, smallMap.length - 1, (int) ((col - cornerLargeMapCoords.x + 0.75) * smallLargeProportion) / 2 * 2 + 1, 4, 0);
                }
                if (nb[3] != null && nb[3].getRivers()[5] && !nb[3].doesAppearInSmallMap() && nb[4] != null && !nb[4].doesAppearInSmallMap()) {
                    riverAttempt(smallMap, smallMap.length - 1, (int) ((col - cornerLargeMapCoords.x + 0.25) * smallLargeProportion) / 2 * 2, 5, 0);
                }
                if (nb[4] != null && nb[4].getRivers()[0] && !nb[4].doesAppearInSmallMap() && nb[5] != null && !nb[5].doesAppearInSmallMap()) {
                    riverAttempt(smallMap, (row - cornerLargeMapCoords.y) / 2 + smallLargeProportion / 2, 0, 0, 0);
                }
                if (nb[5] != null && nb[5].getRivers()[1] && !nb[5].doesAppearInSmallMap() && nb[0] != null && !nb[0].doesAppearInSmallMap()) {
                    riverAttempt(smallMap, 0, (int) ((col - cornerLargeMapCoords.x + 0.25) * smallLargeProportion) / 2 * 2 + 1, 1, 0);
                }
            }
        }
    }

    // this method tries to create a river including the hex given in row,col
    // source is tried to be located at distance away from the given hex
    private void riverAttempt(Terra[][] map, int row, int col, int direction, int distance) {
        Point primarySource = Terra.getNeighbourArrayIndecesNoNulls(row, col, map)[(direction + 5) % 6];
        for (int i = 0; i < distance / 2; i++) {
            primarySource = Terra.getNeighbourArrayIndecesNoNulls(primarySource.x, primarySource.y, map)[(direction + 5) % 6];
            primarySource = Terra.getNeighbourArrayIndecesNoNulls(primarySource.x, primarySource.y, map)[(direction + 4) % 6];
        }
        Point secondarySource = Terra.getNeighbourArrayIndecesNoNulls(row, col, map)[(direction + 1) % 6];
        for (int i = 0; i < distance / 2; i++) {
            secondarySource = Terra.getNeighbourArrayIndecesNoNulls(secondarySource.x, secondarySource.y, map)[(direction + 1) % 6];
            secondarySource = Terra.getNeighbourArrayIndecesNoNulls(secondarySource.x, secondarySource.y, map)[(direction + 2) % 6];
        }
        int trial = continueRiver(primarySource.x, primarySource.y, map, (direction + 1) % 6);
        if (trial != 2) {
            trial = continueRiver(secondarySource.x, secondarySource.y, map, (direction + 4) % 6);
        }
        if (trial != 2) {
            // add padding for breadth first search, otherwise stops at borderplaces
            Terra[][] tMap = new Terra[map.length + 2][map[0].length + 2];
            for (int i = -1; i <= map.length; i++) {
                for (int j = -1; j <= map[0].length; j++) {
                    Terra t = GM.getMatrix(map, i, j);
                    if (t == null) {
                        t = new Province(j, i);
                    }
                    tMap[i + 1][j + 1] = t;
                }
            }
            try {
                Terra.randomBreadthFirstSearchWithObstacles(
                        (Terra t) -> {
                            return continueRiver(t.coord.row, t.coord.col, map, (direction + 1) % 6) == 2;
                        },
                        (Terra t) -> {
                            return t != null;
                        }, tMap, new Point(primarySource.x + 1, primarySource.y + 1));
            } catch (ArrayIndexOutOfBoundsException e) {
                Terra.randomBreadthFirstSearchWithObstacles(
                        (Terra t) -> {
                            return continueRiver(t.coord.row, t.coord.col, map, (direction + 4) % 6) == 2;
                        },
                        (Terra t) -> {
                            return t != null;
                        }, tMap, new Point(secondarySource.x + 1, secondarySource.y + 1));
            }
        }
    }

    private void initializeFactions() {
        ArrayList<Culture> landCultures = Culture.getImperialCultures();
        ArrayList<Culture> seaCultures = Culture.getMaritimeCultures();
        player = new Faction(seaCultures.get(seed.nextInt(seaCultures.size())));
        ArrayList<City> cities = new ArrayList<>();
        for (int i = 0; i < largeMapEdge; i++) {
            for (int j = 0; j < largeMapEdge; j++) {
                if (largeMap[cornerLargeMapCoords.y + i][cornerLargeMapCoords.x + j].getSmallMapCity() != null) {
                    cities.add(largeMap[cornerLargeMapCoords.y + i][cornerLargeMapCoords.x + j].getSmallMapCity());
                }
            }
        }
        System.out.println(cities.size());
        // large nations that also exist in small map are created
        for (int i = 0; i < setup.getNumberOfSuperPowers(); i++) {
            Culture cult = landCultures.remove(seed.nextInt(landCultures.size()));
            AIFaction faction = new AIFaction(cult);
            City foothold = cities.remove(seed.nextInt(cities.size()));
            foothold.setOwner(faction);
            Region mainRegion = foothold.getRegionEquivalent();
            mainRegion.setOwner(faction);
            System.out.println(foothold.coord.getIndexPoint());
            System.out.println(mainRegion.coord.getIndexPoint());

            // other than the city in magna graecia
            int initialRegionCount = seed.nextInt(5) + 1;
            System.out.println("Created superpower " + cult.getCountryName() + " with " + (initialRegionCount + 1) + " regions");
            for (int j = 0; j < initialRegionCount; j++) {
                Point closestMainLand
                        = Terra.randomBreadthFirstSearchWithObstacles(
                                (Terra t) -> {
                                    return t != null && isLand(t) && t.getOwner() == null && !t.doesAppearInSmallMap();
                                },
                                (Terra t) -> {
                                    return t != null && (t.getOwner() == null || t.getOwner() == faction);
                                }, largeMap, mainRegion.coord.getIndexPoint()).get(0);
                mainRegion = largeMap[closestMainLand.y][closestMainLand.x];
                GM.swapPoint(closestMainLand);
                System.out.println(foothold.coord.getPoint());
                mainRegion.setOwner(faction);
            }
        }

        // player faction is created, but first initialize the player..
        City playerCity = cities.remove(seed.nextInt(cities.size()));
        playerCity.setOwner(player);
        playerCity.getRegionEquivalent().setOwner(player);

        // city states are created
        for (City c : cities) {
            Culture cult = seaCultures.get(seed.nextInt(seaCultures.size()));
            AIFaction faction = new AIFaction(cult);
            c.setOwner(faction);
            c.getRegionEquivalent().setOwner(faction);
        }

        for (int i = 0; i < setup.getNumberOfForeignPowers(); i++) {
            // a city state civ is created
            if (seed.nextInt(landCultures.size() + seaCultures.size()) < seaCultures.size()) {
                Culture cult = seaCultures.get(seed.nextInt(seaCultures.size()));
                AIFaction faction = new AIFaction(cult);
                Point randomCity = new Point(seed.nextInt(largeMap.length), seed.nextInt(largeMap[0].length));
                randomCity
                            = Terra.randomBreadthFirstSearchWithObstacles(
                                    (Terra t) -> {
                                        return t != null && isLand(t) && t.getOwner() == null && !t.doesAppearInSmallMap() && coastalTile(largeMap, t.coord.col, t.coord.row);
                                    },
                                    (Terra t) -> {
                                        return t != null;
                                    }, largeMap, randomCity).get(0);
                    Region mainRegion = largeMap[randomCity.y][randomCity.x];
                    mainRegion.setOwner(faction);
                    
                    GM.swapPoint(randomCity);
                    System.out.println("Created city-state in " + randomCity);
            
            } else {
                Culture cult = landCultures.remove(seed.nextInt(landCultures.size()));
                AIFaction faction = new AIFaction(cult);

                Point closestMainLand = new Point(seed.nextInt(largeMap.length), seed.nextInt(largeMap[0].length));

                int initialRegionCount = seed.nextInt(5) + 2;
                System.out.println("Created superpower " + cult.getCountryName() + " with " + (initialRegionCount) + " regions");
            
                for (int j = 0; j < initialRegionCount; j++) {
                    closestMainLand
                            = Terra.randomBreadthFirstSearchWithObstacles(
                                    (Terra t) -> {
                                        return t != null && isLand(t) && t.getOwner() == null && !t.doesAppearInSmallMap();
                                    },
                                    (Terra t) -> {
                                        return t != null;
                                    }, largeMap, closestMainLand).get(0);
                    Region mainRegion = largeMap[closestMainLand.y][closestMainLand.x];
                    GM.swapPoint(closestMainLand);
                    System.out.println(closestMainLand);
                    mainRegion.setOwner(faction);
                }
            }
        }
    }

}
