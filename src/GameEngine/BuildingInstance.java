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
public class BuildingInstance {
    private Building parent;
    private int workerCount;
    private int productionLeft;

    public BuildingInstance(Building parent) {
        this.parent = parent;
        productionLeft = this.parent.getConstructionTime();
        workerCount = 0;
    }

    public Building getParent() {
        return parent;
    }

    public int getWorkerCount() {
        return workerCount;
    }

    public int getProductionLeft() {
        return productionLeft;
    }
    
    
}
