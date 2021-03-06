package com.thizthizzydizzy.resourcespawner.provider.world;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.provider.WorldProvider;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.hjson.JsonObject;
public class EnvironmentWorldProvider implements WorldProvider{
    private World.Environment environment;
    @Override
    public WorldProvider newInstance(){
        return new EnvironmentWorldProvider();
    }
    @Override
    public void loadFromConfig(ResourceSpawnerCore plugin, JsonObject json){
        if(ResourceSpawnerCore.debug)System.out.println("Loading "+getClass().getName());
        environment = World.Environment.valueOf(json.getString("environment", "normal").toUpperCase(Locale.ROOT).replace("OVERWORLD", "NORMAL"));
        if(ResourceSpawnerCore.debug)System.out.println("environment: "+environment.toString());
    }
    @Override
    public World get(Random rand){
        ArrayList<World> worlds = new ArrayList<>();
        for(World world : Bukkit.getWorlds())if(world.getEnvironment()==environment)worlds.add(world);
        if(worlds.isEmpty())return null;
        return worlds.get(rand.nextInt(worlds.size()));
    }
}