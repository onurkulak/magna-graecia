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
public class Settings {
    private int smallMapSize, largeMapSize;
    private int battleDifficulty, strategyDifficulty;
    private int numberOfSuperPowers, numberOfForeignPowers;
    public final static int numberOfSettings = 6;
    public Settings(int smallMapSize, int largeMapSize, int numberOfForeignPowers, int numberOfSuperPowers, int battleDifficulty, int strategyDifficulty) {
        this.smallMapSize = smallMapSize;
        this.largeMapSize = largeMapSize;
        this.numberOfSuperPowers = numberOfSuperPowers;
        this.numberOfForeignPowers = numberOfForeignPowers;
        this.battleDifficulty = battleDifficulty;
        this.strategyDifficulty = strategyDifficulty;
    }

    public int getSmallMapSize() {
        return smallMapSize;
    }

    public void setSmallMapSize(int smallMapSize) {
        this.smallMapSize = smallMapSize;
    }

    public int getLargeMapSize() {
        return largeMapSize;
    }

    public void setLargeMapSize(int largeMapSize) {
        this.largeMapSize = largeMapSize;
    }

    public int getBattleDifficulty() {
        return battleDifficulty;
    }

    public void setBattleDifficulty(int battleDifficulty) {
        this.battleDifficulty = battleDifficulty;
    }

    public int getStrategyDifficulty() {
        return strategyDifficulty;
    }

    public void setStrategyDifficulty(int strategyDifficulty) {
        this.strategyDifficulty = strategyDifficulty;
    }

    public int getNumberOfSuperPowers() {
        return numberOfSuperPowers;
    }

    public void setNumberOfSuperPowers(int numberOfSuperPowers) {
        this.numberOfSuperPowers = numberOfSuperPowers;
    }

    public int getNumberOfForeignPowers() {
        return numberOfForeignPowers;
    }

    public void setNumberOfForeignPowers(int numberOfForeignPowers) {
        this.numberOfForeignPowers = numberOfForeignPowers;
    }
    
    
}
