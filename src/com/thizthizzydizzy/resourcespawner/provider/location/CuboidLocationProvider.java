package com.thizthizzydizzy.resourcespawner.provider.location;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.distribution.Distribution;
import com.thizthizzydizzy.resourcespawner.provider.LocationProvider;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;
import org.hjson.JsonObject;
public class CuboidLocationProvider implements LocationProvider{
    private int minX;
    private int maxX;
    private int minY;
    private int maxY;
    private int minZ;
    private int maxZ;
    private Distribution xDistribution;
    private Distribution yDistribution;
    private Distribution zDistribution;
    @Override
    public LocationProvider newInstance(){
        return new CuboidLocationProvider();
    }
    @Override
    public void loadFromConfig(ResourceSpawnerCore plugin, JsonObject json){
        if(ResourceSpawnerCore.debug)System.out.println("Loading "+getClass().getName());
        minX = json.get("min_x").asInt();
        if(ResourceSpawnerCore.debug)System.out.println("min x: "+minX);
        maxX = json.get("max_x").asInt();
        if(ResourceSpawnerCore.debug)System.out.println("max x: "+maxX);
        minY = json.getInt("min_y", Integer.MIN_VALUE);
        if(ResourceSpawnerCore.debug)System.out.println("min y: "+minY);
        maxY = json.getInt("max_y", Integer.MAX_VALUE);
        if(ResourceSpawnerCore.debug)System.out.println("max y: "+maxY);
        minZ = json.get("min_z").asInt();
        if(ResourceSpawnerCore.debug)System.out.println("min z: "+minZ);
        maxZ = json.get("max_z").asInt();
        if(ResourceSpawnerCore.debug)System.out.println("max z: "+maxZ);
        if(maxX<minX)throw new IllegalArgumentException("max_x must be greater than or equal to min_x!");
        if(maxY<minY)throw new IllegalArgumentException("max_y must be greater than or equal to min_y!");
        if(maxZ<minZ)throw new IllegalArgumentException("max_z must be greater than or equal to min_z!");
        xDistribution = plugin.getDistribution(json.getString("x_distribution", "even"));
        if(ResourceSpawnerCore.debug)System.out.println("x distribution: "+xDistribution.getClass().getName());
        yDistribution = plugin.getDistribution(json.getString("y_distribution", "even"));
        if(ResourceSpawnerCore.debug)System.out.println("y distribution: "+yDistribution.getClass().getName());
        zDistribution = plugin.getDistribution(json.getString("z_distribution", "even"));
        if(ResourceSpawnerCore.debug)System.out.println("z distribution: "+zDistribution.getClass().getName());
    }
    @Override
    public Location get(World world, Random rand){
        if(ResourceSpawnerCore.debug)System.out.println(getClass().getName()+" getting location...");
        int x = xDistribution.get(minX, maxX, rand);
        int y = yDistribution.get(minY, maxY, rand);
        int z = zDistribution.get(minZ, maxZ, rand);
        return world.getBlockAt(x, y, z).getLocation();
    }
}