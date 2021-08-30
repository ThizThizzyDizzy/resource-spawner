package com.thizthizzydizzy.resourcespawner.provider;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import java.util.Random;
import org.bukkit.World;
import org.hjson.JsonObject;
public interface WorldProvider{
    public WorldProvider newInstance();
    public void loadFromConfig(ResourceSpawnerCore plugin, JsonObject json);
    public World get(Random rand);
}