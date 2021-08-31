package com.thizthizzydizzy.resourcespawner.provider.spawn;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.Task;
import com.thizthizzydizzy.resourcespawner.provider.SpawnProvider;
import java.util.Locale;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.hjson.JsonObject;
public class EntitySpawnProvider extends SpawnProvider{
    private EntityType entity;
    @Override
    public SpawnProvider newInstance(){
        return new EntitySpawnProvider();
    }
    @Override
    public void loadFromConfig(ResourceSpawnerCore plugin, JsonObject json){
        if(ResourceSpawnerCore.debug)System.out.println("Loading "+getClass().getName());
        entity = EntityType.valueOf(json.get("entity").asString().toUpperCase(Locale.ROOT));
    }
    @Override
    public Task<Entity> spawn(ResourceSpawnerCore plugin, World world, Location location){
        if(ResourceSpawnerCore.debug)System.out.println("Creating entity spawn task");
        return new Task<Entity>() {
            private Entity result;
            @Override
            public void step(){
                if(ResourceSpawnerCore.debug)System.out.println("Spawning entity");
                result = world.spawnEntity(location, entity);
            }
            @Override
            public boolean isFinished(){
                return result!=null;
            }
            @Override
            public Entity getResult(){
                return result;
            }
        };
    }

}