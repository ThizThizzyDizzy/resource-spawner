package com.thizthizzydizzy.resourcespawner;
import com.thizthizzydizzy.resourcespawner.condition.Condition;
import com.thizthizzydizzy.resourcespawner.condition.CubeFillCondition;
import com.thizthizzydizzy.resourcespawner.condition.CubeWorldGuardRegionCondition;
import com.thizthizzydizzy.resourcespawner.provider.LocationProvider;
import com.thizthizzydizzy.resourcespawner.provider.SpawnProvider;
import com.thizthizzydizzy.resourcespawner.provider.WorldProvider;
import com.thizthizzydizzy.resourcespawner.provider.location.SquareLocationProvider;
import com.thizthizzydizzy.resourcespawner.provider.spawn.WorldEditSchematicSpawnProvider;
import com.thizthizzydizzy.resourcespawner.provider.world.EnvironmentWorldProvider;
import com.thizthizzydizzy.resourcespawner.sorter.CenterStructureSorter;
import com.thizthizzydizzy.resourcespawner.sorter.RandomStructureSorter;
import com.thizthizzydizzy.resourcespawner.sorter.StructureSorter;
import com.thizthizzydizzy.resourcespawner.trigger.BlockBreakTrigger;
import com.thizthizzydizzy.resourcespawner.trigger.TimerTrigger;
import com.thizthizzydizzy.resourcespawner.trigger.Trigger;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.hjson.JsonArray;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.hjson.Stringify;
public class ResourceSpawnerCore extends JavaPlugin implements Listener{
    public final String defaultNamespace = "resourcespawner";
    public final ArrayList<ResourceSpawner> resourceSpawners = new ArrayList<>();
    public final HashMap<NamespacedKey, WorldProvider> worldProviders = new HashMap<>();
    public final HashMap<NamespacedKey, LocationProvider> locationProviders = new HashMap<>();
    public final HashMap<NamespacedKey, SpawnProvider> spawnProviders = new HashMap<>();
    public final HashMap<NamespacedKey, Condition> conditions = new HashMap<>();
    public final HashMap<NamespacedKey, StructureSorter> structureSorters = new HashMap<>();
    public final HashMap<NamespacedKey, Trigger> triggers = new HashMap<>();
    public static boolean debug = false;
    /**
     * Register a new world provider
     * @param key the world provider's unique key
     * @param provider the world provider to register
     * @return true if the provider is successfully registered, false otherwise
     * @throws IllegalArgumentException if key is null
     */
    public boolean registerWorldProvider(NamespacedKey key, WorldProvider provider) throws IllegalArgumentException{
        if(key==null)throw new IllegalArgumentException("Key must not be null!");
        if(worldProviders.containsKey(key))return false;
        worldProviders.put(key, provider);
        return true;
    }
    /**
     * Register a new location provider
     * @param key the location provider's unique key
     * @param provider the location provider to register
     * @return true if the provider is successfully registered, false otherwise
     * @throws IllegalArgumentException if key is null
     */
    public boolean registerLocationProvider(NamespacedKey key, LocationProvider provider) throws IllegalArgumentException{
        if(key==null)throw new IllegalArgumentException("Key must not be null!");
        if(locationProviders.containsKey(key))return false;
        locationProviders.put(key, provider);
        return true;
    }
    /**
     * Register a new spawn provider
     * @param key the spawn provider's unique key
     * @param provider the spawn provider to register
     * @return true if the provider is successfully registered, false otherwise
     * @throws IllegalArgumentException if key is null
     */
    public boolean registerSpawnProvider(NamespacedKey key, SpawnProvider provider) throws IllegalArgumentException{
        if(key==null)throw new IllegalArgumentException("Key must not be null!");
        if(spawnProviders.containsKey(key))return false;
        spawnProviders.put(key, provider);
        return true;
    }
    /**
     * Register a new condition
     * @param key the condition's unique key
     * @param condition the condition to register
     * @return true if the provider is successfully registered, false otherwise
     * @throws IllegalArgumentException if key is null
     */
    public boolean registerCondition(NamespacedKey key, Condition condition) throws IllegalArgumentException{
        if(key==null)throw new IllegalArgumentException("Key must not be null!");
        if(conditions.containsKey(key))return false;
        conditions.put(key, condition);
        return true;
    }
    /**
     * Register a new structure sorter
     * @param key the structure sorter's unique key
     * @param sorter the structure sorter to register
     * @return true if the sorter is successfully registered, false otherwise
     * @throws IllegalArgumentException if key is null
     */
    public boolean registerStructureSorter(NamespacedKey key, StructureSorter sorter) throws IllegalArgumentException{
        if(key==null)throw new IllegalArgumentException("Key must not be null!");
        if(structureSorters.containsKey(key))return false;
        structureSorters.put(key, sorter);
        return true;
    }
    /**
     * Register a new trigger
     * @param key the trigger's unique key
     * @param trigger the trigger to register
     * @return true if the provider is successfully registered, false otherwise
     * @throws IllegalArgumentException if key is null
     */
    public boolean registerTrigger(NamespacedKey key, Trigger trigger) throws IllegalArgumentException{
        if(key==null)throw new IllegalArgumentException("Key must not be null!");
        if(triggers.containsKey(key))return false;
        triggers.put(key, trigger);
        return true;
    }
    @Override
    public void onEnable(){
        PluginManager pm = getServer().getPluginManager();
        PluginDescriptionFile pdfFile = getDescription();
        pm.registerEvents(this, this);
        Bukkit.getServer().getPluginManager().callEvent(new ResourceSpawnerInitilizationEvent(this));
        File configFile = new File(getDataFolder(), "config.hjson");
        if(!getDataFolder().exists())getDataFolder().mkdirs();
        if(!configFile.exists()){
            try{
                configFile.createNewFile();
                try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFile)))){
                    writer.write("{\n" +
                                 "    resource_spawners: [\n" +
                                 "    ]\n" +
                                 "}");
                }
            }catch(IOException ex){
                getLogger().log(Level.SEVERE, "Failed to generate configuration file", ex);
            }
        }
        try{
            JsonObject json = JsonValue.readHjson(new InputStreamReader(new FileInputStream(configFile))).asObject();
            JsonArray spawners = json.get("resource_spawners").asArray();
            for(JsonValue value : spawners){
                if(value.isObject()){
                    JsonObject spawner = value.asObject();
                    String name = spawner.getString("name", null);
                    if(name==null)throw new IllegalArgumentException("Spawner name cannot be null!");
                    for(ResourceSpawner rs : resourceSpawners){
                        if(rs.name.equals(name))throw new IllegalArgumentException("Resource spawner "+name+" already exists! Each resource spawner must have a unique name");
                    }
                    ResourceSpawner resourceSpawner = new ResourceSpawner(name);
                    JsonValue world_providers = spawner.get("world_providers");
                    if(world_providers==null)getLogger().log(Level.WARNING, "Resource spawner {0} does not have any world providers!", name);
                    else{
                        for(JsonValue val : world_providers.asArray()){
                            if(val.isObject()){
                                JsonObject worldProvider = val.asObject();
                                String type = worldProvider.getString("type", null);
                                if(type==null)throw new IllegalArgumentException("World provider type cannot be null!");
                                int weight = worldProvider.getInt("weight", 1);
                                if(!type.contains(":"))type = defaultNamespace+":"+type;
                                WorldProvider provider = null;
                                for(NamespacedKey key : worldProviders.keySet()){
                                    if(key.toString().equals(type)){
                                        provider = worldProviders.get(key).newInstance();
                                        provider.loadFromConfig(worldProvider);
                                    }
                                }
                                if(provider==null)throw new IllegalArgumentException("Unknown world provider: "+type);
                                resourceSpawner.worldProviders.put(provider, weight);
                            }else throw new IllegalArgumentException("Invalid world provider: "+val.getType().getClass().getName());
                        }
                    }
                    JsonValue location_providers = spawner.get("location_providers");
                    if(location_providers==null)getLogger().log(Level.WARNING, "Resource spawner {0} does not have any location providers!", name);
                    else{
                        for(JsonValue val : location_providers.asArray()){
                            if(val.isObject()){
                                JsonObject locationProvider = val.asObject();
                                String type = locationProvider.getString("type", null);
                                if(type==null)throw new IllegalArgumentException("Location provider type cannot be null!");
                                int weight = locationProvider.getInt("weight", 1);
                                if(!type.contains(":"))type = defaultNamespace+":"+type;
                                LocationProvider provider = null;
                                for(NamespacedKey key : locationProviders.keySet()){
                                    if(key.toString().equals(type)){
                                        provider = locationProviders.get(key).newInstance();
                                        provider.loadFromConfig(locationProvider);
                                    }
                                }
                                if(provider==null)throw new IllegalArgumentException("Unknown location provider: "+type);
                                resourceSpawner.locationProviders.put(provider, weight);
                            }else throw new IllegalArgumentException("Invalid location provider: "+val.getType().getClass().getName());
                        }
                    }
                    JsonValue spawns = spawner.get("spawns");
                    if(spawns==null)getLogger().log(Level.WARNING, "Resource spawner {0} does not have any spawns!", name);
                    else{
                        for(JsonValue val : spawns.asArray()){
                            if(val.isObject()){
                                JsonObject spawn = val.asObject();
                                String type = spawn.getString("type", null);
                                if(type==null)throw new IllegalArgumentException("Spawn type cannot be null!");
                                int weight = spawn.getInt("weight", 1);
                                if(!type.contains(":"))type = defaultNamespace+":"+type;
                                SpawnProvider provider = null;
                                for(NamespacedKey key : spawnProviders.keySet()){
                                    if(key.toString().equals(type)){
                                        provider = spawnProviders.get(key).newInstance();
                                        provider.loadFromConfig(spawn);
                                    }
                                }
                                if(provider==null)throw new IllegalArgumentException("Unknown spawn provider: "+type);
                                JsonValue conditionsJson = spawn.get("conditions");
                                if(conditionsJson!=null){
                                    for(JsonValue v : conditionsJson.asArray()){
                                        if(v.isObject()){
                                            JsonObject conditionJson = v.asObject();
                                            String conditionType = conditionJson.getString("type", null);
                                            if(conditionType==null)throw new IllegalArgumentException("Condition type cannot be null!");
                                            if(!conditionType.contains(":"))conditionType = defaultNamespace+":"+conditionType;
                                            Condition condition = null;
                                            for(NamespacedKey key : conditions.keySet()){
                                                if(key.toString().equals(conditionType)){
                                                    condition = conditions.get(key).newInstance();
                                                    condition.loadFromConfig(conditionJson);
                                                }
                                            }
                                            if(condition==null)throw new IllegalArgumentException("Unknown condition: "+conditionType);
                                            provider.conditions.add(condition);
                                        }else throw new IllegalArgumentException("Invalid condition: "+v.getType().getClass().getName());
                                    }
                                }
                                resourceSpawner.spawnProviders.put(provider, weight);
                            }else throw new IllegalArgumentException("Invalid spawn provider: "+val.getType().getClass().getName());
                        }
                    }
                    resourceSpawner.limit = spawner.getInt("limit", resourceSpawner.limit);
                    resourceSpawner.spawnDelay = spawner.getInt("spawn_delay", resourceSpawner.spawnDelay);
                    resourceSpawner.tickInterval = spawner.getInt("tick_interval", resourceSpawner.tickInterval);
                    resourceSpawner.maxTickTime = spawner.getInt("max_tick_time", resourceSpawner.maxTickTime);
                    resourceSpawners.add(resourceSpawner);
                }else throw new IllegalArgumentException("Invalid resource spawner: "+value.getType().getClass().getName());
            }
        }catch(IOException | UnsupportedOperationException ex){
            throw new RuntimeException("Failed to load configuration file", ex);
        }
        load();
        if(ResourceSpawnerCore.debug)System.out.println("Initializing "+resourceSpawners.size()+" spawners...");
        for(ResourceSpawner spawner : resourceSpawners){
            spawner.init(this);
        }
        getLogger().log(Level.INFO, "{0} has been enabled! (Version {1}) by ThizThizzyDizzy", new Object[]{pdfFile.getName(), pdfFile.getVersion()});
    }
    @Override
    public void onDisable(){
        PluginDescriptionFile pdfFile = getDescription();
        save();
        getLogger().log(Level.INFO, "{0} has been disabled! (Version {1}) by ThizThizzyDizzy", new Object[]{pdfFile.getName(), pdfFile.getVersion()});
    }
    @EventHandler
    public void init(ResourceSpawnerInitilizationEvent event){
        event.registerWorldProvider(new NamespacedKey(this, "environment"), new EnvironmentWorldProvider());
        event.registerLocationProvider(new NamespacedKey(this, "square"), new SquareLocationProvider());
        if(getServer().getPluginManager().getPlugin("WorldEdit")!=null)event.registerSpawnProvider(new NamespacedKey(this, "we_schematic"), new WorldEditSchematicSpawnProvider(this));
        event.registerCondition(new NamespacedKey(this, "cube_fill"), new CubeFillCondition());
        event.registerCondition(new NamespacedKey(this, "cube_wg_region"), new CubeWorldGuardRegionCondition());
        event.registerStructureSorter(new NamespacedKey(this, "from_center"), new CenterStructureSorter());
        event.registerStructureSorter(new NamespacedKey(this, "random"), new RandomStructureSorter());
        event.registerTrigger(new NamespacedKey(this, "block_broken"), new BlockBreakTrigger(this));
        event.registerTrigger(new NamespacedKey(this, "timer"), new TimerTrigger(this));
    }
    @EventHandler
    public void onSave(WorldSaveEvent event){
        save();
    }
    public void save(){
        JsonObject json = new JsonObject();
        JsonArray spawners = new JsonArray();
        json.set("spawners", spawners);
        for(ResourceSpawner spawner : resourceSpawners){
            JsonObject spawnerJson = new JsonObject();
            spawners.add(spawnerJson);
            spawnerJson.set("name", spawner.name);
            JsonArray structures = new JsonArray();
            spawnerJson.set("structures", structures);
            for(SpawnedStructure structure : spawner.structures){
                structures.add(structure.save(this, new JsonObject()));
            }
            spawnerJson.set("spawn_timer", spawner.spawnTimer);
        }
        File temp = new File(getDataFolder(), "data_do_not_touch.json.temp");
        if(temp.exists())temp.delete();
        try{
            temp.createNewFile();
            try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp)))){
                json.writeTo(writer, Stringify.PLAIN);
            }
        }catch(IOException ex){
            throw new RuntimeException("Failed to save data!", ex);
        }
        File realFile = new File(getDataFolder(), "data_do_not_touch.json");
        if(realFile.exists())realFile.delete();
        temp.renameTo(realFile);
    }
    private void load(){
        File file = new File(getDataFolder(), "data_do_not_touch.json");
        if(!file.exists())return;//nothing to load
        try{
            JsonObject json = JsonValue.readHjson(new InputStreamReader(new FileInputStream(file))).asObject();
            for(JsonValue v : json.get("spawners").asArray()){
                JsonObject spawnerJson = v.asObject();
                ResourceSpawner spawner = null;
                for(ResourceSpawner r : resourceSpawners){
                    if(r.name.equals(spawnerJson.get("name").asString()))spawner = r;
                }
                spawner.spawnTimer = spawnerJson.get("spawn_timer").asInt();
                JsonArray structures = spawnerJson.get("structures").asArray();
                for(JsonValue val : structures){
                    SpawnedStructure structure = SpawnedStructure.load(this, val.asObject());
                    spawner.structures.add(structure);
                }
            }
        }catch(IOException ex){
            throw new RuntimeException("Failed to load data!", ex);
        }
    }
}