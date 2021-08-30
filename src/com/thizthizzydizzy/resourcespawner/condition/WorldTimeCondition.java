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
        JsonValue minValue = json.get("min");
        if(minValue!=null)min = minValue.asLong();
        JsonValue maxValue = json.get("max");
        if(maxValue!=null)max = maxValue.asLong();
    }
    @Override
    public Task<Boolean> check(World world, Location location){
        return new Task<Boolean>() {
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