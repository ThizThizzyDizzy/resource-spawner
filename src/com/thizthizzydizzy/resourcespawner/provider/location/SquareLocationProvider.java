package com.thizthizzydizzy.resourcespawner.provider.location;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.distribution.Distribution;
import com.thizthizzydizzy.resourcespawner.provider.LocationProvider;
import java.util.Locale;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;
import org.hjson.JsonArray;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
public class SquareLocationProvider implements LocationProvider{
    private int originX;
    private int originZ;
    private int radius;
    private int minY;
    private int maxY;
    private Distribution verticalDistribution;
    private Distribution horizontalDistribution;
    @Override
    public LocationProvider newInstance(){
        return new SquareLocationProvider();
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
        int x = horizontalDistribution.get(originX-radius, originX+radius, rand);
        int y = verticalDistribution.get(minY, maxY, rand);
        int z = horizontalDistribution.get(originZ-radius, originZ+radius, rand);
        return world.getBlockAt(x, y, z).getLocation();
    }
}