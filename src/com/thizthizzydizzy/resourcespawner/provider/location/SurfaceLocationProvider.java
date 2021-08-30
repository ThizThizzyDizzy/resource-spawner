package com.thizthizzydizzy.resourcespawner.provider.location;
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
    public void loadFromConfig(JsonObject json){
        originX = json.get("x").asInt();
        originZ = json.get("z").asInt();
        radius = json.getInt("radius", 0);
        distribution = Distribution.valueOf(json.getString("distribution", "even").toUpperCase(Locale.ROOT));
    }
    @Override
    public Location get(World world, Random rand){
        int x = distribution.get(originX-radius, originX+radius, rand);
        int z = distribution.get(originZ-radius, originZ+radius, rand);
        return world.getHighestBlockAt(x, z).getLocation();
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