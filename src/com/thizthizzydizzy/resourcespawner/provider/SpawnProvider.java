package com.thizthizzydizzy.resourcespawner.provider;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.Task;
import com.thizthizzydizzy.resourcespawner.condition.Condition;
import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.World;
import org.hjson.JsonObject;
public abstract class SpawnProvider{
    public ArrayList<Condition> conditions = new ArrayList<>();
    public abstract SpawnProvider newInstance();
    public abstract void loadFromConfig(ResourceSpawnerCore plugin, JsonObject json);
    public abstract Task spawn(ResourceSpawnerCore plugin, World world, Location location);
}
