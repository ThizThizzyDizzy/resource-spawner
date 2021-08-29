package com.thizthizzydizzy.resourcespawner.provider.world;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.hjson.JsonObject;
import com.thizthizzydizzy.resourcespawner.provider.WorldProvider;
public class EnvironmentWorldProvider implements WorldProvider{
    private World.Environment environment;
    @Override
    public WorldProvider newInstance(){
        return new EnvironmentWorldProvider();
    }
    @Override
    public void loadFromConfig(JsonObject json){
        environment = World.Environment.valueOf(json.getString("environment", "overworld").toUpperCase(Locale.ROOT));
    }
    @Override
    public World get(Random rand){
        ArrayList<World> worlds = new ArrayList<>();
        for(World world : Bukkit.getWorlds())if(world.getEnvironment()==environment)worlds.add(world);
        return worlds.get(rand.nextInt(worlds.size()));
    }
}