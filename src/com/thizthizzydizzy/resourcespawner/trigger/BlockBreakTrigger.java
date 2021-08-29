package com.thizthizzydizzy.resourcespawner.trigger;
import com.thizthizzydizzy.resourcespawner.Vanillify;
import java.util.HashSet;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
public class BlockBreakTrigger extends Trigger implements Listener{
    private final Plugin plugin;
    private HashSet<Material> materials;
    public BlockBreakTrigger(Plugin plugin){
        this.plugin = plugin;
    }
    @Override
    public Trigger newInstance(){
        return new BlockBreakTrigger(plugin);
    }
    @Override
    public void loadFromConfig(JsonObject json){
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