/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameEngine;

import java.util.ArrayList;
/**
 *
 * @author onur
 */
public class AIFaction extends Faction{
    private Civic favoriteCivics;
    private int[] attitudes;
    public int pseudoPopulation;

    public AIFaction(Culture culture) {
        super(culture);
    }

    public void playTurn() {
    }
    
    public int getRegionCountWithoutProvinces(){
        ArrayList<Region> regions = getRegions();
        int cnt = regions.size();
        for(Region r: regions)
            if(r.doesAppearInSmallMap())
                --cnt;
        return cnt;
    }
}
