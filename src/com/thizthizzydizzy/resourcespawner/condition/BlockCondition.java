package com.thizthizzydizzy.resourcespawner.condition;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
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
    public void loadFromConfig(ResourceSpawnerCore plugin, JsonObject json){
        if(ResourceSpawnerCore.debug)System.out.println("Loading "+getClass().getName());
        xOff = json.getInt("x_offset", 0);
        if(ResourceSpawnerCore.debug)System.out.println("xOff: "+xOff);
        yOff = json.getInt("y_offset", 0);
        if(ResourceSpawnerCore.debug)System.out.println("yOff: "+yOff);
        zOff = json.getInt("z_offset", 0);
        if(ResourceSpawnerCore.debug)System.out.println("zOff: "+zOff);
        invert = json.getBoolean("invert", false);
        if(ResourceSpawnerCore.debug)System.out.println("invert: "+invert);
        JsonValue value = json.get("blocks");
        if(value.isArray()){
            for(JsonValue val : value.asArray()){
                if(val.isString()){
                    String block = val.asString();
                    blocks.addAll(Vanillify.getBlocks(block));
                }else throw new IllegalArgumentException("Block must be a String! "+val.getClass().getName());
            }
        }else throw new IllegalArgumentException("blocks must be an array!");
        if(ResourceSpawnerCore.debug)System.out.println("blocks: "+blocks.toString());
    }
    @Override
    public Task<Boolean> check(World world, Location location){
        if(ResourceSpawnerCore.debug)System.out.println("Creating check task for "+getClass().getName());
        return new Task<Boolean>() {
            @Override
            public String getName(){
                return "block-condition:"+world.getName()+"|"+location.getX()+" "+location.getY()+" "+location.getZ()+"|"+getClass().getName();
            }
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