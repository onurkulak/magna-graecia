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
    private ArrayList<TradeOffer> activeTradeOffers;
    private ArrayList<Shipment> ongoingShipments;
    private int[] resourceAmounts;
    private ArrayList<Force> forces;
    private ArrayList<Army> availableMercenaries;
    private ArrayList<Strategos> availableGenerals;
    private Technology[] availableTechnologies;
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
