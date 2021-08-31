package com.thizthizzydizzy.resourcespawner;
import com.thizthizzydizzy.resourcespawner.condition.BiomeCondition;
import com.thizthizzydizzy.resourcespawner.condition.BlockCondition;
import com.thizthizzydizzy.resourcespawner.condition.Condition;
import com.thizthizzydizzy.resourcespawner.condition.CubeFillCondition;
import com.thizthizzydizzy.resourcespawner.condition.CubeWorldGuardRegionCondition;
import com.thizthizzydizzy.resourcespawner.condition.EntityProximityCondition;
import com.thizthizzydizzy.resourcespawner.condition.MoonPhaseCondition;
import com.thizthizzydizzy.resourcespawner.condition.WorldTimeCondition;
import com.thizthizzydizzy.resourcespawner.distribution.Distribution;
import com.thizthizzydizzy.resourcespawner.distribution.EvenDistribution;
import com.thizthizzydizzy.resourcespawner.distribution.GaussianDistribution;
import com.thizthizzydizzy.resourcespawner.provider.LocationProvider;
import com.thizthizzydizzy.resourcespawner.provider.SpawnProvider;
import com.thizthizzydizzy.resourcespawner.provider.WorldProvider;
import com.thizthizzydizzy.resourcespawner.provider.location.BlockLocationProvider;
import com.thizthizzydizzy.resourcespawner.provider.location.CircleLocationProvider;
import com.thizthizzydizzy.resourcespawner.provider.location.CuboidLocationProvider;
import com.thizthizzydizzy.resourcespawner.provider.location.SquareLocationProvider;
import com.thizthizzydizzy.resourcespawner.provider.location.SurfaceLocationProvider;
import com.thizthizzydizzy.resourcespawner.provider.spawn.EntitySpawnProvider;
import com.thizthizzydizzy.resourcespawner.provider.spawn.WorldEditSchematicSpawnProvider;
import com.thizthizzydizzy.resourcespawner.provider.world.EnvironmentWorldProvider;
import com.thizthizzydizzy.resourcespawner.provider.world.NameWorldProvider;
import com.thizthizzydizzy.resourcespawner.provider.world.UUIDWorldProvider;
import com.thizthizzydizzy.resourcespawner.sorter.CenterStructureSorter;
import com.thizthizzydizzy.resourcespawner.sorter.InvertedCenterStructureSorter;
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
    public final HashMap<NamespacedKey, Distribution> distributions = new HashMap<>();
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
        if(worldProviders.containsKey(key)){
            getLogger().log(Level.WARNING, "World provider {0} already exists! Skipping...", key.toString());
            return false;
        }
        if(debug)getLogger().log(Level.INFO, "Registered World Provider {0} as {1}", new Object[]{key, provider.getClass().getName()});
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
        if(locationProviders.containsKey(key)){
            getLogger().log(Level.WARNING, "Location provider {0} already exists! Skipping...", key.toString());
            return false;
        }
        locationProviders.put(key, provider);
        if(debug)getLogger().log(Level.INFO, "Registered Location Provider {0} as {1}", new Object[]{key, provider.getClass().getName()});
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
        if(spawnProviders.containsKey(key)){
            getLogger().log(Level.WARNING, "Spawn provider {0} already exists! Skipping...", key.toString());
            return false;
        }
        spawnProviders.put(key, provider);
        if(debug)getLogger().log(Level.INFO, "Registered Spawn Provider {0} as {1}", new Object[]{key, provider.getClass().getName()});
        return true;
    }
    /**
     * Register a new condition
     * @param key the condition's unique key
     * @param condition the condition to register
     * @return true if the condition is successfully registered, false otherwise
     * @throws IllegalArgumentException if key is null
     */
    public boolean registerCondition(NamespacedKey key, Condition condition) throws IllegalArgumentException{
        if(key==null)throw new IllegalArgumentException("Key must not be null!");
        if(conditions.containsKey(key)){
            getLogger().log(Level.WARNING, "Condition {0} already exists! Skipping...", key.toString());
            return false;
        }
        conditions.put(key, condition);
        if(debug)getLogger().log(Level.INFO, "Registered Condition {0} as {1}", new Object[]{key, condition.getClass().getName()});
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
        if(structureSorters.containsKey(key)){
            getLogger().log(Level.WARNING, "Structure sorter {0} already exists! Skipping...", key.toString());
            return false;
        }
        structureSorters.put(key, sorter);
        if(debug)getLogger().log(Level.INFO, "Registered Structure Sorter {0} as {1}", new Object[]{key, sorter.getClass().getName()});
        return true;
    }
    /**
     * Register a new trigger
     * @param key the trigger's unique key
     * @param trigger the trigger to register
     * @return true if the trigger is successfully registered, false otherwise
     * @throws IllegalArgumentException if key is null
     */
    public boolean registerTrigger(NamespacedKey key, Trigger trigger) throws IllegalArgumentException{
        if(key==null)throw new IllegalArgumentException("Key must not be null!");
        if(triggers.containsKey(key)){
            getLogger().log(Level.WARNING, "Trigger {0} already exists! Skipping...", key.toString());
            return false;
        }
        triggers.put(key, trigger);
        if(debug)getLogger().log(Level.INFO, "Registered Trigger {0} as {1}", new Object[]{key, trigger.getClass().getName()});
        return true;
    }
    /**
     * Register a new random distribution
     * @param key the distribution's unique key
     * @param distribution the distribution to register
     * @return true if the distribution is successfully registered, false otherwise
     * @throws IllegalArgumentException if key is null
     */
    public boolean registerDistribution(NamespacedKey key, Distribution distribution) throws IllegalArgumentException{
        if(key==null)throw new IllegalArgumentException("Key must not be null!");
        if(distributions.containsKey(key)){
            getLogger().log(Level.WARNING, "Distribution {0} already exists! Skipping...", key.toString());
            return false;
        }
        distributions.put(key, distribution);
        if(debug)getLogger().log(Level.INFO, "Registered Distribution {0} as {1}", new Object[]{key, distribution.getClass().getName()});
        return true;
    }
    @Override
    public void onEnable(){
        if(debug)getLogger().log(Level.INFO, "Starting up...");
        PluginManager pm = getServer().getPluginManager();
        PluginDescriptionFile pdfFile = getDescription();
        if(debug)getLogger().log(Level.INFO, "Registering self-initialization event");
        pm.registerEvents(this, this);
        if(debug)getLogger().log(Level.INFO, "Calling initializiation event");
        Bukkit.getServer().getPluginManager().callEvent(new ResourceSpawnerInitilizationEvent(this));
        if(debug)getLogger().log(Level.INFO, "Initialization complete. Loading config...");
        File configFile = new File(getDataFolder(), "config.hjson");
        if(!getDataFolder().exists()){
            getDataFolder().mkdirs();
            if(debug)getLogger().log(Level.INFO, "Generating data folder");
        }
        if(!configFile.exists()){
            try{
                if(debug)getLogger().log(Level.INFO, "Generating empty configuration file");
                configFile.createNewFile();
                try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFile)))){
                    writer.write("{\n" +
                                 "    debug: false\n" +
                                 "    resource_spawners: [\n" +
                                 "    ]\n" +
                                 "}");
                }
            }catch(IOException ex){
                getLogger().log(Level.SEVERE, "Failed to generate configuration file", ex);
            }
        }
        try{
            if(debug)getLogger().log(Level.INFO, "Reading config...");
            JsonObject json = JsonValue.readHjson(new InputStreamReader(new FileInputStream(configFile))).asObject();
            debug = json.getBoolean("debug", false);
            if(debug)getLogger().log(Level.INFO, "Reading resource spawners...");
            JsonArray spawners = json.get("resource_spawners").asArray();
            for(JsonValue value : spawners){
                if(value.isObject()){
                    JsonObject spawner = value.asObject();
                    String name = spawner.getString("name", null);
                    if(debug)getLogger().log(Level.INFO, "Reading resource spawner {0}", name);
                    if(name==null)throw new IllegalArgumentException("Spawner name cannot be null!");
                    for(ResourceSpawner rs : resourceSpawners){
                        if(rs.name.equals(name))throw new IllegalArgumentException("Resource spawner "+name+" already exists! Each resource spawner must have a unique name");
                    }
                    ResourceSpawner resourceSpawner = new ResourceSpawner(name);
                    if(debug)getLogger().log(Level.INFO, "Reading world providers...");
                    JsonValue world_providers = spawner.get("world_providers");
                    if(world_providers==null)getLogger().log(Level.WARNING, "Resource spawner {0} does not have any world providers!", name);
                    else{
                        for(JsonValue val : world_providers.asArray()){
                            if(val.isObject()){
                                JsonObject worldProvider = val.asObject();
                                String type = worldProvider.getString("type", null);
                                if(debug)getLogger().log(Level.INFO, "Reading world provider: {0}", type);
                                WorldProvider provider = getWorldProvider(type);
                                if(provider==null)throw new IllegalArgumentException("Unknown world provider: "+type);
                                provider.loadFromConfig(this, worldProvider);
                                int weight = worldProvider.getInt("weight", 1);
                                if(debug)getLogger().log(Level.INFO, "Weight: {0}", weight);
                                resourceSpawner.worldProviders.put(provider, weight);
                            }else throw new IllegalArgumentException("Invalid world provider: "+val.getType().getClass().getName());
                        }
                    }
                    if(debug)getLogger().log(Level.INFO, "Reading location providers");
                    JsonValue location_providers = spawner.get("location_providers");
                    if(location_providers==null)getLogger().log(Level.WARNING, "Resource spawner {0} does not have any location providers!", name);
                    else{
                        for(JsonValue val : location_providers.asArray()){
                            if(val.isObject()){
                                JsonObject locationProvider = val.asObject();
                                String type = locationProvider.getString("type", null);
                                if(debug)getLogger().log(Level.INFO, "Reading location provider {0}", type);
                                LocationProvider provider = getLocationProvider(type);
                                if(provider==null)throw new IllegalArgumentException("Unknown location provider: "+type);
                                provider.loadFromConfig(this, locationProvider);
                                int weight = locationProvider.getInt("weight", 1);
                                if(debug)getLogger().log(Level.INFO, "Weight: {0}", weight);
                                resourceSpawner.locationProviders.put(provider, weight);
                            }else throw new IllegalArgumentException("Invalid location provider: "+val.getType().getClass().getName());
                        }
                    }
                    if(debug)getLogger().log(Level.INFO, "Reading spawn providers");
                    JsonValue spawns = spawner.get("spawn_providers");
                    if(spawns==null)getLogger().log(Level.WARNING, "Resource spawner {0} does not have any spawn providers!", name);
                    else{
                        for(JsonValue val : spawns.asArray()){
                            if(val.isObject()){
                                JsonObject spawn = val.asObject();
                                String type = spawn.getString("type", null);
                                if(debug)getLogger().log(Level.INFO, "Reading spawn provider {0}", type);
                                SpawnProvider provider = getSpawnProvider(type);
                                if(provider==null)throw new IllegalArgumentException("Unknown spawn provider: "+type);
                                provider.loadFromConfig(this, spawn);
                                int weight = spawn.getInt("weight", 1);
                                if(debug)getLogger().log(Level.INFO, "Weight: {0}", weight);
                                if(debug)getLogger().log(Level.INFO, "Reading conditions");
                                JsonValue conditionsJson = spawn.get("conditions");
                                if(conditionsJson!=null){
                                    for(JsonValue v : conditionsJson.asArray()){
                                        if(v.isObject()){
                                            JsonObject conditionJson = v.asObject();
                                            String conditionType = conditionJson.getString("type", null);
                                            if(debug)getLogger().log(Level.INFO, "Reading condition {0}", conditionType);
                                            Condition condition = getCondition(conditionType);
                                            if(condition==null)throw new IllegalArgumentException("Unknown condition: "+conditionType);
                                            condition.loadFromConfig(this, conditionJson);
                                            provider.conditions.add(condition);
                                        }else throw new IllegalArgumentException("Invalid condition: "+v.getType().getClass().getName());
                                    }
                                }
                                resourceSpawner.spawnProviders.put(provider, weight);
                            }else throw new IllegalArgumentException("Invalid spawn provider: "+val.getType().getClass().getName());
                        }
                    }
                    resourceSpawner.limit = spawner.getInt("limit", resourceSpawner.limit);
                    if(debug)getLogger().log(Level.INFO, "Limit: {0}", resourceSpawner.limit);
                    resourceSpawner.spawnDelay = spawner.getInt("spawn_delay", resourceSpawner.spawnDelay);
                    if(debug)getLogger().log(Level.INFO, "Spawn Delay: {0}", resourceSpawner.spawnDelay);
                    resourceSpawner.tickInterval = spawner.getInt("tick_interval", resourceSpawner.tickInterval);
                    if(debug)getLogger().log(Level.INFO, "Tick Interval: {0}", resourceSpawner.tickInterval);
                    resourceSpawner.maxTickTime = spawner.getLong("max_tick_time", resourceSpawner.maxTickTime);
                    if(debug)getLogger().log(Level.INFO, "Max Tick Time: {0}", resourceSpawner.maxTickTime);
                    resourceSpawners.add(resourceSpawner);
                }else throw new IllegalArgumentException("Invalid resource spawner: "+value.getType().getClass().getName());
            }
        }catch(IOException | UnsupportedOperationException ex){
            throw new RuntimeException("Failed to load configuration file", ex);
        }
        if(debug)getLogger().log(Level.INFO, "Loading data...");
        load();
        if(debug)getLogger().log(Level.INFO, "Loaded!");
        if(debug)System.out.println("Initializing "+resourceSpawners.size()+" spawners...");
        for(ResourceSpawner spawner : resourceSpawners){
            if(debug)getLogger().log(Level.INFO, "Initializing spawner {0}", spawner.name);
            spawner.init(this);
        }
        if(debug)getLogger().log(Level.INFO, "Startup complete!");
        getLogger().log(Level.INFO, "{0} has been enabled! (Version {1}) by ThizThizzyDizzy", new Object[]{pdfFile.getName(), pdfFile.getVersion()});
    }
    @Override
    public void onDisable(){
        PluginDescriptionFile pdfFile = getDescription();
        save();
        if(debug)getLogger().log(Level.INFO, "Shutting down");
        getLogger().log(Level.INFO, "{0} has been disabled! (Version {1}) by ThizThizzyDizzy", new Object[]{pdfFile.getName(), pdfFile.getVersion()});
    }
    @EventHandler
    public void init(ResourceSpawnerInitilizationEvent event){
        event.registerWorldProvider(new NamespacedKey(this, "environment"), new EnvironmentWorldProvider());
        event.registerWorldProvider(new NamespacedKey(this, "uuid"), new UUIDWorldProvider());
        event.registerWorldProvider(new NamespacedKey(this, "name"), new NameWorldProvider());
        event.registerLocationProvider(new NamespacedKey(this, "square"), new SquareLocationProvider());
        event.registerLocationProvider(new NamespacedKey(this, "cuboid"), new CuboidLocationProvider());
        event.registerLocationProvider(new NamespacedKey(this, "block"), new BlockLocationProvider());
        event.registerLocationProvider(new NamespacedKey(this, "surface"), new SurfaceLocationProvider());
        event.registerLocationProvider(new NamespacedKey(this, "circle"), new CircleLocationProvider());
        if(getServer().getPluginManager().getPlugin("WorldEdit")!=null)event.registerSpawnProvider(new NamespacedKey(this, "we_schematic"), new WorldEditSchematicSpawnProvider());
        event.registerSpawnProvider(new NamespacedKey(this, "entity"), new EntitySpawnProvider());
        event.registerCondition(new NamespacedKey(this, "cube_fill"), new CubeFillCondition());
        if(getServer().getPluginManager().getPlugin("WorldGuard")!=null)event.registerCondition(new NamespacedKey(this, "cube_wg_region"), new CubeWorldGuardRegionCondition());
        event.registerCondition(new NamespacedKey(this, "entity_proximity"), new EntityProximityCondition());
        event.registerCondition(new NamespacedKey(this, "world_time"), new WorldTimeCondition());
        event.registerCondition(new NamespacedKey(this, "moon_phase"), new MoonPhaseCondition());
        event.registerCondition(new NamespacedKey(this, "block"), new BlockCondition());
        event.registerCondition(new NamespacedKey(this, "biome"), new BiomeCondition());
        event.registerStructureSorter(new NamespacedKey(this, "from_center"), new CenterStructureSorter());
        event.registerStructureSorter(new NamespacedKey(this, "to_center"), new InvertedCenterStructureSorter());
        event.registerStructureSorter(new NamespacedKey(this, "random"), new RandomStructureSorter());
        event.registerTrigger(new NamespacedKey(this, "block_broken"), new BlockBreakTrigger());
        event.registerTrigger(new NamespacedKey(this, "timer"), new TimerTrigger());
        event.registerDistribution(new NamespacedKey(this, "even"), new EvenDistribution());
        event.registerDistribution(new NamespacedKey(this, "gaussian"), new GaussianDistribution());
    }
    @EventHandler
    public void onSave(WorldSaveEvent event){
        
        save();
    }
    public void save(){
        if(debug)getLogger().log(Level.INFO, "Saving...");
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
        if(debug)getLogger().log(Level.INFO, "Saved!");
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
    public WorldProvider getWorldProvider(String key){
        if(!key.contains(":"))key = defaultNamespace+":"+key;
        for(NamespacedKey k : worldProviders.keySet()){
            if(k.toString().equals(key))return worldProviders.get(k).newInstance();
        }
        return null;
    }
    public LocationProvider getLocationProvider(String key){
        if(!key.contains(":"))key = defaultNamespace+":"+key;
        for(NamespacedKey k : locationProviders.keySet()){
            if(k.toString().equals(key))return locationProviders.get(k).newInstance();
        }
        return null;
    }
    public SpawnProvider getSpawnProvider(String key){
        if(!key.contains(":"))key = defaultNamespace+":"+key;
        for(NamespacedKey k : spawnProviders.keySet()){
            if(k.toString().equals(key))return spawnProviders.get(k).newInstance();
        }
        return null;
    }
    public Condition getCondition(String key){
        if(!key.contains(":"))key = defaultNamespace+":"+key;
        for(NamespacedKey k : conditions.keySet()){
            if(k.toString().equals(key))return conditions.get(k).newInstance();
        }
        return null;
    }
    public StructureSorter getStructureSorter(String key){
        if(!key.contains(":"))key = defaultNamespace+":"+key;
        for(NamespacedKey k : structureSorters.keySet()){
            if(k.toString().equals(key))return structureSorters.get(k);
        }
        return null;
    }
    public Trigger getTrigger(String key){
        if(!key.contains(":"))key = defaultNamespace+":"+key;
        for(NamespacedKey k : triggers.keySet()){
            if(k.toString().equals(key))return triggers.get(k).newInstance();
        }
        return null;
    }
    public Distribution getDistribution(String key){
        if(!key.contains(":"))key = defaultNamespace+":"+key;
        for(NamespacedKey k : distributions.keySet()){
            if(k.toString().equals(key))return distributions.get(k);
        }
        return null;
    }
}