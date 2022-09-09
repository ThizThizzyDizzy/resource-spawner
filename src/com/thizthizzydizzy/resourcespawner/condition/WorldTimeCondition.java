package com.thizthizzydizzy.resourcespawner.condition;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.Task;
import org.bukkit.Location;
import org.bukkit.World;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
public class WorldTimeCondition implements Condition{
    private Long min, max;
    @Override
    public Condition newInstance(){
        return new WorldTimeCondition();
    }
    @Override
    public void loadFromConfig(ResourceSpawnerCore plugin, JsonObject json){
        if(ResourceSpawnerCore.debug)System.out.println("Loading "+getClass().getName());
        JsonValue minValue = json.get("min");
        if(minValue!=null)min = minValue.asLong();
        if(ResourceSpawnerCore.debug)System.out.println("min: "+min);
        JsonValue maxValue = json.get("max");
        if(maxValue!=null)max = maxValue.asLong();
        if(ResourceSpawnerCore.debug)System.out.println("max: "+max);
    }
    @Override
    public Task<Boolean> check(World world, Location location){
        if(ResourceSpawnerCore.debug)System.out.println("Creating check task for "+getClass().getName());
        return new Task<Boolean>() {
            @Override
            public String getName(){
                return "time-condition:"+world.getName()+"|"+location.getX()+" "+location.getY()+" "+location.getZ()+"|"+getClass().getName();
            }
            Boolean result = null;
            @Override
            public void step(){
                long time = world.getTime();
                if(min!=null&&time<min)result = false;
                if(max!=null&&time>max)result = false;
                result = true;
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