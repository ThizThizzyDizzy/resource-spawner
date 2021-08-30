package com.thizthizzydizzy.resourcespawner.provider.world;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.provider.WorldProvider;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
public class UUIDWorldProvider implements WorldProvider{
    private HashSet<UUID> worlds = new HashSet<>();
    private boolean blacklist;
    @Override
    public WorldProvider newInstance(){
        return new UUIDWorldProvider();
    }
    @Override
    public void loadFromConfig(ResourceSpawnerCore plugin, JsonObject json){
        JsonValue value = json.get("worlds");
        if(value.isArray()){
            for(JsonValue val : value.asArray()){
                if(val.isString()){
                    String world = val.asString();
                    worlds.add(UUID.fromString(world));
                }else throw new IllegalArgumentException("World UUID must be a String! "+val.getClass().getName());
            }
        }else throw new IllegalArgumentException("worlds must be an array!");
        blacklist = json.getBoolean("blacklist", false);
    }
    @Override
    public World get(Random rand){
        ArrayList<World> chosenWorlds = new ArrayList<>();
        for(World world : Bukkit.getWorlds()){
            boolean has = worlds.contains(world.getUID());
            if(blacklist)if(!has)chosenWorlds.add(world);
            else if(has)chosenWorlds.add(world);
        }
        return chosenWorlds.get(rand.nextInt(chosenWorlds.size()));
    }
}