package com.thizthizzydizzy.resourcespawner.scanner;
import com.thizthizzydizzy.resourcespawner.ResourceSpawner;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.SpawnedStructure;
import java.util.ArrayList;
import org.bukkit.Location;
public abstract class StructureScanner extends Scanner{
    @Override
    public ArrayList<String> scan(ResourceSpawnerCore plugin, Location location){
        ArrayList<String> messages = new ArrayList<>();
        for(ResourceSpawner spawner : plugin.resourceSpawners){
            for(SpawnedStructure structure : spawner.structures){
                if(location.getWorld()!=structure.getWorld())continue;
                Location loc = structure.getLocation();
                double dist = location.distance(loc);
                if(dist<minRange)continue;
                if(dist>maxRange)continue;
                messages.add(format(displayNameOverrides.getOrDefault(structure.getName(), structure.getName()), location, loc));
            }
        }
        return messages;
    }
    public abstract String format(String displayName, Location playerPos, Location structurePos);
}