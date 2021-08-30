package com.thizthizzydizzy.resourcespawner.distribution;
import java.util.Random;
public class GaussianDistribution implements Distribution{
    @Override
    public int get(int min, int max, Random rand){
        return (int)(rand.nextGaussian()*(max-min))+(max-min)/2;
    }
}