package com.thizthizzydizzy.resourcespawner.sorter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
public class CenterStructureSorter implements StructureSorter{
    @Override
    public ArrayList<int[]> sort(Collection<int[]> data){
        ArrayList<int[]> blocks = new ArrayList<>(data);
        Collections.sort(blocks, (o1, o2) -> {
            long dist1 = Math.round(Math.sqrt(o1[0]*o1[0]+o1[1]*o1[1]+o1[2]*o1[2])*1000);
            long dist2 = Math.round(Math.sqrt(o2[0]*o2[0]+o2[1]*o2[1]+o2[2]*o2[2])*1000);
            return (int)(dist1-dist2);
        });
        return blocks;
    }
}