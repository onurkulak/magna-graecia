/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GameEngine;

import java.awt.Point;
import java.util.Random;

/**
 * GM stands for generic math, or methods
 * @author onur
 */
public class GM {

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
    
    public static void swapPoint(Point n) {
        int t;
        t = n.x;
        n.x = n.y;
        n.y=t;
    }
    
    //returns null instead of array index out of bounds..
    public static < E > E getMatrix(E[][] arr, int i, int j){
        try{
            return arr[i][j];
        }catch(ArrayIndexOutOfBoundsException e){
            return null;
        }
    }
    
    public static int truncate(int var, int bound1, int bound2){
        boolean d1 = bound1 > var;
        boolean d2 = bound2 > var;
        // var is too small, it gets the msaller number
        if(d1 && d2) {
            return bound1 > bound2 ? bound2 : bound1;
        }
        else if(!(d1||d2)){
            return bound1 > bound2 ? bound1 : bound2;
        }
        else return var;
    }
    
    public static double truncate(double var, double bound1, double bound2){
        boolean d1 = bound1 > var;
        boolean d2 = bound2 > var;
        // var is too small, it gets the smaller number
        if(d1 && d2) {
            return bound1 > bound2 ? bound2 : bound1;
        }
        // var is too large, it gets the larger one
        else if(!(d1||d2)){
            return bound1 > bound2 ? bound1 : bound2;
        }
        else return var;
    }
}
