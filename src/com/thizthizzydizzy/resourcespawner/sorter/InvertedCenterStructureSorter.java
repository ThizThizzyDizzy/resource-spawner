package com.thizthizzydizzy.resourcespawner.sorter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.bukkit.Location;
public class InvertedCenterStructureSorter implements StructureSorter{
    @Override
    public ArrayList<Location> sort(Collection<Location> data){
        ArrayList<Location> blocks = new ArrayList<>(data);
        Collections.sort(blocks, (o1, o2) -> {
            long dist1 = Math.round(Math.sqrt(o1.getBlockX()*o1.getBlockX()+o1.getBlockY()*o1.getBlockY()+o1.getBlockZ()*o1.getBlockZ())*1000);
            long dist2 = Math.round(Math.sqrt(o2.getBlockX()*o2.getBlockX()+o2.getBlockY()*o2.getBlockY()+o2.getBlockZ()*o2.getBlockZ())*1000);
            return (int)(dist2-dist1);
        });
        return blocks;
    }
}