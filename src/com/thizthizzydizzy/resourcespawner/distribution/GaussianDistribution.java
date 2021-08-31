package com.thizthizzydizzy.resourcespawner.distribution;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import java.util.Random;
public class GaussianDistribution implements Distribution{
    @Override
    public int get(int min, int max, Random rand){
        if(ResourceSpawnerCore.debug)System.out.println(getClass().getName()+" getting distribution");
        return (int)(rand.nextGaussian()*(max-min))+(max-min)/2;
    }
}