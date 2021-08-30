package com.thizthizzydizzy.resourcespawner.trigger;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.SpawnedStructure;
import com.thizthizzydizzy.resourcespawner.Vanillify;
import java.util.HashSet;
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
    @Override
    public Trigger newInstance(){
        if(ResourceSpawnerCore.debug)System.out.println("BlockBreakTrigger new instance");
        return new BlockBreakTrigger();
    }
    @Override
    public void loadFromConfig(ResourceSpawnerCore plugin, JsonObject json){
        if(ResourceSpawnerCore.debug)System.out.println("BlockBreakTrigger loading from config");
        JsonValue blox = json.get("blocks");
        if(blox!=null){
            materials = new HashSet<>();
            for(JsonValue val : blox.asArray()){
                materials.addAll(Vanillify.getBlocks(val.asString()));
            }
        }
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        if(ResourceSpawnerCore.debug)System.out.println("BlockBreakTrigger onBlockBreak");
        if(!structure.blocks.contains(event.getBlock()))return;//not one of my blocks
        if(ResourceSpawnerCore.debug)System.out.println("BlockBreakTrigger onBlockBreak - one of my blocks was broken!");
        if(materials!=null&&!materials.contains(event.getBlock().getType()))return;
        trigger();
    }
    @Override
    public StructureTrigger newInstance(ResourceSpawnerCore plugin, SpawnedStructure structure){
        if(ResourceSpawnerCore.debug)System.out.println("BlockBreakTrigger new structure instance!");
        BlockBreakTrigger trigger = new BlockBreakTrigger();
        trigger.structure = structure;
        plugin.getServer().getPluginManager().registerEvents(trigger, plugin);
        trigger.materials = materials;
        return trigger;
    }
    @Override
    public void dissolve(){
        if(ResourceSpawnerCore.debug)System.out.println("BlockBreakTrigger dissolving");
        HandlerList.unregisterAll(this);
    }
}