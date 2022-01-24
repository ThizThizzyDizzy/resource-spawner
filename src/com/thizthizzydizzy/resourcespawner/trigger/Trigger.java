package com.thizthizzydizzy.resourcespawner.trigger;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.Task;
import com.thizthizzydizzy.resourcespawner.condition.Condition;
import java.util.ArrayList;
import java.util.Objects;
import org.hjson.JsonObject;
public abstract class Trigger{
    public ArrayList<Condition> conditions = new ArrayList<>();
    private ArrayList<TriggerListener> listeners = new ArrayList<>();
    private final TriggerHandler handler;
    public Trigger(TriggerHandler handler){
        this.handler = handler;
    }
    public abstract Trigger newInstance(TriggerHandler handler);
    public abstract void loadFromConfig(ResourceSpawnerCore plugin, JsonObject obj);
    public void trigger(){
        if(ResourceSpawnerCore.debug)System.out.println("Trigger triggered: "+getClass().getName());
        LISTENER:for(TriggerListener listener : listeners){
            ArrayList<Task<Boolean>> subtasks = new ArrayList<>();
            for(Condition condition : conditions){
                subtasks.add(condition.check(listener.getWorld(), listener.getLocation()));
            }
            handler.addTask(new Task(){
                boolean finished = false;
                @Override
                public void step(){
                    if(!subtasks.isEmpty()){
                        Task<Boolean> subtask = subtasks.get(0);
                        if(subtask.isFinished()){
                            subtasks.remove(0);
                            boolean pass = Objects.equals(subtask.getResult(), Boolean.TRUE);
                            if(ResourceSpawnerCore.debug)System.out.println("Condition "+(pass?"passed":"failed"));
                            if(!pass)finished = true;
                        }
                        else subtask.step();
                        return;
                    }
                    if(ResourceSpawnerCore.debug)System.out.println("Triggering");
                    listener.trigger();
                    finished = true;
                }
                @Override
                public boolean isFinished(){
                    return finished;
                }
                @Override
                public Object getResult(){
                    return null;
                }
            });
        }
    }
    public void addTriggerListener(TriggerListener listener){
        listeners.add(listener);
    }
    public void removeTriggerListener(TriggerListener listener){
        listeners.remove(listener);
    }
}