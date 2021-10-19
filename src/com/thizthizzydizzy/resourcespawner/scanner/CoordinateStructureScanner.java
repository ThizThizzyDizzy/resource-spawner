package com.thizthizzydizzy.resourcespawner.scanner;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import org.bukkit.Location;
import org.hjson.JsonObject;
public class CoordinateStructureScanner extends StructureScanner{
    @Override
    public Scanner newInstance(){
        return new CoordinateStructureScanner();
    }
    @Override
    public void loadFromConfig(ResourceSpawnerCore plugin, JsonObject obj){}
    @Override
    public String format(String displayName, Location playerPos, Location structurePos){
        return displayName+" ("+structurePos.getBlockX()+","+structurePos.getBlockY()+","+structurePos.getBlockZ()+")";
    }
}