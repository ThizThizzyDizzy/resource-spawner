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
        if(ResourceSpawnerCore.debug)System.out.println("Loading "+getClass().getName());
        JsonValue value = json.get("worlds");
        if(value.isArray()){
            for(JsonValue val : value.asArray()){
                if(val.isString()){
                    String world = val.asString();
                    worlds.add(UUID.fromString(world));
                }else throw new IllegalArgumentException("World UUID must be a String! "+val.getClass().getName());
            }
        }else throw new IllegalArgumentException("worlds must be an array!");
        if(ResourceSpawnerCore.debug)System.out.println("Worlds: "+worlds.toString());
        blacklist = json.getBoolean("blacklist", false);
        if(ResourceSpawnerCore.debug)System.out.println("Blacklist: "+blacklist);
    }
    @Override
    public World get(Random rand){
        if(ResourceSpawnerCore.debug)System.out.println("UUIDWorldProvider Choosing");
        ArrayList<World> chosenWorlds = new ArrayList<>();
        for(World world : Bukkit.getWorlds()){
            boolean has = worlds.contains(world.getUID());
            boolean include = blacklist?!has:has;
            if(ResourceSpawnerCore.debug)System.out.println((include?"Including":"Excluding")+" world "+world.getUID().toString()+" ("+world.getName()+")");
            if(include)chosenWorlds.add(world);
        }
        if(ResourceSpawnerCore.debug)System.out.println("Found "+chosenWorlds.size()+" World"+(chosenWorlds.size()==1?"":"s"));
        if(chosenWorlds.isEmpty())return null;
        return chosenWorlds.get(rand.nextInt(chosenWorlds.size()));
    }
}