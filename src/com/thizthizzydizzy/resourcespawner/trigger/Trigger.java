package com.thizthizzydizzy.resourcespawner.trigger;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.condition.Condition;
import java.util.ArrayList;
import org.hjson.JsonObject;
public abstract class Trigger{
    public int interval;
    public ArrayList<Condition> conditions = new ArrayList<>();
    private ArrayList<TriggerListener> listeners = new ArrayList<>();
    public abstract Trigger newInstance();
    public abstract void loadFromConfig(JsonObject obj);
    public void trigger(){
        for(TriggerListener listener : listeners)listener.trigger();
    }
    public void addTriggerListener(TriggerListener listener){
        listeners.add(listener);
    }
}