package com.thizthizzydizzy.resourcespawner.sorter;
import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.Location;
public interface StructureSorter{
    public ArrayList<Location> sort(Collection<Location> data);
}