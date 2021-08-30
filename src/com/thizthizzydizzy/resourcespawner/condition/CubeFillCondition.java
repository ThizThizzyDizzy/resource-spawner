package com.thizthizzydizzy.resourcespawner.condition;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.Task;
import com.thizthizzydizzy.resourcespawner.Vanillify;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
public class CubeFillCondition implements Condition{
    private int radius;
    private HashSet<Material> blocks = new HashSet<>();
    private Double minPercent, maxPercent;
    private Integer min, max;
    @Override
    public Condition newInstance(){
        return new CubeFillCondition();
    }
    @Override
    public void loadFromConfig(ResourceSpawnerCore plugin, JsonObject json){
        radius = json.getInt("radius", 0);//default only checks one block
        JsonValue value = json.get("blocks");
        if(value.isArray()){
            for(JsonValue val : value.asArray()){
                if(val.isString()){
                    String block = val.asString();
                    blocks.addAll(Vanillify.getBlocks(block));
                }else throw new IllegalArgumentException("Block must be a String! "+val.getClass().getName());
            }
        }else throw new IllegalArgumentException("blocks must be an array!");
        JsonValue minValue = json.get("min");
        if(minValue!=null)min = minValue.asInt();
        JsonValue maxValue = json.get("max");
        if(maxValue!=null)max = maxValue.asInt();
        JsonValue minPercentValue = json.get("min_percent");
        if(minPercentValue!=null)minPercent = minPercentValue.asDouble()/100;
        JsonValue maxPercentValue = json.get("max_percent");
        if(maxPercentValue!=null)maxPercent = maxPercentValue.asDouble()/100;
    }
    @Override
    public Task<Boolean> check(World world, Location location){
        if(ResourceSpawnerCore.debug)System.out.println("CubeFill check");
        int minX = location.getBlockX()-radius;
        int minY = Math.max(0, location.getBlockY()-radius);//TODO 1.17 min world height!
        int minZ = location.getBlockZ()-radius;
        int maxX = location.getBlockX()+radius;
        int maxY = Math.min(world.getMaxHeight(), location.getBlockY()+radius);
        int maxZ = location.getBlockZ()+radius;
        int volume = (maxX-minX+1)*(maxY-minY+1)*(maxZ-minZ+1);
        return new Task<Boolean>() {
            private int x = minX-1, y = minY, z = minZ;
            private Boolean result = null;
            int numFound = 0;
            @Override
            public void step(){
                x++;
                if(x>maxX){
                    x = minX;
                    y++;
                    if(y>maxY){
                        y = minY;
                        z++;
                        if(z>maxZ){
                            result = true;
                            double percent = numFound/(double)volume;
                            if(min!=null&&numFound<min){
                                result = false;
                                if(ResourceSpawnerCore.debug)System.out.println("CubeFill fail: "+numFound+"/"+volume+" ("+percent+")");
                            }
                            if(minPercent!=null&&percent<minPercent){
                                result = false;
                                if(ResourceSpawnerCore.debug)System.out.println("CubeFill fail: "+numFound+"/"+volume+" ("+percent+")");
                            }
                            if(result!=null&&result&&ResourceSpawnerCore.debug)System.out.println("CubeFill pass: "+numFound+"/"+volume+" ("+percent+")");
                            return;
                        }
                    }
                }
                if(blocks.contains(world.getBlockAt(x, y, z).getType()))numFound++;
                double percent = numFound/(double)volume;
                if(max!=null&&numFound>max){
                    result = false;
                    if(ResourceSpawnerCore.debug)System.out.println("CubeFill fail: "+numFound+"/"+volume+" ("+percent+")");
                }
                if(maxPercent!=null&&percent>maxPercent){
                    result = false;
                    if(ResourceSpawnerCore.debug)System.out.println("CubeFill fail: "+numFound+"/"+volume+" ("+percent+")");
                }
                if(max==null&&maxPercent==null){
                    boolean metMin = min==null||numFound>=min;
                    boolean metMinPercent = minPercent==null||percent>=minPercent;
                    if(metMin&&metMinPercent){
                        result = true;//met minimum, no maximum
                        if(ResourceSpawnerCore.debug)System.out.println("CubeFill pass: "+numFound+"/"+volume+" ("+percent+")");
                    }
                }
            }
            @Override
            public boolean isFinished(){
                return result!=null;
            }
            @Override
            public Boolean getResult(){
                return result;
            }
        };
    }
}