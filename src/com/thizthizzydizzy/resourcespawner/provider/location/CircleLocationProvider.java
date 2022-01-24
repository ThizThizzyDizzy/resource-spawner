package com.thizthizzydizzy.resourcespawner.provider.location;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.distribution.Distribution;
import com.thizthizzydizzy.resourcespawner.provider.LocationProvider;
import java.util.Random;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.hjson.JsonObject;
public class CircleLocationProvider implements LocationProvider{
    private int originX;
    private int originZ;
    private int radius;
    private int minY;
    private int maxY;
    private Distribution verticalDistribution;
    private Distribution horizontalDistribution;
    @Override
    public LocationProvider newInstance(){
        return new CircleLocationProvider();
    }
    @Override
    public void loadFromConfig(ResourceSpawnerCore plugin, JsonObject json){
        if(ResourceSpawnerCore.debug)System.out.println("Loading "+getClass().getName());
        originX = json.get("x").asInt();
        if(ResourceSpawnerCore.debug)System.out.println("x: "+originX);
        originZ = json.get("z").asInt();
        if(ResourceSpawnerCore.debug)System.out.println("z: "+originZ);
        radius = json.getInt("radius", 0);
        if(ResourceSpawnerCore.debug)System.out.println("radius: "+radius);
        minY = json.getInt("min_y", Short.MIN_VALUE);
        if(ResourceSpawnerCore.debug)System.out.println("min y: "+minY);
        maxY = json.getInt("max_y", Short.MAX_VALUE);
        if(ResourceSpawnerCore.debug)System.out.println("max y: "+maxY);
        if(maxY<minY)throw new IllegalArgumentException("max_y must be greater than or equal to min_y!");
        verticalDistribution = plugin.getDistribution(json.getString("vertical_distribution", "even"));
        if(ResourceSpawnerCore.debug)System.out.println("vertical distribution: "+verticalDistribution.getClass().getName());
        horizontalDistribution = plugin.getDistribution(json.getString("horizontal_distribution", "even"));
        if(ResourceSpawnerCore.debug)System.out.println("horizontal distribution: "+horizontalDistribution.getClass().getName());
    }
    @Override
    public Location get(World world, Random rand){
        if(ResourceSpawnerCore.debug)System.out.println(getClass().getName()+" getting location...");
        Location loc = null;
        boolean withinCircle = false;
        int tries = 0;
        while(loc==null||!withinCircle){
            int x = horizontalDistribution.get(originX-radius, originX+radius, rand);
            int y = verticalDistribution.get(minY, maxY, rand);
            int z = horizontalDistribution.get(originZ-radius, originZ+radius, rand);
            loc = world.getBlockAt(x, y, z).getLocation();
            double dist = Math.sqrt(Math.pow(x-originX, 2)+Math.pow(z-originZ, 2));
            withinCircle = dist<=radius;
            tries++;
            if(tries>1024){
                Bukkit.getServer().getLogger().log(Level.WARNING, "Circle location provider took over 1000 tries to pick a location! This is probably a bug!");
            }
        }
        return loc;
    }
}