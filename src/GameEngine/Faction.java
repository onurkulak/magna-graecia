/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameEngine;

import javafx.scene.paint.Color;
import java.util.ArrayList;
/**
 *
 * @author onur
 */
public class Faction {
    private String name;
    private Color color;
    private ArrayList<Province> provinces;
    private ArrayList<Region> regions;
    private Civic civicChoices;
    private ArrayList<TradeOffer> availableTradeOffers;
    private ArrayList<Shipment> ongoingShipments;
    private int[] resourceAmounts;
    private ArrayList<Force> forces;
    private ArrayList<Army> availableMercenaries;
    private ArrayList<Strategos> availableGenerals;
    private ArrayList<Technology> availableTechnologies;

    public String getName() {
        return name;
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

    public ArrayList<Shipment> getOngoingShipments() {
        return ongoingShipments;
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
    private Trait[] traits;
    private ArrayList<Technology> discoveredTechs;
    private ArrayList<Technology> unknownTechs;
    private diceElement[] Dice;
    private ArrayList<Building> availableBuildings; //can be switched with bitmaps
    private ArrayList<Unit> availableUnits;
    
    public enum Stance{PEACE, WAR, TRADE_AGREEMENT};
    public enum Trait{};//to be filled later
    private ArrayList<Stance> diplomaticStances;
   
}
