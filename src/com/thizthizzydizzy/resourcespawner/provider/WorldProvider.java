package com.thizthizzydizzy.resourcespawner.provider;
import java.util.Random;
import org.bukkit.World;
import org.hjson.JsonObject;
public interface WorldProvider{
    public WorldProvider newInstance();
    public void loadFromConfig(JsonObject json);
    public World get(Random rand);
}