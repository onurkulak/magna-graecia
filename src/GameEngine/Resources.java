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
    
    public final static int resourceCount = 16;

    public Resources() {
        wheat = new Resource("wheat", 1, Resource.ResourceTypes.EDIBLE, 0);
        clay = new Resource("clay", 1, Resource.ResourceTypes.MATERIAL, 1);

        wine = new Resource("wine", 2, Resource.ResourceTypes.EDIBLE, 2);
        flax = new Resource("flax", 2, Resource.ResourceTypes.LUXURY, 3);
        livestock = new Resource("livestock", 2, Resource.ResourceTypes.EDIBLE, 4);

        dye = new Resource("dye", 4, Resource.ResourceTypes.LUXURY, 5);
        salt = new Resource("salt", 2, Resource.ResourceTypes.MATERIAL, 6);
        spice = new Resource("spice", 5, Resource.ResourceTypes.LUXURY, 7);

        iron = new Resource("iron", 2, Resource.ResourceTypes.MATERIAL, 8);
        copper = new Resource("copper", 1, Resource.ResourceTypes.MATERIAL, 9);
        marble = new Resource("marble", 2, Resource.ResourceTypes.MATERIAL, 10);

        timber = new Resource("timber", 1, Resource.ResourceTypes.MATERIAL, 11);
        olive = new Resource("olive", 1, Resource.ResourceTypes.EDIBLE, 12);

        fish = new Resource("fish", 2, Resource.ResourceTypes.EDIBLE, 13);
        pot = new Resource("pot", 2, Resource.ResourceTypes.LUXURY, 14);
        glass = new Resource("glass", 3, Resource.ResourceTypes.LUXURY, 15);
        slave = new Resource("slave", 5, Resource.ResourceTypes.SLAVE, 16);
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
    
    //well that is everything except slaves..
    public Resource[] getAllStorableResources(){
        return new Resource[]{
            wheat, clay,
            wine, flax, livestock,
            dye, salt, spice,
            iron, copper, marble,
            timber, olive,
            fish, pot, glass
        };
    }
}
