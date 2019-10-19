/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameEngine;

import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.Arrays;
import magna.graecia.AvailableColorsJavaFX;

/**
 *
 * @author onur
 */
public class Faction {

    private Culture culture;
    private String name;
    private final Color color;
    private ArrayList<Province> provinces;
    private ArrayList<City> cities;
    private ArrayList<Region> regions;
    private Civic civicChoices;
    private ArrayList<TradeOffer> availableTradeOffers;
    private ArrayList<Shipment> shipments;
    private final int[] resourceAmounts;
    private ArrayList<Force> forces;
    private ArrayList<Army> availableMercenaries;
    private ArrayList<Strategos> availableGenerals;
    private ArrayList<Technology> availableTechnologies;

    private Trait[] traits;
    private ArrayList<Technology> discoveredTechs;
    private ArrayList<Technology> unknownTechs;
    private diceElement[] Dice;
    private ArrayList<Building> availableBuildings; //can be switched with bitmaps
    private ArrayList<Unit> availableUnits;

    public void endTurn() {
        for(Shipment s: shipments)
            s.moveShipment();
        for(City c: getCities())
            c.produce();
        
        // small map is already considered with the city method
        for(Region r: getRegions())
            if(!r.doesAppearInSmallMap())
                r.produce();
    }

    public enum Stance {

        PEACE, WAR, TRADE_AGREEMENT
    };
    

    public enum Trait {
    };//to be filled later
    private ArrayList<Stance> diplomaticStances;

    public Faction(Culture culture) {
        this.culture = culture;
        if(culture.isMaritime())
            color = AvailableColorsJavaFX.availableColors.get((int)(Math.random()*
                    AvailableColorsJavaFX.availableColors.size()));
        else color = culture.getAssociatedColor();
        resourceAmounts = new int[Resources.resourceCount];
    }

    //@TODO  will be cahnged later
    public String getName() {
        return culture.getCountryName();
    }

    public Color getColor() {
        return color;
    }

    public ArrayList<Province> getProvinces() {
        return provinces;
    }

    public ArrayList<Region> getRegions() {
        return regions;
    }

    public Civic getCivicChoices() {
        return civicChoices;
    }

    public ArrayList<TradeOffer> getAvailableTradeOffers() {
        return availableTradeOffers;
    }

    public ArrayList<Shipment> getShipments() {
        return shipments;
    }

    public int[] getResourceAmounts() {
        return resourceAmounts;
    }

    public ArrayList<Force> getForces() {
        return forces;
    }

    public ArrayList<Army> getAvailableMercenaries() {
        return availableMercenaries;
    }

    public ArrayList<Strategos> getAvailableGenerals() {
        return availableGenerals;
    }

    public ArrayList<Technology> getAvailableTechnologies() {
        return availableTechnologies;
    }

    public Trait[] getTraits() {
        return traits;
    }

    public ArrayList<Technology> getDiscoveredTechs() {
        return discoveredTechs;
    }

    public ArrayList<Technology> getUnknownTechs() {
        return unknownTechs;
    }

    public diceElement[] getDice() {
        return Dice;
    }

    public ArrayList<Building> getAvailableBuildings() {
        return availableBuildings;
    }

    public ArrayList<Unit> getAvailableUnits() {
        return availableUnits;
    }

    public ArrayList<Stance> getDiplomaticStances() {
        return diplomaticStances;
    }

    public Culture getCulture() {
        return culture;
    }

    public void setCulture(Culture culture) {
        this.culture = culture;
    }
    
    public void changeResource(Resource r, int amount){
        if(r.getType() != Resource.ResourceTypes.SLAVE)
            resourceAmounts[r.getId()]+=amount;
        else
            // if it has no provinces, it's an AIplayer and slaves added to its pseudo population
            if(this.getProvinces().isEmpty()){
                AIFaction aiFaction = (AIFaction)this;
                aiFaction.pseudoPopulation+=amount;
            }
            else{
                // slaves are sent to first capital
                this.getProvinces().get(0).getCapital().changeSlavePopulation(amount);
            }
    }
    
    public void changeGold(int amount){
        resourceAmounts[resourceAmounts.length-1]+=amount;
    }

    public ArrayList<City> getCities() {
        return cities;
    }
    
    
}
