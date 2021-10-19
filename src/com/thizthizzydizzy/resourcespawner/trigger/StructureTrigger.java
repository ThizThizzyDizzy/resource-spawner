package com.thizthizzydizzy.resourcespawner.trigger;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.SpawnedStructure;
public abstract class StructureTrigger extends Trigger{
    public StructureTrigger(TriggerHandler handler){
        super(handler);
    }
    public abstract StructureTrigger newInstance(ResourceSpawnerCore plugin, SpawnedStructure structure, TriggerHandler handler);
    public abstract void dissolve();
}