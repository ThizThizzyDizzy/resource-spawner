package com.thizthizzydizzy.resourcespawner.provider;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;
import org.hjson.JsonObject;
public interface LocationProvider{
    public LocationProvider newInstance();
    public void loadFromConfig(JsonObject json);
    public Location get(World world, Random rand);
}