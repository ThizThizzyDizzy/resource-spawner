package com.thizthizzydizzy.resourcespawner.trigger;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import org.bukkit.scheduler.BukkitRunnable;
import org.hjson.JsonObject;
public class TimerTrigger extends Trigger{
    private int interval;
    @Override
    public Trigger newInstance(){
        return new TimerTrigger();
    }
    @Override
    public void loadFromConfig(ResourceSpawnerCore plugin, JsonObject obj){
        interval = obj.getInt("interval", -1);
        if(interval==-1)throw new IllegalArgumentException("Timer interval must be provided!");
        new BukkitRunnable() {
            @Override
            public void run(){
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }.runTaskTimer(plugin, 0, interval);
    }
}