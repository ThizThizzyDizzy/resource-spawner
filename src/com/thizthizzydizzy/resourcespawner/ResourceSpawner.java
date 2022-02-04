package com.thizthizzydizzy.resourcespawner;
import com.thizthizzydizzy.resourcespawner.condition.Condition;
import com.thizthizzydizzy.resourcespawner.provider.LocationProvider;
import com.thizthizzydizzy.resourcespawner.provider.SpawnProvider;
import com.thizthizzydizzy.resourcespawner.provider.WorldProvider;
import com.thizthizzydizzy.resourcespawner.trigger.TriggerHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
public class ResourceSpawner implements TriggerHandler{
    public final String name;
    public final HashMap<WorldProvider, Integer> worldProviders = new HashMap<>();
    public final HashMap<LocationProvider, Integer> locationProviders = new HashMap<>();
    public final HashMap<SpawnProvider, Integer> spawnProviders = new HashMap<>();
    public int limit = 1;//defaults to 1 to avoid runaway generation
    public int spawnDelay = 0;
    public int tickInterval = 1;//defaults to ticking every tick for maximum accuracy
    public long maxTickTime = 5_000_000;//defaults to 5 to avoid freezing the server
    //active stuff
    public int spawnTimer = 0;
    public ArrayList<Task> tasks = new ArrayList<>();
    public ArrayList<SpawnedStructure> structures = new ArrayList<>();
    public Task spawnTask;//not actually used; just holds the task so it doesn't try spawning multiple things at once
    public BukkitTask taskProcessor;
    private Random rand = new Random();
    public ResourceSpawner(String name){
        this.name = name;
    }
    public World getRandomWorld(Random rand){
        return chooseWeighted(worldProviders, rand).get(rand);
    }
    public Location getRandomLocation(World world, Random rand){
        return chooseWeighted(locationProviders, rand).get(world, rand);
    }
    public <T> T chooseWeighted(HashMap<T, Integer> items, Random rand){
        if(items.isEmpty())return null;
        int completeWeight = 0;
        for (T item : items.keySet()){
            completeWeight+=items.get(item);;
        }
        int r = rand.nextInt(completeWeight)+1;//Math.random()*completeWeight;
        double countWeight = 0;
        for(T item : items.keySet()){
            countWeight+=items.get(item);
            if(countWeight>=r)return item;
        }
        return null;
    }
    public void init(ResourceSpawnerCore plugin){
        if(ResourceSpawnerCore.debug)System.out.println("Resource Spawner "+name+" Initialized!");
        new BukkitRunnable() {
            @Override
            public void run(){
                if(ResourceSpawnerCore.debug)System.out.println("Resource Spawner "+name+" Ticking");
                spawnTimer-=tickInterval;
                if(spawnTimer<=0){
                    if(ResourceSpawnerCore.debug)System.out.println("Spawn timer hit!");
                    spawnTimer = spawnDelay;
                    if(spawnTask==null){
                        if(ResourceSpawnerCore.debug)System.out.println("No current spawn; "+structures.size()+"/"+limit);
                        if(limit==-1||structures.size()<limit)startSpawn(plugin);
                    }
                }
                for(SpawnedStructure s : structures){
                    if(s.decayTimer==Integer.MIN_VALUE)continue;//doesn't decay
                    if(s.decayTask!=null)continue;
                    s.decayTimer-=tickInterval;
                    if(s.decayTimer<=0){
                        s.decayTask = s.decay();
                        tasks.add(s.decayTask);
                    }
                }
                if(taskProcessor==null&&!tasks.isEmpty()){
                    Task task = tasks.remove(0);
                    if(ResourceSpawnerCore.debug)System.out.println("Starting task processor");
                    taskProcessor = new BukkitRunnable() {
                        @Override
                        public void run(){
                            if(ResourceSpawnerCore.debug)System.out.println("Task processor started");
                            long startTime = System.nanoTime();
                            while(!task.isFinished()){
                                task.step();
                                long totalNanos = System.nanoTime()-startTime;
                                if(totalNanos>maxTickTime)return;
                            }
                            Object o = task.getResult();
                            if(o instanceof SpawnedStructure){
                                SpawnedStructure s = (SpawnedStructure)o;
                                if(task==spawnTask){
                                    structures.add(s);
                                }else{
                                    structures.remove(s);
                                }
                            }
                            if(task==spawnTask)spawnTask = null;
                            taskProcessor = null;
                            if(ResourceSpawnerCore.debug)System.out.println("Task processor finished");
                            cancel();
                        }
                    }.runTaskTimer(plugin, 0, 1);
                }
            }
        }.runTaskTimer(plugin, 0, tickInterval);
    }
    private void startSpawn(ResourceSpawnerCore plugin){
        if(ResourceSpawnerCore.debug)System.out.println("Preparing to spawn structure...");
        if(worldProviders.isEmpty()){
            if(ResourceSpawnerCore.debug)System.out.println("No world providers");
            return;
        }
        World world = getRandomWorld(rand);
        if(world==null){
            if(ResourceSpawnerCore.debug)System.out.println("No world provided");
            return;
        }
        if(ResourceSpawnerCore.debug)System.out.println("World: "+world.getName()+" ("+world.getUID()+")");
        if(locationProviders.isEmpty()){
            if(ResourceSpawnerCore.debug)System.out.println("No location providers");
            return;
        }
        Location loc = getRandomLocation(world, rand);
        if(loc==null){
            if(ResourceSpawnerCore.debug)System.out.println("No location provided");
            return;
        }
        if(ResourceSpawnerCore.debug)System.out.println("Location: "+loc.toString());
        if(spawnProviders.isEmpty()){
            if(ResourceSpawnerCore.debug)System.out.println("No spawn providers");
            return;
        }
        SpawnProvider spawnProvider = chooseWeighted(spawnProviders, rand);
        if(ResourceSpawnerCore.debug)System.out.println("SpawnProvider: "+spawnProvider.getClass().getName());
        if(ResourceSpawnerCore.debug)System.out.println("Creating spawn task...");
        spawnTask = new Task(){
            private ArrayList<Condition> conditions = new ArrayList<>(spawnProvider.conditions);
            private Task<Boolean> conditionTask = null;
            private boolean failed = false;
            Object result = null;
            private Task spawnTask = null;
            @Override
            public void step(){
                if(conditionTask!=null){
                    if(!conditionTask.isFinished()){
                        conditionTask.step();
                        return;
                    }else{
                        if(!conditionTask.getResult()){
                            failed = true;
                            if(ResourceSpawnerCore.debug)System.out.println("Condition Failed");
                            return;
                        }
                        if(ResourceSpawnerCore.debug)System.out.println("Condition passed");
                        conditionTask = null;
                    }
                }
                if(!conditions.isEmpty()){
                    if(ResourceSpawnerCore.debug)System.out.println("Checking Condition...");
                    conditionTask = conditions.remove(0).check(world, loc);
                    return;
                }
                //All conditions have been met; begin spawning
                if(spawnTask==null){
                    if(ResourceSpawnerCore.debug)System.out.println("Spawn conditions passed; spawning...");
                    spawnTask = spawnProvider.spawn(plugin, world, loc);
                }
                spawnTask.step();
                if(spawnTask.isFinished()){
                    result = spawnTask.getResult();
                    if(ResourceSpawnerCore.debug)System.out.println("Spawned!");
                }
            }
            @Override
            public boolean isFinished(){
                return failed||result!=null;
            }
            @Override
            public Object getResult(){
                return failed?null:result;
            }
        };
        tasks.add(spawnTask);
    }
    @Override
    public void addTask(Task task){
        tasks.add(task);
    }
}