package com.thizthizzydizzy.resourcespawner;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
public class Vanillify{
    public static ArrayList<Material> getBlocks(String name){
        ArrayList<Material> blocks = new ArrayList<>();
        if(name.startsWith("#")){
            Iterable<Tag<Material>> tags = Bukkit.getTags(Tag.REGISTRY_BLOCKS, Material.class);
            for(Tag<Material> tag : tags){
                if(tag.getKey().toString().equals(name.substring(1))){
                    blocks.addAll(tag.getValues());
                    break;
                }
            }
        }else{
            blocks.add(Material.matchMaterial(name));
        }
        return blocks;
    }
}