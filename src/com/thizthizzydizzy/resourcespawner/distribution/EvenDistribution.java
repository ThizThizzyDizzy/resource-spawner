package com.thizthizzydizzy.resourcespawner.distribution;
import java.util.Random;
public class EvenDistribution implements Distribution{
    @Override
    public int get(int min, int max, Random rand){
        return rand.nextInt(max-min+1)+min;
    }
}