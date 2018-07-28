/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameEngine;

/**
 *
 * @author onur
 */
public class Civic {
    public enum GovernmentCivic{TYRANNY};
    public enum EconomicalCivic{SELF_SUFFICIENCY};
    public enum MilitaryCivic{MILITIA, MERCENARIES};
    private GovernmentCivic government;
    private EconomicalCivic economical;
    private MilitaryCivic military;
    
    public Civic() {
        government = GovernmentCivic.TYRANNY;
        economical = EconomicalCivic.SELF_SUFFICIENCY;
        military = MilitaryCivic.MILITIA;
    }

    public GovernmentCivic getGovernment() {
        return government;
    }

    public void setGovernment(GovernmentCivic government) {
        this.government = government;
    }

    public EconomicalCivic getEconomical() {
        return economical;
    }

    public void setEconomical(EconomicalCivic economical) {
        this.economical = economical;
    }

    public MilitaryCivic getMilitary() {
        return military;
    }

    public void setMilitary(MilitaryCivic military) {
        this.military = military;
    }
    
    
}
