package com.thizthizzydizzy.resourcespawner.scanner;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Location;
import org.hjson.JsonObject;
public abstract class Scanner{
    public HashMap<String, String> displayNameOverrides = new HashMap<>();
    public int minRange, maxRange;
    public int maxResults;
    public abstract Scanner newInstance();
    public abstract void loadFromConfig(ResourceSpawnerCore plugin, JsonObject obj);
    public abstract ArrayList<String> scan(ResourceSpawnerCore plugin, Location location);
}