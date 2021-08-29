package com.thizthizzydizzy.resourcespawner.sorter;
import com.thizthizzydizzy.resourcespawner.Structure;
import java.util.ArrayList;
import java.util.Collections;
public class RandomStructureSorter implements StructureSorter{
    @Override
    public ArrayList<int[]> sort(Structure structure){
        structure.normalize();
        ArrayList<int[]> blocks = new ArrayList<>(structure.data.keySet());
        Collections.shuffle(blocks);
        return blocks;
    }
}