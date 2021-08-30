package com.thizthizzydizzy.resourcespawner.provider.spawn;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.SpawnedStructure;
import com.thizthizzydizzy.resourcespawner.Structure;
import com.thizthizzydizzy.resourcespawner.Task;
import com.thizthizzydizzy.resourcespawner.Vanillify;
import com.thizthizzydizzy.resourcespawner.condition.Condition;
import com.thizthizzydizzy.resourcespawner.provider.SpawnProvider;
import com.thizthizzydizzy.resourcespawner.sorter.StructureSorter;
import com.thizthizzydizzy.resourcespawner.trigger.Trigger;
import com.thizthizzydizzy.resourcespawner.trigger.TriggerListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
public abstract class AbstractStructureSpawnProvider extends SpawnProvider{
    protected final ResourceSpawnerCore plugin;
    public HashMap<Trigger, Integer> resetTriggers = new HashMap<>();
    public StructureSorter sorter;//can be null
    public HashSet<Material> replace;//can be null
    public int minBuildTime;
    public boolean shouldDecay = false;
    public StructureSorter decaySorter;//can be null
    public int decayDelay;
    public Structure structure;
    public int minDecayTime;
    public Material decayTo = Material.AIR;
    public AbstractStructureSpawnProvider(ResourceSpawnerCore plugin){
        this.plugin = plugin;
    }
    @Override
    public void loadFromConfig(JsonObject json){
        String buildOrder = json.getString("build_order", null);
        if(buildOrder!=null){
            if(!buildOrder.contains(":"))buildOrder = plugin.defaultNamespace+":"+buildOrder;
            for(NamespacedKey key : plugin.structureSorters.keySet()){
                if(key.toString().equals(buildOrder)){
                    sorter = plugin.structureSorters.get(key);
                }
            }
            if(sorter==null)throw new IllegalArgumentException("Unknown structure sorter: "+buildOrder);
        }
        JsonValue repl = json.get("replace");
        if(repl!=null){
            replace = new HashSet<>();
            for(JsonValue val : repl.asArray()){
                replace.addAll(Vanillify.getBlocks(val.asString()));
            }
        }
        minBuildTime = json.getInt("min_build_time", 0);
        JsonValue decay = json.get("decay");
        if(decay!=null){
            JsonObject decayObj = decay.asObject();
            shouldDecay = true;
            String decayOrder = decayObj.getString("decay_order", null);
            if(decayOrder!=null){
                if(!decayOrder.contains(":"))decayOrder = plugin.defaultNamespace+":"+decayOrder;
                for(NamespacedKey key : plugin.structureSorters.keySet()){
                    if(key.toString().equals(decayOrder)){
                        decaySorter = plugin.structureSorters.get(key);
                    }
                }
                if(decaySorter==null)throw new IllegalArgumentException("Unknown structure sorter: "+decayOrder);
            }
            decayDelay = decayObj.getInt("delay", -1);
            if(decayDelay==-1)throw new IllegalArgumentException("Decay delay must be provided!");
            minDecayTime = decayObj.getInt("min_decay_time", 0);
            JsonValue resetTriggersObj = json.get("reset_triggers");
            if(resetTriggersObj!=null){
                for(JsonValue value : resetTriggersObj.asArray()){
                    JsonObject obj = value.asObject();
                    String name = obj.getString("trigger", null);
                    if(name==null)throw new IllegalArgumentException("Trigger cannot be null!");
                    if(!name.contains(":"))name = plugin.defaultNamespace+":"+name;
                    Trigger trigger = null;
                    for(NamespacedKey key : plugin.triggers.keySet()){
                        if(key.toString().equals(name)){
                            trigger = plugin.triggers.get(key).newInstance();
                            trigger.loadFromConfig(obj);
                        }
                    }
                    if(trigger==null)throw new IllegalArgumentException("Unknown trigger: "+name);
                    int delay = obj.getInt("delay", -1);
                    if(delay==-1)throw new IllegalArgumentException("Reset trigger delay must be provided!");
                    JsonValue conditionsJson = obj.get("conditions");//TODO this is duplicated from ResourceSpawnerCore
                    if(conditionsJson!=null){
                        for(JsonValue v : conditionsJson.asArray()){
                            if(v.isObject()){
                                JsonObject conditionJson = v.asObject();
                                String conditionType = conditionJson.getString("type", null);
                                if(conditionType==null)throw new IllegalArgumentException("Condition type cannot be null!");
                                if(!conditionType.contains(":"))conditionType = plugin.defaultNamespace+":"+conditionType;
                                Condition condition = null;
                                for(NamespacedKey key : plugin.conditions.keySet()){
                                    if(key.toString().equals(conditionType)){
                                        condition = plugin.conditions.get(key).newInstance();
                                        condition.loadFromConfig(conditionJson);
                                    }
                                }
                                if(condition==null)throw new IllegalArgumentException("Unknown condition: "+conditionType);
                                trigger.conditions.add(condition);
                            }else throw new IllegalArgumentException("Invalid condition: "+v.getType().getClass().getName());
                        }
                    }
                    resetTriggers.put(trigger, delay);
                }
            }
            String decayToS = decayObj.getString("decay_to", null);
            if(decayToS!=null)decayTo = Material.matchMaterial(decayToS);
        }
        structure = load(json);
        structure.normalize();
    }
    public abstract Structure load(JsonObject json);
    @Override
    public Task<SpawnedStructure> spawn(World world, Location location){
        return new Task<SpawnedStructure>(){
            private SpawnedStructure spawnedStructure = new SpawnedStructure(AbstractStructureSpawnProvider.this, world, location);
            private ArrayList<Location> data = sorter==null?new ArrayList<>(structure.data.keySet()):sorter.sort(structure.data.keySet());
            private boolean finished = false;
            @Override
            public void step(){
                if(!data.isEmpty()){
                    Location pos = data.remove(0);
                    Block block = world.getBlockAt(location.getBlockX()+pos.getBlockX(), location.getBlockY()+pos.getBlockY(), location.getBlockZ()+pos.getBlockZ());
                    BlockData blockData = structure.data.get(pos);
                    block.setType(blockData.getMaterial());
                    block.setBlockData(blockData, false);
                    spawnedStructure.blocks.add(block);
                    return;
                }
                spawnedStructure.decayTimer = decayDelay;
                for(Trigger trigger : resetTriggers.keySet()){
                    TriggerListener triggerListener = () -> {
                        spawnedStructure.decayTimer = Math.max(spawnedStructure.decayTimer, resetTriggers.get(trigger));
                    };
                    spawnedStructure.triggerListeners.put(trigger, triggerListener);
                    trigger.addTriggerListener(triggerListener);
                }
                finished = true;
            }
            @Override
            public boolean isFinished(){
                return finished;
            }
            @Override
            public SpawnedStructure getResult(){
                return spawnedStructure;
            }
        };
    }
}