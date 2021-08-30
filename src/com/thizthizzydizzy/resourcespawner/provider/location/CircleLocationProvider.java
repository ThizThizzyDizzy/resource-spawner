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
        originX = json.get("x").asInt();
        originZ = json.get("z").asInt();
        radius = json.getInt("radius", 0);
        minY = json.getInt("min_y", Integer.MIN_VALUE);
        maxY = json.getInt("max_y", Integer.MAX_VALUE);
        if(maxY<minY)throw new IllegalArgumentException("max_y must be greater than or equal to min_y!");
        verticalDistribution = plugin.getDistribution(json.getString("vertical_distribution", "even"));
        horizontalDistribution = plugin.getDistribution(json.getString("horizontal_distribution", "even"));
    }
    @Override
    public Location get(World world, Random rand){
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