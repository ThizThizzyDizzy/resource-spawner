package com.thizthizzydizzy.resourcespawner.condition;
import com.thizthizzydizzy.resourcespawner.Task;
import com.thizthizzydizzy.resourcespawner.Vanillify;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
public class BlockCondition implements Condition{
    private int xOff;
    private int yOff;
    private int zOff;
    private boolean invert;
    private HashSet<Material> blocks = new HashSet<>();
    @Override
    public Condition newInstance(){
        return new BlockCondition();
    }
    @Override
    public void loadFromConfig(JsonObject json){
        xOff = json.getInt("x_offset", 0);
        yOff = json.getInt("y_offset", 0);
        zOff = json.getInt("z_offset", 0);
        invert = json.getBoolean("invert", false);
        JsonValue value = json.get("blocks");
        if(value.isArray()){
            for(JsonValue val : value.asArray()){
                if(val.isString()){
                    String block = val.asString();
                    blocks.addAll(Vanillify.getBlocks(block));
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
                boolean has = blocks.contains(world.getBlockAt(location.getBlockX()+xOff, location.getBlockY()+yOff, location.getBlockZ()+zOff).getType());
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