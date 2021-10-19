package com.thizthizzydizzy.resourcespawner.trigger;
import org.bukkit.Location;
import org.bukkit.World;
public interface TriggerListener{
    public void trigger();
    public World getWorld();
    public Location getLocation();
}