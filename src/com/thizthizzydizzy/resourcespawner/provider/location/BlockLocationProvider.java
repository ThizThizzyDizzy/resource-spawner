package com.thizthizzydizzy.resourcespawner.provider.location;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.provider.LocationProvider;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;
import org.hjson.JsonObject;
public class BlockLocationProvider implements LocationProvider{
    private int x;
    private int y;
    private int z;
    @Override
    public LocationProvider newInstance(){
        return new BlockLocationProvider();
    }
    @Override
    public void loadFromConfig(ResourceSpawnerCore plugin, JsonObject json){
        if(ResourceSpawnerCore.debug)System.out.println("Loading "+getClass().getName());
        x = json.get("x").asInt();
        if(ResourceSpawnerCore.debug)System.out.println("x: "+x);
        y = json.get("y").asInt();
        if(ResourceSpawnerCore.debug)System.out.println("y: "+y);
        z = json.get("z").asInt();
        if(ResourceSpawnerCore.debug)System.out.println("z: "+z);
    }
    @Override
    public Location get(World world, Random rand){
        if(ResourceSpawnerCore.debug)System.out.println(getClass().getName()+" getting location...");
        return world.getBlockAt(x, y, z).getLocation();
    }
}