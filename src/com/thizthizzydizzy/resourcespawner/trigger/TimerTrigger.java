package com.thizthizzydizzy.resourcespawner.trigger;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.hjson.JsonObject;
public class TimerTrigger extends Trigger{
    private final Plugin plugin;
    public TimerTrigger(Plugin plugin){
        this.plugin = plugin;
    }
    @Override
    public Trigger newInstance(){
        return new TimerTrigger(plugin);
    }
    @Override
    public void loadFromConfig(JsonObject obj){
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