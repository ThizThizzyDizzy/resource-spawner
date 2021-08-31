package com.thizthizzydizzy.resourcespawner.condition;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.Task;
import java.util.ArrayList;
import java.util.Locale;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
public class EntityProximityCondition implements Condition{
    private int radius;
    private boolean invert;
    private ArrayList<EntityType> entities = new ArrayList<>();
    @Override
    public Condition newInstance(){
        return new EntityProximityCondition();
    }
    @Override
    public void loadFromConfig(ResourceSpawnerCore plugin, JsonObject json){
        if(ResourceSpawnerCore.debug)System.out.println("Loading "+getClass().getName());
        radius = json.get("radius").asInt();//radius is required
        if(ResourceSpawnerCore.debug)System.out.println("radius: "+radius);
        invert = json.getBoolean("invert", false);
        if(ResourceSpawnerCore.debug)System.out.println("invert: "+invert);
        JsonValue ents = json.get("entities");
        if(ents!=null){
            for(JsonValue val : ents.asArray()){
                entities.add(EntityType.valueOf(val.asString().toUpperCase(Locale.ROOT)));
            }
        }
        if(ResourceSpawnerCore.debug)System.out.println("entities: "+entities.toString());
    }
    @Override
    public Task<Boolean> check(World world, Location location){
        if(ResourceSpawnerCore.debug)System.out.println("Creating check task for "+getClass().getName());
        ArrayList<Entity> nearbyEntities;
        if(entities.isEmpty()){
            nearbyEntities = new ArrayList<>(world.getNearbyEntities(location, radius, radius, radius));
        }else{
            nearbyEntities = new ArrayList<>(world.getNearbyEntities(location, radius, radius, radius, (t) -> {
                return entities.contains(t.getType());
            }));
        }
        ArrayList<Entity> importantEntities = new ArrayList<>();
        return new Task<Boolean>() {
            Boolean result = null;
            @Override
            public void step(){
                if(!nearbyEntities.isEmpty()){
                    Entity e = nearbyEntities.remove(0);
                    double distance = e.getLocation().distance(location);
                    if(distance<radius){
                        importantEntities.add(e);
                        if(invert)result = false;//oh no, found an entity
                        else result = true;//yay found an entity
                    }
                    return;
                }
                if(invert)result = true;//yay didn't find an entity
                else result = false;//oh no, didn't find an entity
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