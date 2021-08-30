package com.thizthizzydizzy.resourcespawner.sorter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
public class RandomStructureSorter implements StructureSorter{
    @Override
    public ArrayList<int[]> sort(Collection<int[]> data){
        ArrayList<int[]> blocks = new ArrayList<>(data);
        Collections.shuffle(blocks);
        return blocks;
    }
}