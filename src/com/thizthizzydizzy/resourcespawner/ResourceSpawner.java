package com.thizthizzydizzy.resourcespawner;
import com.thizthizzydizzy.resourcespawner.provider.LocationProvider;
import com.thizthizzydizzy.resourcespawner.provider.SpawnProvider;
import com.thizthizzydizzy.resourcespawner.provider.WorldProvider;
import java.util.HashMap;
import java.util.Random;
import org.bukkit.Location;
public class ResourceSpawner{
    public final String name;
    public final HashMap<WorldProvider, Integer> worldProviders = new HashMap<>();
    public final HashMap<LocationProvider, Integer> locationProviders = new HashMap<>();
    public final HashMap<SpawnProvider, Integer> spawnProviders = new HashMap<>();
    public int limit = 1;//defaults to 1 to avoid runaway generation
    public int spawnDelay = 0;
    public int maxTickTime = 5;//defaults to 5 to avoid freezing the server
    public ResourceSpawner(String name){
        this.name = name;
    }
    public Location getRandomLocation(Random rand){
        return chooseWeighted(locationProviders, rand).get(chooseWeighted(worldProviders, rand).get(rand), rand);
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
}