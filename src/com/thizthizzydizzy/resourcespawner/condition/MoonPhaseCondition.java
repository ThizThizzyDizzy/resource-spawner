package com.thizthizzydizzy.resourcespawner.condition;
import com.thizthizzydizzy.resourcespawner.Task;
import org.bukkit.Location;
import org.bukkit.World;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
public class MoonPhaseCondition implements Condition{
    private Integer min, max;
    @Override
    public Condition newInstance(){
        return new MoonPhaseCondition();
    }
    @Override
    public void loadFromConfig(JsonObject json){
        JsonValue minValue = json.get("min");
        if(minValue!=null)min = minValue.asInt();
        JsonValue maxValue = json.get("max");
        if(maxValue!=null)max = maxValue.asInt();
    }
    @Override
    public Task<Boolean> check(World world, Location location){
        return new Task<Boolean>() {
            Boolean result = null;
            @Override
            public void step(){
                long gameTime = world.getFullTime();
                long day = gameTime/24000;
                long phase = day%8;
                if(min!=null&&phase<min)result = false;
                if(max!=null&&phase>max)result = false;
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