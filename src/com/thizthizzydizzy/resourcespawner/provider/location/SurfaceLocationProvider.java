package com.thizthizzydizzy.resourcespawner.provider.location;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.distribution.Distribution;
import com.thizthizzydizzy.resourcespawner.provider.LocationProvider;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;
import org.hjson.JsonObject;
public class SurfaceLocationProvider implements LocationProvider{
    private int originX;
    private int originZ;
    private int radius;
    private int yOffset;
    private Distribution distribution;
    @Override
    public LocationProvider newInstance(){
        return new SurfaceLocationProvider();
    }
    @Override
    public void loadFromConfig(ResourceSpawnerCore plugin, JsonObject json){
        if(ResourceSpawnerCore.debug)System.out.println("Loading "+getClass().getName());
        originX = json.get("x").asInt();
        if(ResourceSpawnerCore.debug)System.out.println("x: "+originX);
        originZ = json.get("z").asInt();
        if(ResourceSpawnerCore.debug)System.out.println("z: "+originZ);
        yOffset = json.getInt("y_offset", 1);
        if(ResourceSpawnerCore.debug)System.out.println("y offset: "+yOffset);
        radius = json.getInt("radius", 0);
        if(ResourceSpawnerCore.debug)System.out.println("radius: "+radius);
        distribution = plugin.getDistribution(json.getString("distribution", "even"));
        if(ResourceSpawnerCore.debug)System.out.println("distribution: "+distribution.getClass().getName());
    }
    @Override
    public Location get(World world, Random rand){
        if(ResourceSpawnerCore.debug)System.out.println(getClass().getName()+" getting location...");
        int x = distribution.get(originX-radius, originX+radius, rand);
        int z = distribution.get(originZ-radius, originZ+radius, rand);
        Location loc = world.getHighestBlockAt(x, z).getLocation();
        loc.setY(loc.getY()+yOffset);
        return loc;
    }
}