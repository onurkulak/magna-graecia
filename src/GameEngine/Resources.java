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
class Resources {

    private final Resource wheat, clay,
            wine, flax, livestock,
            dye, salt, spice,
            iron, copper, marble,
            timber, olive,
            fish, pot, glass, slave;

    public Resources() {
        wheat = new Resource("wheat", 1, Resource.ResourceTypes.EDIBLE);
        clay = new Resource("clay", 1, Resource.ResourceTypes.MATERIAL);

        wine = new Resource("wine", 2, Resource.ResourceTypes.EDIBLE);
        flax = new Resource("flax", 2, Resource.ResourceTypes.LUXURY);
        livestock = new Resource("livestock", 2, Resource.ResourceTypes.EDIBLE);

        dye = new Resource("dye", 4, Resource.ResourceTypes.LUXURY);
        salt = new Resource("salt", 1, Resource.ResourceTypes.MATERIAL);
        spice = new Resource("spice", 5, Resource.ResourceTypes.LUXURY);

        iron = new Resource("iron", 2, Resource.ResourceTypes.MATERIAL);
        copper = new Resource("copper", 1, Resource.ResourceTypes.MATERIAL);
        marble = new Resource("marble", 2, Resource.ResourceTypes.MATERIAL);

        timber = new Resource("timber", 1, Resource.ResourceTypes.MATERIAL);
        olive = new Resource("olive", 1, Resource.ResourceTypes.EDIBLE);

        fish = new Resource("fish", 2, Resource.ResourceTypes.EDIBLE);
        pot = new Resource("pot", 2, Resource.ResourceTypes.LUXURY);
        glass = new Resource("glass", 3, Resource.ResourceTypes.LUXURY);
        slave = new Resource("slave", 5, Resource.ResourceTypes.SLAVE);
    }
    
    public Resource[] getDesertResources(){
        return new Resource[]{
            dye, salt, spice
        };
    }
    public Resource[] getHillResources(){
        return new Resource[]{
            iron, copper, marble
        };
    }
    public Resource[] getPlainResources(){
        return new Resource[]{
            clay, wheat
        };
    }
    public Resource[] getGrassResources(){
        return new Resource[]{
            wine, flax, livestock, wheat
        };
    }
    public Resource[] getForestResources(){
        return new Resource[]{
            timber, olive
        };
    }
    
    public Resource[] getResourcesByTerrain(Terra.TerrainType t){
        switch(t){
            case DESERT: return getDesertResources();
            case FOREST: return getForestResources();
            case PLAIN: return getPlainResources();
            case HILL: return getHillResources();
            case GRASS: return getGrassResources();
            default: return null;
        }
    }

    public Resource getWheat() {
        return wheat;
    }

    public Resource getClay() {
        return clay;
    }

    public Resource getWine() {
        return wine;
    }

    public Resource getFlax() {
        return flax;
    }

    public Resource getLivestock() {
        return livestock;
    }

    public Resource getDye() {
        return dye;
    }

    public Resource getSalt() {
        return salt;
    }

    public Resource getSpice() {
        return spice;
    }

    public Resource getIron() {
        return iron;
    }

    public Resource getCopper() {
        return copper;
    }

    public Resource getMarble() {
        return marble;
    }

    public Resource getTimber() {
        return timber;
    }

    public Resource getOlive() {
        return olive;
    }

    public Resource getFish() {
        return fish;
    }

    public Resource getPot() {
        return pot;
    }

    public Resource getGlass() {
        return glass;
    }

    public Resource getSlave() {
        return slave;
    }
}
