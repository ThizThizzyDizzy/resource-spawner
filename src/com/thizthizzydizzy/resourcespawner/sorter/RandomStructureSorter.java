package com.thizthizzydizzy.resourcespawner.sorter;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.bukkit.Location;
public class RandomStructureSorter implements StructureSorter{
    @Override
    public ArrayList<Location> sort(Collection<Location> data){
        if(ResourceSpawnerCore.debug)System.out.println(getClass().getName()+" Shuffling...");
        ArrayList<Location> blocks = new ArrayList<>(data);
        Collections.shuffle(blocks);
        if(ResourceSpawnerCore.debug)System.out.println(getClass().getName()+" Shuffled");
        return blocks;
    }
}