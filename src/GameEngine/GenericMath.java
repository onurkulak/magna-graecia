/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameEngine;

import java.util.Random;

/**
 *
 * @author onur
 */
public class GenericMath {

    static <element> element getSample(element[] values, double[] terrainProbs) {
        return getSample(values, terrainProbs, new Random());
    }

    static <element> element getSample(element[] values, double[] terrainProbs, Random seed) {
        double p = seed.nextDouble();
        for(int i = 0; i < values.length; i++){
            p-=terrainProbs[i];
            if(p<0)
                return values[i];
        }
        return values[values.length-1];
    }
    
    public static void normalizeArray(double[] arr){
        double sum = 0;
        for(int i = 0; i < arr.length; i++)
            sum += arr[i];
        for(int i = 0; i < arr.length; i++)
            arr[i] /= sum;
    }
    
}
