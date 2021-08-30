package com.thizthizzydizzy.resourcespawner.sorter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.bukkit.Location;
public class RandomStructureSorter implements StructureSorter{
    @Override
    public ArrayList<Location> sort(Collection<Location> data){
        ArrayList<Location> blocks = new ArrayList<>(data);
        Collections.shuffle(blocks);
        return blocks;
    }
}