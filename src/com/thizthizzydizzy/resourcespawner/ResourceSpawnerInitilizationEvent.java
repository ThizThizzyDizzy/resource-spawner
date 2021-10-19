package com.thizthizzydizzy.resourcespawner;
import com.thizthizzydizzy.resourcespawner.condition.Condition;
import com.thizthizzydizzy.resourcespawner.distribution.Distribution;
import com.thizthizzydizzy.resourcespawner.provider.LocationProvider;
import com.thizthizzydizzy.resourcespawner.provider.SpawnProvider;
import com.thizthizzydizzy.resourcespawner.provider.WorldProvider;
import com.thizthizzydizzy.resourcespawner.scanner.Scanner;
import com.thizthizzydizzy.resourcespawner.sorter.StructureSorter;
import com.thizthizzydizzy.resourcespawner.trigger.Trigger;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
public class ResourceSpawnerInitilizationEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private final ResourceSpawnerCore plugin;
    public ResourceSpawnerInitilizationEvent(ResourceSpawnerCore plugin){
        this.plugin = plugin;
    }
    public ResourceSpawnerCore getPlugin(){
        return plugin;
    }
    @Override
    public HandlerList getHandlers(){
        return handlers;
    }
    public static HandlerList getHandlerList(){
        return handlers;
    }
    /**
     * Register a new world provider (Equivelent to getPlugin()#registerWorldProvider)
     * @param key the world provider's unique key
     * @param provider the world provider to register
     * @return true if the provider is successfully registered, false otherwise
     * @throws IllegalArgumentException if key is null
     */
    public boolean registerWorldProvider(NamespacedKey key, WorldProvider provider) throws IllegalArgumentException{
        return plugin.registerWorldProvider(key, provider);
    }
    /**
     * Register a new location provider (Equivelent to getPlugin()#registerLocationProvider)
     * @param key the location provider's unique key
     * @param provider the location provider to register
     * @return true if the provider is successfully registered, false otherwise
     * @throws IllegalArgumentException if key is null
     */
    public boolean registerLocationProvider(NamespacedKey key, LocationProvider provider) throws IllegalArgumentException{
        return plugin.registerLocationProvider(key, provider);
    }
    /**
     * Register a new spawn provider (Equivelent to getPlugin()#registerSpawnProvider)
     * @param key the spawn provider's unique key
     * @param provider the spawn provider to register
     * @return true if the provider is successfully registered, false otherwise
     * @throws IllegalArgumentException if key is null
     */
    public boolean registerSpawnProvider(NamespacedKey key, SpawnProvider provider) throws IllegalArgumentException{
        return plugin.registerSpawnProvider(key, provider);
    }
    /**
     * Register a new condition  (Equivelent to getPlugin()#registerCondition)
     * @param key the condition's unique key
     * @param condition the condition to register
     * @return true if the condition is successfully registered, false otherwise
     * @throws IllegalArgumentException if key is null
     */
    public boolean registerCondition(NamespacedKey key, Condition condition) throws IllegalArgumentException{
        return plugin.registerCondition(key, condition);
    }
    /**
     * Register a new structure sorter (Equivelent to getPlugin()#registerStructureSorter)
     * @param key the structure sorter's unique key
     * @param sorter the structure sorter to register
     * @return true if the sorter is successfully registered, false otherwise
     * @throws IllegalArgumentException if key is null
     */
    public boolean registerStructureSorter(NamespacedKey key, StructureSorter sorter) throws IllegalArgumentException{
        return plugin.registerStructureSorter(key, sorter);
    }
    /**
     * Register a new trigger  (Equivelent to getPlugin()#registerTrigger)
     * @param key the trigger's unique key
     * @param trigger the trigger to register
     * @return true if the trigger is successfully registered, false otherwise
     * @throws IllegalArgumentException if key is null
     */
    public boolean registerTrigger(NamespacedKey key, Trigger trigger) throws IllegalArgumentException{
        return plugin.registerTrigger(key, trigger);
    }
    /**
     * Register a new distribution  (Equivelent to getPlugin()#registerDistribution)
     * @param key the distribution's unique key
     * @param distribution the distribution to register
     * @return true if the distribution is successfully registered, false otherwise
     * @throws IllegalArgumentException if key is null
     */
    public boolean registerDistribution(NamespacedKey key, Distribution distribution) throws IllegalArgumentException{
        return plugin.registerDistribution(key, distribution);
    }
    /**
     * Register a new scanner  (Equivelent to getPlugin()#registerScanner)
     * @param key the scanner's unique key
     * @param scanner the scanner to register
     * @return true if the scanner is successfully registered, false otherwise
     * @throws IllegalArgumentException if key is null
     */
    public boolean registerScanner(NamespacedKey key, Scanner scanner) throws IllegalArgumentException{
        return plugin.registerScanner(key, scanner);
    }
}