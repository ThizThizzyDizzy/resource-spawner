package com.thizthizzydizzy.resourcespawner.distribution;
import java.util.Random;
public interface Distribution{
    public int get(int min, int max, Random rand);
}