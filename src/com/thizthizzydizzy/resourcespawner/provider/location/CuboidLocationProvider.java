package com.thizthizzydizzy.resourcespawner.provider.location;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.distribution.Distribution;
import com.thizthizzydizzy.resourcespawner.provider.LocationProvider;
import java.util.Locale;
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
        minX = json.getInt("min_x", Integer.MIN_VALUE);
        maxX = json.getInt("max_x", Integer.MAX_VALUE);
        minY = json.getInt("min_y", Integer.MIN_VALUE);
        maxY = json.getInt("max_y", Integer.MAX_VALUE);
        minZ = json.getInt("min_z", Integer.MIN_VALUE);
        maxZ = json.getInt("max_z", Integer.MAX_VALUE);
        if(maxX<minX)throw new IllegalArgumentException("max_x must be greater than or equal to min_x!");
        if(maxY<minY)throw new IllegalArgumentException("max_y must be greater than or equal to min_y!");
        if(maxZ<minZ)throw new IllegalArgumentException("max_z must be greater than or equal to min_z!");
        xDistribution = plugin.getDistribution(json.getString("x_distribution", "even"));
        yDistribution = plugin.getDistribution(json.getString("y_distribution", "even"));
        zDistribution = plugin.getDistribution(json.getString("z_distribution", "even"));
    }
    @Override
    public Location get(World world, Random rand){
        int x = xDistribution.get(minX, maxX, rand);
        int y = yDistribution.get(minY, maxY, rand);
        int z = zDistribution.get(minZ, maxZ, rand);
        return world.getBlockAt(x, y, z).getLocation();
    }
}