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
public class Shipment {

    private int monthsLeft, transferDuration;
    private TradeOffer deal;
    private Faction accepter;

    public Shipment(int transferDuration, TradeOffer deal, Faction accepter) {
        this.transferDuration = transferDuration;
        this.deal = deal;
        this.accepter = accepter;
        monthsLeft = transferDuration;
        // shipment is added to the lists of both parties
        // but offer needs to be removed by the manager
        accepter.getShipments().add(this);
        deal.getOfferer().getShipments().add(this);
        // resource and gold are transferred
        // if offerer is selling
        if (!getDeal().isBuyer()) {
            getDeal().getOfferer().changeResource(
                    getDeal().getResourceTraded(), -getDeal().getAmount());
            accepter.changeGold(-getDeal().getCost());
        } else {
            accepter.changeResource(
                    getDeal().getResourceTraded(), -getDeal().getAmount());
            getDeal().getOfferer().changeGold(-getDeal().getCost());
        }
    }

    public int getMonthsLeft() {
        return monthsLeft;
    }

    public int getTransferDuration() {
        return transferDuration;
    }

    public TradeOffer getDeal() {
        return deal;
    }

    public Faction getAccepter() {
        return accepter;
    }

    public boolean moveShipment() {
        monthsLeft--;
        if (monthsLeft <= 0) {
            // resource and gold are transferred
            if (getDeal().isBuyer()) {
                getDeal().getOfferer().changeResource(
                        getDeal().getResourceTraded(), getDeal().getAmount());
                accepter.changeGold(getDeal().getCost());
            } else {
                accepter.changeResource(
                        getDeal().getResourceTraded(), getDeal().getAmount());
                getDeal().getOfferer().changeGold(getDeal().getCost());
            }

            // shipment is removed from the lists
            accepter.getShipments().remove(this);
            getDeal().getOfferer().getShipments().remove(this);
            return true;
        }
        return false;
    }
}
