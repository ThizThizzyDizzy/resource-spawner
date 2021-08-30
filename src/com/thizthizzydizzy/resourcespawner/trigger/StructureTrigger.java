package com.thizthizzydizzy.resourcespawner.trigger;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.SpawnedStructure;
public abstract class StructureTrigger extends Trigger{
    public abstract StructureTrigger newInstance(ResourceSpawnerCore plugin, SpawnedStructure structure);
    public abstract void dissolve();
}