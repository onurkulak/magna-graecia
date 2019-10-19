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
public class TradeOffer {
    private final Resource resourceTraded;
    private final int cost, amount;
    private final boolean buyer;
    private final Faction offerer;

    public TradeOffer(Resource resourceTraded, int cost, int amount, boolean buyer, Faction offerer) {
        this.resourceTraded = resourceTraded;
        this.cost = cost;
        this.amount = amount;
        this.buyer = buyer;
        this.offerer = offerer;
    }

    public Resource getResourceTraded() {
        return resourceTraded;
    }

    public int getCost() {
        return cost;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isBuyer() {
        return buyer;
    }

    public Faction getOfferer() {
        return offerer;
    }
}
