/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameEngine;

import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author onur
 */
public class Faction {

    private Culture culture;
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

    private Trait[] traits;
    private ArrayList<Technology> discoveredTechs;
    private ArrayList<Technology> unknownTechs;
    private diceElement[] Dice;
    private ArrayList<Building> availableBuildings; //can be switched with bitmaps
    private ArrayList<Unit> availableUnits;

    public enum Stance {

        PEACE, WAR, TRADE_AGREEMENT
    };
    //whenever a faction randomly picks a color from here, it's no longer available
    private static ArrayList<Color> availableFactionColors = new ArrayList<>(Arrays.asList(
            Color.ALICEBLUE,
            Color.ANTIQUEWHITE,
            Color.AQUA,
            Color.AQUAMARINE,
            Color.AZURE,
            Color.BEIGE,
            Color.BISQUE,
            Color.BLACK,
            Color.BLANCHEDALMOND,
            Color.BLUE,
            Color.BLUEVIOLET,
            Color.BROWN,
            Color.BURLYWOOD,
            Color.CADETBLUE,
            Color.CHARTREUSE,
            Color.CHOCOLATE,
            Color.CORAL,
            Color.CORNFLOWERBLUE,
            Color.CORNSILK,
            Color.CRIMSON,
            Color.CYAN,
            Color.DARKBLUE,
            Color.DARKCYAN,
            Color.DARKGOLDENROD,
            Color.DARKGRAY,
            Color.DARKGREEN,
            Color.DARKGREY,
            Color.DARKKHAKI,
            Color.DARKMAGENTA,
            Color.DARKOLIVEGREEN,
            Color.DARKORANGE,
            Color.DARKORCHID,
            Color.DARKRED,
            Color.DARKSALMON,
            Color.DARKSEAGREEN,
            Color.DARKSLATEBLUE,
            Color.DARKSLATEGRAY,
            Color.DARKSLATEGREY,
            Color.DARKTURQUOISE,
            Color.DARKVIOLET,
            Color.DEEPPINK,
            Color.DEEPSKYBLUE,
            Color.DIMGRAY,
            Color.DIMGREY,
            Color.DODGERBLUE,
            Color.FIREBRICK,
            Color.FLORALWHITE,
            Color.FORESTGREEN,
            Color.FUCHSIA,
            Color.GAINSBORO,
            Color.GHOSTWHITE,
            Color.GOLD,
            Color.GOLDENROD,
            Color.GRAY,
            Color.GREEN,
            Color.GREENYELLOW,
            Color.GREY,
            Color.HONEYDEW,
            Color.HOTPINK,
            Color.INDIANRED,
            Color.INDIGO,
            Color.IVORY,
            Color.KHAKI,
            Color.LAVENDER,
            Color.LAVENDERBLUSH,
            Color.LAWNGREEN,
            Color.LEMONCHIFFON,
            Color.LIGHTBLUE,
            Color.LIGHTCORAL,
            Color.LIGHTCYAN,
            Color.LIGHTGOLDENRODYELLOW,
            Color.LIGHTGRAY,
            Color.LIGHTGREEN,
            Color.LIGHTGREY,
            Color.LIGHTPINK,
            Color.LIGHTSALMON,
            Color.LIGHTSEAGREEN,
            Color.LIGHTSKYBLUE,
            Color.LIGHTSLATEGRAY,
            Color.LIGHTSLATEGREY,
            Color.LIGHTSTEELBLUE,
            Color.LIGHTYELLOW,
            Color.LIME,
            Color.LIMEGREEN,
            Color.LINEN,
            Color.MAGENTA,
            Color.MAROON,
            Color.MEDIUMAQUAMARINE,
            Color.MEDIUMBLUE,
            Color.MEDIUMORCHID,
            Color.MEDIUMPURPLE,
            Color.MEDIUMSEAGREEN,
            Color.MEDIUMSLATEBLUE,
            Color.MEDIUMSPRINGGREEN,
            Color.MEDIUMTURQUOISE,
            Color.MEDIUMVIOLETRED,
            Color.MIDNIGHTBLUE,
            Color.MINTCREAM,
            Color.MISTYROSE,
            Color.MOCCASIN,
            Color.NAVAJOWHITE,
            Color.NAVY,
            Color.OLDLACE,
            Color.OLIVE,
            Color.OLIVEDRAB,
            Color.ORANGE,
            Color.ORANGERED,
            Color.ORCHID,
            Color.PALEGOLDENROD,
            Color.PALEGREEN,
            Color.PALETURQUOISE,
            Color.PALEVIOLETRED,
            Color.PAPAYAWHIP,
            Color.PEACHPUFF,
            Color.PERU,
            Color.PINK,
            Color.PLUM,
            Color.POWDERBLUE,
            Color.PURPLE,
            Color.RED,
            Color.ROSYBROWN,
            Color.ROYALBLUE,
            Color.SADDLEBROWN,
            Color.SALMON,
            Color.SANDYBROWN,
            Color.SEAGREEN,
            Color.SEASHELL,
            Color.SIENNA,
            Color.SILVER,
            Color.SKYBLUE,
            Color.SLATEBLUE,
            Color.SLATEGRAY,
            Color.SLATEGREY,
            Color.SNOW,
            Color.SPRINGGREEN,
            Color.STEELBLUE,
            Color.TAN,
            Color.TEAL,
            Color.THISTLE,
            Color.TOMATO,
            Color.TRANSPARENT,
            Color.TURQUOISE,
            Color.VIOLET,
            Color.WHEAT,
            Color.WHITE,
            Color.WHITESMOKE,
            Color.YELLOW,
            Color.YELLOWGREEN
    ));

    public enum Trait {
    };//to be filled later
    private ArrayList<Stance> diplomaticStances;

    public Faction(Culture culture) {
        this.culture = culture;
        color = availableFactionColors.remove((int)(Math.random()*availableFactionColors.size()));
    }

    // will be cahnged later
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

    public Culture getCulture() {
        return culture;
    }

    public void setCulture(Culture culture) {
        this.culture = culture;
    }
}
