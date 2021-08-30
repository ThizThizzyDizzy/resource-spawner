package com.thizthizzydizzy.resourcespawner.provider.location;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.distribution.Distribution;
import com.thizthizzydizzy.resourcespawner.provider.LocationProvider;
import java.util.Locale;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;
import org.hjson.JsonObject;
public class SurfaceLocationProvider implements LocationProvider{
    private int originX;
    private int originZ;
    private int radius;
    private Distribution distribution;
    @Override
    public LocationProvider newInstance(){
        return new SurfaceLocationProvider();
    }
    @Override
    public void loadFromConfig(ResourceSpawnerCore plugin, JsonObject json){
        originX = json.get("x").asInt();
        originZ = json.get("z").asInt();
        radius = json.getInt("radius", 0);
        distribution = plugin.getDistribution(json.getString("distribution", "even"));
    }
    @Override
    public Location get(World world, Random rand){
        int x = distribution.get(originX-radius, originX+radius, rand);
        int z = distribution.get(originZ-radius, originZ+radius, rand);
        return world.getHighestBlockAt(x, z).getLocation();
    }
}