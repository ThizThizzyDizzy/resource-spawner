package com.thizthizzydizzy.resourcespawner.trigger;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import org.bukkit.scheduler.BukkitRunnable;
import org.hjson.JsonObject;
public class TimerTrigger extends Trigger{
    private int interval;
    public TimerTrigger(TriggerHandler handler){
        super(handler);
    }
    @Override
    public Trigger newInstance(TriggerHandler handler){
        return new TimerTrigger(handler);
    }
    @Override
    public void loadFromConfig(ResourceSpawnerCore plugin, JsonObject obj){
        if(ResourceSpawnerCore.debug)System.out.println("Loading TimerTrigger");
        interval = obj.getInt("interval", -1);
        if(ResourceSpawnerCore.debug)System.out.println("interval: "+interval);
        if(interval==-1)throw new IllegalArgumentException("Timer interval must be provided!");
        new BukkitRunnable() {
            @Override
            public void run(){
                trigger();
            }
        }.runTaskTimer(plugin, 0, interval);
    }
}