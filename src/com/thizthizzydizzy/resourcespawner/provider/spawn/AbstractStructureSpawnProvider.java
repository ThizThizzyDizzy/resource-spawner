package com.thizthizzydizzy.resourcespawner.provider.spawn;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.SpawnedStructure;
import com.thizthizzydizzy.resourcespawner.Structure;
import com.thizthizzydizzy.resourcespawner.Task;
import com.thizthizzydizzy.resourcespawner.Vanillify;
import com.thizthizzydizzy.resourcespawner.condition.Condition;
import com.thizthizzydizzy.resourcespawner.provider.SpawnProvider;
import com.thizthizzydizzy.resourcespawner.sorter.StructureSorter;
import com.thizthizzydizzy.resourcespawner.trigger.StructureTrigger;
import com.thizthizzydizzy.resourcespawner.trigger.Trigger;
import com.thizthizzydizzy.resourcespawner.trigger.TriggerListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
public abstract class AbstractStructureSpawnProvider extends SpawnProvider{
    public HashMap<Trigger, Integer> resetTriggers = new HashMap<>();
    public StructureSorter sorter;//can be null
    public HashSet<Material> replace;//can be null
    public boolean shouldDecay = false;
    public StructureSorter decaySorter;//can be null
    public int decayDelay;
    private Structure structure;
    public Material decayTo = Material.AIR;
    public String name;
    private int rotation = 0;//0 1 2 3
    private boolean randomRotation = false;
    private final Random rand = new Random();
    @Override
    public void loadFromConfig(ResourceSpawnerCore plugin, JsonObject json){
        name = json.getString("name", null);
        if(ResourceSpawnerCore.debug)System.out.println("Loading Structure Spawn Provider "+name);
        if(name==null)throw new IllegalArgumentException("Name cannot be null!");
        String buildOrder = json.getString("build_order", null);
        if(ResourceSpawnerCore.debug)System.out.println("Build order: "+buildOrder);
        if(buildOrder!=null){
            sorter = plugin.getStructureSorter(buildOrder);
            if(sorter==null)throw new IllegalArgumentException("Unknown structure sorter: "+buildOrder);
        }
        JsonValue rotation = json.get("rotation");
        if(rotation!=null){
            if(rotation.isNumber()){
                int rot = rotation.asInt();
                if(rot%90!=0)throw new IllegalArgumentException("Rotation must be a multiple of 90!");
                rot/=90;
                while(rot<0)rot+=4;
                while(rot>=4)rot-=4;
                this.rotation = rot;
            }else if(rotation.isString()){
                if(rotation.asString().equalsIgnoreCase("random")){
                    randomRotation = true;
                }else throw new IllegalArgumentException("Invalid string rotation! expected 'random'");
            }else{
                throw new IllegalArgumentException("Rotation is invalid type! Expected number or string, found "+rotation.getType().toString());
            }
        }
        JsonValue repl = json.get("replace");
        if(repl!=null){
            replace = new HashSet<>();
            for(JsonValue val : repl.asArray()){
                replace.addAll(Vanillify.getBlocks(val.asString()));
            }
        }
        if(ResourceSpawnerCore.debug)System.out.println("replace: "+Objects.toString(replace));
        JsonValue decay = json.get("decay");
        if(decay!=null){
            if(ResourceSpawnerCore.debug)System.out.println("Loading decay settings");
            JsonObject decayObj = decay.asObject();
            shouldDecay = true;
            String decayOrder = decayObj.getString("decay_order", null);
            if(ResourceSpawnerCore.debug)System.out.println("Decay order: "+decayOrder);
            if(decayOrder!=null){
                decaySorter = plugin.getStructureSorter(decayOrder);
                if(decaySorter==null)throw new IllegalArgumentException("Unknown structure sorter: "+decayOrder);
            }
            decayDelay = decayObj.getInt("delay", -1);
            if(ResourceSpawnerCore.debug)System.out.println("delay: "+decayDelay);
            if(decayDelay==-1)throw new IllegalArgumentException("Decay delay must be provided!");
            if(ResourceSpawnerCore.debug)System.out.println("Loading reset triggers");
            JsonValue resetTriggersObj = decayObj.get("reset_triggers");
            if(resetTriggersObj!=null){
                for(JsonValue value : resetTriggersObj.asArray()){
                    JsonObject obj = value.asObject();
                    String name = obj.getString("trigger", null);
                    if(ResourceSpawnerCore.debug)System.out.println("Loading reset trigger "+name);
                    Trigger trigger = plugin.getTrigger(name);
                    if(trigger==null)throw new IllegalArgumentException("Unknown trigger: "+name);
                    trigger.loadFromConfig(plugin, obj);
                    int delay = obj.getInt("delay", -1);
                    if(ResourceSpawnerCore.debug)System.out.println("delay: "+delay);
                    if(delay==-1)throw new IllegalArgumentException("Reset trigger delay must be provided!");
                    JsonValue conditionsJson = obj.get("conditions");
                    if(ResourceSpawnerCore.debug)System.out.println("Loading conditions");
                    if(conditionsJson!=null){
                        for(JsonValue v : conditionsJson.asArray()){
                            if(v.isObject()){
                                JsonObject conditionJson = v.asObject();
                                String conditionType = conditionJson.getString("type", null);
                                if(ResourceSpawnerCore.debug)System.out.println("Loading condition "+conditionType);
                                Condition condition = plugin.getCondition(conditionType);
                                if(condition==null)throw new IllegalArgumentException("Unknown condition: "+conditionType);
                                condition.loadFromConfig(plugin, conditionJson);
                                trigger.conditions.add(condition);
                            }else throw new IllegalArgumentException("Invalid condition: "+v.getType().getClass().getName());
                        }
                    }
                    resetTriggers.put(trigger, delay);
                }
            }
            String decayToS = decayObj.getString("decay_to", null);
            if(ResourceSpawnerCore.debug)System.out.println("Decay to: "+decayToS);
            if(decayToS!=null)decayTo = Material.matchMaterial(decayToS);
        }
        if(ResourceSpawnerCore.debug)System.out.println("Loading structure");
        structure = load(plugin, json);
        structure.normalize();
        if(ResourceSpawnerCore.debug)System.out.println("Structure loaded");
    }
    public abstract Structure load(ResourceSpawnerCore plugin, JsonObject json);
    @Override
    public Task<SpawnedStructure> spawn(ResourceSpawnerCore plugin, World world, Location location){
        if(ResourceSpawnerCore.debug)System.out.println("Creating structure spawn task");
        int chosenRotation = randomRotation?rand.nextInt(4):rotation;
        Structure structure = getStructure(chosenRotation);
        return new Task<SpawnedStructure>(){
            private SpawnedStructure spawnedStructure = new SpawnedStructure(AbstractStructureSpawnProvider.this, world, location, chosenRotation);
            private ArrayList<Location> data = sorter==null?new ArrayList<>(structure.data.keySet()):sorter.sort(structure.data.keySet());
            private boolean finished = false;
            @Override
            public void step(){
                if(!data.isEmpty()){
                    Location pos = data.remove(0);
                    Block block = world.getBlockAt(location.getBlockX()+pos.getBlockX(), location.getBlockY()+pos.getBlockY(), location.getBlockZ()+pos.getBlockZ());
                    if(replace!=null&&!replace.contains(block.getType()))return;//skip this block
                    BlockData blockData = structure.data.get(pos);
                    block.setType(blockData.getMaterial());
                    block.setBlockData(blockData, false);
                    spawnedStructure.blocks.add(block);
                    return;
                }
                if(ResourceSpawnerCore.debug)System.out.println("Finished spawning structure, setting triggers");
                spawnedStructure.decayTimer = decayDelay;
                for(Trigger trigger : resetTriggers.keySet()){
                    TriggerListener triggerListener = () -> {
                        spawnedStructure.decayTimer = Math.max(spawnedStructure.decayTimer, resetTriggers.get(trigger));
                    };
                    if(trigger instanceof StructureTrigger){
                        //make a unique instance for this structure
                        StructureTrigger st = ((StructureTrigger)trigger).newInstance(plugin, spawnedStructure);
                        spawnedStructure.triggerListeners.put(st, triggerListener);
                        st.addTriggerListener(triggerListener);
                    }else{
                        spawnedStructure.triggerListeners.put(trigger, triggerListener);
                        trigger.addTriggerListener(triggerListener);
                    }
                }
                if(ResourceSpawnerCore.debug)System.out.println("Triggers are set");
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
    public Structure getStructure(int rotation){
        return structure.getRotated(rotation);
    }
}