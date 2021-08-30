package com.thizthizzydizzy.resourcespawner.condition;
import com.thizthizzydizzy.resourcespawner.Task;
import java.util.HashSet;
import java.util.Locale;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
public class BiomeCondition implements Condition{
    private boolean invert;
    private HashSet<Biome> biomes = new HashSet<>();
    @Override
    public Condition newInstance(){
        return new BiomeCondition();
    }
    @Override
    public void loadFromConfig(JsonObject json){
        invert = json.getBoolean("invert", false);
        JsonValue value = json.get("biomes");
        if(value.isArray()){
            for(JsonValue val : value.asArray()){
                if(val.isString()){
                    biomes.add(Biome.valueOf(val.asString().toUpperCase(Locale.ROOT)));
                }else throw new IllegalArgumentException("Block must be a String! "+val.getClass().getName());
            }
        }else throw new IllegalArgumentException("blocks must be an array!");
    }
    @Override
    public Task<Boolean> check(World world, Location location){
        return new Task<Boolean>() {
            Boolean result = null;
            @Override
            public void step(){
                boolean has = biomes.contains(world.getBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
                if(invert)result = !has;
                else result = has;
            }
            @Override
            public boolean isFinished(){
                return result!=null;
            }
            @Override
            public Boolean getResult(){
                return result;
            }
        };
    }
}