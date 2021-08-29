package com.thizthizzydizzy.resourcespawner.provider.spawn;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.Structure;
import com.thizthizzydizzy.resourcespawner.Vanillify;
import com.thizthizzydizzy.resourcespawner.condition.Condition;
import com.thizthizzydizzy.resourcespawner.provider.SpawnProvider;
import com.thizthizzydizzy.resourcespawner.sorter.StructureSorter;
import com.thizthizzydizzy.resourcespawner.trigger.Trigger;
import java.util.ArrayList;
import java.util.HashSet;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
public abstract class AbstractStructureSpawnProvider extends SpawnProvider{
    protected final ResourceSpawnerCore plugin;
    private ArrayList<Trigger> resetTriggers = new ArrayList<>();
    private StructureSorter sorter;//can be null
    private HashSet<Material> replace;//can be null
    private int minBuildTime;
    private boolean shouldDecay = false;
    private StructureSorter decaySorter;//can be null
    private int decayDelay;
    private Structure structure;
    private int minDecayTime;
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
                    trigger.interval = obj.getInt("delay", -1);
                    if(trigger.interval==-1)throw new IllegalArgumentException("Reset trigger delay must be provided!");
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
                    resetTriggers.add(trigger);
                }
            }
        }
        structure = load(json);
        structure.normalize();
    }
    public abstract Structure load(JsonObject json);//TODO ohno
}