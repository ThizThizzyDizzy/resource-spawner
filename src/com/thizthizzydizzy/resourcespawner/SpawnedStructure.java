package com.thizthizzydizzy.resourcespawner;
import com.thizthizzydizzy.resourcespawner.provider.SpawnProvider;
import com.thizthizzydizzy.resourcespawner.provider.spawn.AbstractStructureSpawnProvider;
import com.thizthizzydizzy.resourcespawner.trigger.StructureTrigger;
import com.thizthizzydizzy.resourcespawner.trigger.Trigger;
import com.thizthizzydizzy.resourcespawner.trigger.TriggerListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.hjson.JsonObject;
public class SpawnedStructure{
    private final AbstractStructureSpawnProvider spawnProvider;
    public final ArrayList<Block> blocks = new ArrayList<>();
    private final World world;
    private final Location pos;
    public int decayTimer = Integer.MIN_VALUE;//min value does not decay
    public Task decayTask;
    public HashMap<Trigger, TriggerListener> triggerListeners = new HashMap<>();
    private final int rotation;
    public SpawnedStructure(AbstractStructureSpawnProvider spawnProvider, World world, Location pos, int rotation){
        this.spawnProvider = spawnProvider;
        this.world = world;
        this.pos = pos;
        this.rotation = rotation;
    }
    public Task<SpawnedStructure> decay(){
        if(ResourceSpawnerCore.debug)System.out.println("Preparing decay task");
        HashMap<Location, Block> data = new HashMap<>();
        for(Block b : blocks){
            Location offset = new Location(null, b.getX()-pos.getBlockX(), b.getY()-pos.getBlockY(), b.getZ()-pos.getBlockZ());
            BlockData shouldBe = spawnProvider.getStructure(rotation).data.get(offset);
            if(b.getType()!=shouldBe.getMaterial())continue;//not the same block; SKIP!
            data.put(offset, b);
        }
        ArrayList<Location> decayOrder = spawnProvider.decaySorter==null?new ArrayList<>(data.keySet()):spawnProvider.decaySorter.sort(data.keySet());
        if(ResourceSpawnerCore.debug)System.out.println("Creating decay task");
        return new Task<SpawnedStructure>() {
            @Override
            public String getName(){
                return "decay:"+world.getName()+"|"+pos.getX()+" "+pos.getY()+" "+pos.getZ()+"|"+spawnProvider.name+"|"+spawnProvider.getClass().getName();
            }
            private boolean finished = false;
            @Override
            public void step(){
                if(!decayOrder.isEmpty()){
                    Location pos = decayOrder.remove(0);
                    Block block = data.get(pos);
                    block.setType(spawnProvider.decayTo);
                    return;
                }
                for(Trigger t : triggerListeners.keySet()){
                    t.removeTriggerListener(triggerListeners.get(t));
                    if(t instanceof StructureTrigger){
                        ((StructureTrigger)t).dissolve();
                    }
                }
                finished = true;
            }
            @Override
            public boolean isFinished(){
                return finished;
            }
            @Override
            public SpawnedStructure getResult(){
                return SpawnedStructure.this;
            }
        };
    }
    public JsonObject save(ResourceSpawnerCore plugin, JsonObject json){
        json.set("spawn_provider", spawnProvider.name);
        json.set("world", world.getUID().toString());
        json.set("x", pos.getBlockX());
        json.set("y", pos.getBlockY());
        json.set("z", pos.getBlockZ());
        json.set("decay_timer", decayTimer);
        return json;
    }
    public static SpawnedStructure load(ResourceSpawnerCore plugin, JsonObject json){
        World world = plugin.getServer().getWorld(UUID.fromString(json.get("world").asString()));
        Location pos = new Location(world, json.get("x").asInt(), json.get("y").asInt(), json.get("z").asInt());
        int rotation = json.getInt("rotation", 0);
        String spawn_provider = json.get("spawn_provider").asString();
        AbstractStructureSpawnProvider spawnProvider = null;
        for(ResourceSpawner s : plugin.resourceSpawners){
            for(SpawnProvider provider : s.spawnProviders.keySet()){
                if(provider instanceof AbstractStructureSpawnProvider){
                    if(spawn_provider.equals(((AbstractStructureSpawnProvider)provider).name))spawnProvider = (AbstractStructureSpawnProvider)provider;
                }
            }
        }
        if(spawnProvider==null){
            plugin.getLogger().log(Level.WARNING, "Failed to load spawned structure: unknown spawn provider {0}! Structure position: ({1},{2},{3}) in world {4}", new Object[]{spawn_provider, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), world.getName()});
        }
        SpawnedStructure spawnedStructure = new SpawnedStructure(spawnProvider, world, pos, rotation);
        spawnedStructure.decayTimer = json.get("decay_timer").asInt();
        for(Location l : spawnedStructure.spawnProvider.getStructure(rotation).data.keySet()){
            spawnedStructure.blocks.add(world.getBlockAt(pos.getBlockX()+l.getBlockX(), pos.getBlockY()+l.getBlockY(), pos.getBlockZ()+l.getBlockZ()));
        }
        AbstractStructureSpawnProvider spawnProviderButEffectivelyFinal = spawnProvider;
        for(Trigger trigger : spawnProvider.resetTriggers.keySet()){
            TriggerListener triggerListener = new TriggerListener(){
                @Override
                public void trigger(){
                    spawnedStructure.decayTimer = Math.max(spawnedStructure.decayTimer, spawnProviderButEffectivelyFinal.resetTriggers.get(trigger));
                }
                @Override
                public World getWorld(){
                    return world;
                }
                @Override
                public Location getLocation(){
                    return pos;
                }
            };
            if(trigger instanceof StructureTrigger){
                //make a unique instance for this structure
                StructureTrigger st = ((StructureTrigger)trigger).newInstance(plugin, spawnedStructure, spawnProvider.resourceSpawner);
                spawnedStructure.triggerListeners.put(st, triggerListener);
                st.addTriggerListener(triggerListener);
            }else{
                spawnedStructure.triggerListeners.put(trigger, triggerListener);
                trigger.addTriggerListener(triggerListener);
            }
        }
        return spawnedStructure;
    }
    public World getWorld(){
        return world;
    }
    public Location getLocation(){
        return pos;
    }
    public String getName(){
        return spawnProvider.name;
    }
}