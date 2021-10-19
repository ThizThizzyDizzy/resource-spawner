package com.thizthizzydizzy.resourcespawner.trigger;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.SpawnedStructure;
import com.thizthizzydizzy.resourcespawner.Vanillify;
import java.util.HashSet;
import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
public class BlockBreakTrigger extends StructureTrigger implements Listener{
    private HashSet<Material> materials;
    private SpawnedStructure structure;
    public BlockBreakTrigger(TriggerHandler handler){
        super(handler);
    }
    @Override
    public Trigger newInstance(TriggerHandler handler){
        return new BlockBreakTrigger(handler);
    }
    @Override
    public void loadFromConfig(ResourceSpawnerCore plugin, JsonObject json){
        if(ResourceSpawnerCore.debug)System.out.println("Loading "+getClass().getName());
        JsonValue blox = json.get("blocks");
        if(blox!=null){
            materials = new HashSet<>();
            for(JsonValue val : blox.asArray()){
                materials.addAll(Vanillify.getBlocks(val.asString()));
            }
        }
        if(ResourceSpawnerCore.debug)System.out.println("blocks: "+Objects.toString(materials));
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        if(ResourceSpawnerCore.debug)System.out.println("BlockBreakTrigger detected event");
        if(!structure.blocks.contains(event.getBlock()))return;//not one of my blocks
        if(ResourceSpawnerCore.debug)System.out.println("event was on own structure");
        if(materials!=null&&!materials.contains(event.getBlock().getType()))return;
        trigger();
    }
    @Override
    public StructureTrigger newInstance(ResourceSpawnerCore plugin, SpawnedStructure structure, TriggerHandler handler){
        if(ResourceSpawnerCore.debug)System.out.println("new BlockBreakTrigger structure instance created");
        BlockBreakTrigger trigger = new BlockBreakTrigger(handler);
        trigger.structure = structure;
        plugin.getServer().getPluginManager().registerEvents(trigger, plugin);
        trigger.materials = materials;
        return trigger;
    }
    @Override
    public void dissolve(){
        if(ResourceSpawnerCore.debug)System.out.println("Dissolving BlockBreakTrigger");
        HandlerList.unregisterAll(this);
    }
}