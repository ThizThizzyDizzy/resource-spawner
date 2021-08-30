package com.thizthizzydizzy.resourcespawner.provider.location;
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
    public void loadFromConfig(JsonObject json){
        minX = json.getInt("min_x", Integer.MIN_VALUE);
        maxX = json.getInt("max_x", Integer.MAX_VALUE);
        minY = json.getInt("min_y", Integer.MIN_VALUE);
        maxY = json.getInt("max_y", Integer.MAX_VALUE);
        minZ = json.getInt("min_z", Integer.MIN_VALUE);
        maxZ = json.getInt("max_z", Integer.MAX_VALUE);
        if(maxX<minX)throw new IllegalArgumentException("max_x must be greater than or equal to min_x!");
        if(maxY<minY)throw new IllegalArgumentException("max_y must be greater than or equal to min_y!");
        if(maxZ<minZ)throw new IllegalArgumentException("max_z must be greater than or equal to min_z!");
        xDistribution = Distribution.valueOf(json.getString("x_distribution", "even").toUpperCase(Locale.ROOT));
        yDistribution = Distribution.valueOf(json.getString("y_distribution", "even").toUpperCase(Locale.ROOT));
        zDistribution = Distribution.valueOf(json.getString("z_distribution", "even").toUpperCase(Locale.ROOT));
    }
    @Override
    public Location get(World world, Random rand){
        int x = xDistribution.get(minX, maxX, rand);
        int y = yDistribution.get(minY, maxY, rand);
        int z = zDistribution.get(minZ, maxZ, rand);
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