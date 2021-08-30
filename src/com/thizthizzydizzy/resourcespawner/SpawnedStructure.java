package com.thizthizzydizzy.resourcespawner;
import com.thizthizzydizzy.resourcespawner.provider.spawn.AbstractStructureSpawnProvider;
import com.thizthizzydizzy.resourcespawner.trigger.Trigger;
import com.thizthizzydizzy.resourcespawner.trigger.TriggerListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.hjson.JsonArray;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
public class SpawnedStructure{
    private final AbstractStructureSpawnProvider spawnProvider;
    public final ArrayList<Block> blocks = new ArrayList<>();
    private final World world;
    private final Location pos;
    public int decayTimer = Integer.MIN_VALUE;//min value does not decay
    public Task decayTask;
    public HashMap<Trigger, TriggerListener> triggerListeners = new HashMap<>();
    public SpawnedStructure(AbstractStructureSpawnProvider spawnProvider, World world, Location pos){
        this.spawnProvider = spawnProvider;
        this.world = world;
        this.pos = pos;
    }
    public Task<SpawnedStructure> decay(){
        HashMap<Location, Block> data = new HashMap<>();
        for(Block b : blocks){
            Location offset = new Location(null, b.getX()-pos.getBlockX(), b.getY()-pos.getBlockY(), b.getZ()-pos.getBlockZ());
            BlockData shouldBe = spawnProvider.structure.data.get(offset);
            if(b.getType()!=shouldBe.getMaterial())continue;//not the same block; SKIP!
            data.put(offset, b);
        }
        ArrayList<Location> decayOrder = spawnProvider.decaySorter==null?new ArrayList<>(data.keySet()):spawnProvider.decaySorter.sort(data.keySet());
        return new Task<SpawnedStructure>() {
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
        String key = null;
        for(NamespacedKey k : plugin.spawnProviders.keySet()){
            if(plugin.spawnProviders.get(k)==spawnProvider){
                key = k.toString();
            }
        }
        json.set("spawn_provider", key);
        json.set("world", world.getUID().toString());
        json.set("x", pos.getBlockX());
        json.set("y", pos.getBlockY());
        json.set("z", pos.getBlockZ());
        json.set("decay_timer", decayTimer);
        JsonArray blocks = new JsonArray();
        for(Block b : this.blocks){
            JsonArray block = new JsonArray();
            block.add(b.getX());
            block.add(b.getY());
            block.add(b.getZ());
            blocks.add(block);
        }
        json.set("blocks", blocks);
        return json;
    }
    public static SpawnedStructure load(ResourceSpawnerCore plugin, JsonObject json){
        String spawn_provider = json.get("spawn_provider").asString();
        AbstractStructureSpawnProvider spawnProvider = null;
        for(NamespacedKey k : plugin.spawnProviders.keySet()){
            if(k.toString().equals(spawn_provider))spawnProvider = (AbstractStructureSpawnProvider)plugin.spawnProviders.get(k);
        }
        World world = plugin.getServer().getWorld(UUID.fromString(json.get("world").asString()));
        Location pos = new Location(world, json.get("x").asInt(), json.get("y").asInt(), json.get("z").asInt());
        SpawnedStructure spawnedStructure = new SpawnedStructure(spawnProvider, world, pos);
        spawnedStructure.decayTimer = json.get("decay_timer").asInt();
        JsonArray blocks = json.get("blocks").asArray();
        for(JsonValue val : blocks){
            JsonArray block = val.asArray();
            spawnedStructure.blocks.add(world.getBlockAt(block.get(0).asInt(), block.get(1).asInt(), block.get(2).asInt()));
        }
        AbstractStructureSpawnProvider spawnProviderButEffectivelyFinal = spawnProvider;
        for(Trigger trigger : spawnProvider.resetTriggers.keySet()){
            TriggerListener triggerListener = () -> {
                spawnedStructure.decayTimer = Math.max(spawnedStructure.decayTimer, spawnProviderButEffectivelyFinal.resetTriggers.get(trigger));
            };
            spawnedStructure.triggerListeners.put(trigger, triggerListener);
            trigger.addTriggerListener(triggerListener);
        }
        return spawnedStructure;
    }
}