package com.thizthizzydizzy.resourcespawner.trigger;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.Vanillify;
import java.util.HashSet;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
public class BlockBreakTrigger extends Trigger implements Listener{
    private HashSet<Material> materials;
    @Override
    public Trigger newInstance(){
        return new BlockBreakTrigger();
    }
    @Override
    public void loadFromConfig(ResourceSpawnerCore plugin, JsonObject json){
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
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
        if(materials!=null&&!materials.contains(event.getBlock().getType()))return;
        trigger();
    }
}