package com.thizthizzydizzy.resourcespawner;
import com.thizthizzydizzy.resourcespawner.condition.Condition;
import com.thizthizzydizzy.resourcespawner.provider.LocationProvider;
import com.thizthizzydizzy.resourcespawner.provider.SpawnProvider;
import com.thizthizzydizzy.resourcespawner.provider.WorldProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
public class ResourceSpawner{
    public final String name;
    public final HashMap<WorldProvider, Integer> worldProviders = new HashMap<>();
    public final HashMap<LocationProvider, Integer> locationProviders = new HashMap<>();
    public final HashMap<SpawnProvider, Integer> spawnProviders = new HashMap<>();
    public int limit = 1;//defaults to 1 to avoid runaway generation
    public int spawnDelay = 0;
    public int tickInterval = 1;//defaults to ticking every tick for maximum accuracy
    public int maxTickTime = 5;//defaults to 5 to avoid freezing the server
    //active stuff
    public int spawnTimer = 0;
    public ArrayList<Task<SpawnedStructure>> tasks = new ArrayList<>();
    public ArrayList<SpawnedStructure> structures = new ArrayList<>();
    public Task<SpawnedStructure> spawnTask;//not actually used; just holds the task so it doesn't try spawning multiple things at once
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
        new BukkitRunnable() {
            @Override
            public void run(){
                spawnTimer-=tickInterval;
                if(spawnTimer<=0){
                    spawnTimer = spawnDelay;//because it's in seconds
                    if(spawnTask!=null&&structures.size()<limit)startSpawn();
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
                    Task<SpawnedStructure> task = tasks.remove(0);
                    taskProcessor = new BukkitRunnable() {
                        @Override
                        public void run(){
                            long startTime = System.nanoTime();
                            while(!task.isFinished()){
                                task.step();
                                long totalNanos = System.nanoTime()-startTime;
                                if(totalNanos>maxTickTime*1_000_000L)return;
                            }
                            SpawnedStructure s = task.getResult();
                            if(task==spawnTask){
                                structures.add(s);
                                spawnTask = null;
                            }else{
                                structures.remove(s);
                            }
                            taskProcessor = null;
                            cancel();
                        }
                    }.runTaskTimer(plugin, 0, 1);
                }
            }
        }.runTaskTimer(plugin, 0, tickInterval);
    }
    private void startSpawn(){
        World world = chooseWeighted(worldProviders, rand).get(rand);
        Location loc = chooseWeighted(locationProviders, rand).get(world, rand);
        SpawnProvider spawnProvider = chooseWeighted(spawnProviders, rand);
        spawnTask = new Task<SpawnedStructure>(){
            private ArrayList<Condition> conditions = new ArrayList<>(spawnProvider.conditions);
            private Task<Boolean> conditionTask = null;
            private boolean failed = false;
            SpawnedStructure result = null;
            private Task<SpawnedStructure> spawnTask = null;
            @Override
            public void step(){
                if(conditionTask!=null){
                    if(!conditionTask.isFinished()){
                        conditionTask.step();
                        return;
                    }else{
                        if(!conditionTask.getResult()){
                            failed = true;
                            return;
                        }
                        conditionTask = null;
                    }
                }
                if(!conditions.isEmpty()){
                    conditionTask = conditions.remove(0).check(world, loc);
                    return;
                }
                //All conditions have been met; begin spawning
                if(spawnTask==null)spawnTask = spawnProvider.spawn(world, loc);
                spawnTask.step();
                if(spawnTask.isFinished())result = spawnTask.getResult();
            }
            @Override
            public boolean isFinished(){
                return failed||result!=null;
            }
            @Override
            public SpawnedStructure getResult(){
                return failed?null:result;
            }
        };
        tasks.add(spawnTask);
    }
}