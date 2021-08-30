package com.thizthizzydizzy.resourcespawner.condition;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.Task;
import org.bukkit.Location;
import org.bukkit.World;
import org.hjson.JsonObject;
public interface Condition{
    public Condition newInstance();
    public void loadFromConfig(ResourceSpawnerCore plugin, JsonObject json);
    /**
     * Checks the condition at a specified location
     * @param world the world to check in
     * @param location the location to check
     * @return a Task used to calculate the result
     */
    public Task<Boolean> check(World world, Location location);
}