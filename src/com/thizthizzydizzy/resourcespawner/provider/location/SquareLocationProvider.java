package com.thizthizzydizzy.resourcespawner.provider.location;
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
    public void loadFromConfig(JsonObject json){
        JsonValue val = json.get("origin");
        if(val.isArray()){
            JsonArray origin = val.asArray();
            if(origin.size()!=2)throw new IllegalArgumentException("origin must have 2 entries! (x,z)");
            originX = origin.get(0).asInt();
            originZ = origin.get(1).asInt();
        }else throw new IllegalArgumentException("origin must be an array!");
        radius = json.getInt("radius", 0);
        minY = json.getInt("min_y", Integer.MIN_VALUE);
        maxY = json.getInt("max_y", Integer.MAX_VALUE);
        if(maxY<minY)throw new IllegalArgumentException("max_y must be greater than or equal to min_y!");
        verticalDistribution = Distribution.valueOf(json.getString("distribution", "even").toUpperCase(Locale.ROOT));
        horizontalDistribution = Distribution.valueOf(json.getString("distribution", "even").toUpperCase(Locale.ROOT));
    }
    @Override
    public Location get(World world, Random rand){
        int x = horizontalDistribution.get(originX-radius, originX+radius, rand);
        int y = verticalDistribution.get(minY, maxY, rand);
        int z = horizontalDistribution.get(originZ-radius, originZ+radius, rand);
        return world.getBlockAt(x, y, z).getLocation();
    }
    private enum Distribution{
        EVEN {
            @Override
            int get(int min, int max, Random rand){
                return rand.nextInt(max-min+1)+min;
            }
        }, GAUSSIAN {//center is halfway between min and max, standard deviation hits min and max
            @Override
            int get(int min, int max, Random rand){
                return (int)(rand.nextGaussian()*(max-min))+(max-min)/2;
            }
        };
        abstract int get(int min, int max, Random rand);
    }
}