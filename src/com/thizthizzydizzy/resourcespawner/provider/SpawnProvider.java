package com.thizthizzydizzy.resourcespawner.provider;
import com.thizthizzydizzy.resourcespawner.condition.Condition;
import java.util.ArrayList;
import org.hjson.JsonObject;
public abstract class SpawnProvider{
    public ArrayList<Condition> conditions = new ArrayList<>();
    public abstract SpawnProvider newInstance();
    public abstract void loadFromConfig(JsonObject json);
}
