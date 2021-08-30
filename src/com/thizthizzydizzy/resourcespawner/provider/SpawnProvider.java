package com.thizthizzydizzy.resourcespawner.provider;
import com.thizthizzydizzy.resourcespawner.SpawnedStructure;
import com.thizthizzydizzy.resourcespawner.Task;
import com.thizthizzydizzy.resourcespawner.condition.Condition;
import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.World;
import org.hjson.JsonObject;
public abstract class SpawnProvider{
    public ArrayList<Condition> conditions = new ArrayList<>();
    public abstract SpawnProvider newInstance();
    public abstract void loadFromConfig(JsonObject json);
    public abstract Task<SpawnedStructure> spawn(World world, Location location);
}
