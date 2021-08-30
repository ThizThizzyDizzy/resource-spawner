package com.thizthizzydizzy.resourcespawner.provider.location;
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
    public void loadFromConfig(JsonObject json){
        x = json.get("x").asInt();
        y = json.get("y").asInt();
        z = json.get("z").asInt();
    }
    @Override
    public Location get(World world, Random rand){
        return world.getBlockAt(x, y, z).getLocation();
    }
}