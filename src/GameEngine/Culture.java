/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameEngine;

import java.util.ArrayList;
import javafx.scene.paint.Color;

/**
 *
 * @author onur
 */
class Culture {

    private final String nationalityName, countryName, officialName, shortCode;
    private final Color associatedColor;

    // maritime cultures are represented as independent cities rather than empires
    // enables that there can be more than one instance of this culture
    // phoenicans and greeks start with this feature
    // TODO make rules about being maritime..
    private final boolean maritime;

    public Culture(String nationalityName, String countryName, String officialName, String shortName, boolean maritime, Color color) {
        this.nationalityName = nationalityName;
        this.countryName = countryName;
        this.officialName = officialName;
        this.shortCode = shortName;
        this.maritime = maritime;
        this.associatedColor = color;
    }

    public String getShortCode() {
        return shortCode;
    }

    public Color getAssociatedColor() {
        return associatedColor;
    }

    public String getNationalityName() {
        return nationalityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getOfficialName() {
        return officialName;
    }

    public String getShortName() {
        return shortCode;
    }

    public boolean isMaritime() {
        return maritime;
    }

    public static Culture getPersia() {
        return new Culture("Persian", "Persia", "Achaemenid Empire", "PER", false, Color.AQUAMARINE);
    }

    public static Culture getEgypt() {
        return new Culture("Egyptian", "Egypt", "Kingdom of Egypt", "EGY", false, Color.GOLD);
    }

    public static Culture getBabylonia() {
        return new Culture("Babylonian", "Babylonia", "Neo-Babylonian Empire", "BAB", false, Color.DEEPPINK);
    }

    public static Culture getPhoenicia() {
        return new Culture("Phoenicia", "Phoenician", "Carthaginian Empire", "PHO", true, null);
    }

    public static Culture getMacedonia() {
        return new Culture("Macedon", "Macedonia", "Macedonian Empire", "MAC", false, Color.DIMGRAY);
    }

    public static Culture getGreece() {
        return new Culture("Greek", "Greece", "Delian League", "GRE", true, null);
    }

    public static Culture getRoma() {
        return new Culture("Roman", "Roma", "Roman Republic", "ROM", false, Color.DARKRED);
    }

    public static Culture getNumidia() {
        return new Culture("Numidian", "Numidia", "Kingdom of Numidia", "NUM", false, Color.SANDYBROWN);
    }

    public static Culture getCeltica() {
        return new Culture("Celt", "Celtica", "Gallia", "CEL", false, Color.FORESTGREEN);
    }

    public static ArrayList<Culture> getImperialCultures() {
        ArrayList<Culture> arr = new ArrayList<>();
        arr.add(getBabylonia());
        arr.add(getCeltica());
        arr.add(getEgypt());
        arr.add(getMacedonia());
        arr.add(getNumidia());
        arr.add(getPersia());
        arr.add(getRoma());
        return arr;
    }

    public static ArrayList<Culture> getMaritimeCultures() {
        ArrayList<Culture> arr = new ArrayList<>();
        arr.add(getGreece());
        arr.add(getPhoenicia());
        return arr;
    }
}
