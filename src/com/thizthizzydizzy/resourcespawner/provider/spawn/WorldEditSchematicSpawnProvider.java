package com.thizthizzydizzy.resourcespawner.provider.spawn;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.Structure;
import com.thizthizzydizzy.resourcespawner.provider.SpawnProvider;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.hjson.JsonObject;
public class WorldEditSchematicSpawnProvider extends AbstractStructureSpawnProvider{
    @Override
    public SpawnProvider newInstance(){
        return new WorldEditSchematicSpawnProvider();
    }
    @Override
    public Structure load(ResourceSpawnerCore plugin, JsonObject json){
        if(ResourceSpawnerCore.debug)System.out.println("Loading "+getClass().getName());
        String filepath = json.getString("file", null);
        if(ResourceSpawnerCore.debug)System.out.println("file: "+filepath);
        if(filepath==null)throw new IllegalArgumentException("Schematic file must be provided!");
        File file = new File(plugin.getDataFolder(), filepath);
        if(!file.exists())throw new IllegalArgumentException("Could not find schematic file "+filepath+"!");
        if(ResourceSpawnerCore.debug)System.out.println("Reading schematic...");
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        try(ClipboardReader reader = format.getReader(new FileInputStream(file))){
            Clipboard clipboard = reader.read();
            Structure structure = new Structure();
            clipboard.getRegion().forEach((t) -> {
                structure.addBlock(t.getBlockX(), t.getBlockY(), t.getBlockZ(), BukkitAdapter.adapt(clipboard.getBlock(t)));
            });
        if(ResourceSpawnerCore.debug)System.out.println("Finished reading schematic");
            return structure;
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
}