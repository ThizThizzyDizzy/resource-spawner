package com.thizthizzydizzy.resourcespawner.condition;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import com.thizthizzydizzy.resourcespawner.Task;
import java.util.ArrayList;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
public class CubeWorldGuardRegionCondition implements Condition{
    private static final String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);
    private int radius;
    private boolean invert;
    private ArrayList<String> regions = new ArrayList<>();
    @Override
    public Condition newInstance(){
        return new CubeWorldGuardRegionCondition();
    }
    @Override
    public void loadFromConfig(ResourceSpawnerCore plugin, JsonObject json){
        if(ResourceSpawnerCore.debug)System.out.println("Loading "+getClass().getName());
        radius = json.getInt("radius", 0);//default only checks one block
        if(ResourceSpawnerCore.debug)System.out.println("radius: "+radius);
        invert = json.getBoolean("invert", false);
        if(ResourceSpawnerCore.debug)System.out.println("invert: "+invert);
        JsonValue rgs = json.get("regions");
        if(rgs!=null){
            for(JsonValue val : rgs.asArray()){
                regions.add(val.asString());
            }
        }
        if(ResourceSpawnerCore.debug)System.out.println("regions: "+regions.toString());
    }
    @Override
    public Task<Boolean> check(World world, Location location){
        if(ResourceSpawnerCore.debug)System.out.println("Creating check task for "+getClass().getName());
        int minX = location.getBlockX()-radius;
        int minY;
        if(serverVersion.contains("1_16")){
            minY = Math.max(0, location.getBlockY()-radius);
        }else{
            minY = Math.max(world.getMinHeight(), location.getBlockY()-radius);
        }
        int minZ = location.getBlockZ()-radius;
        int maxX = location.getBlockX()+radius;
        int maxY = Math.min(world.getMaxHeight(), location.getBlockY()+radius);
        int maxZ = location.getBlockZ()+radius;
        return new Task<Boolean>() {
            @Override
            public String getName(){
                return "cube-wg-condition:"+world.getName()+"|"+location.getX()+" "+location.getY()+" "+location.getZ()+"|"+getClass().getName();
            }
            private int x = minX-1, y = minY, z = minZ;
            private Boolean result = null;
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
                            return;
                        }
                    }
                }
                boolean regionFound = false;
                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                if(container==null){//RegionContainer is null. I don't know what this means, probably that there's no regions?
                    regionFound = false;
                }else{
                    RegionQuery query = container.createQuery();
                    com.sk89q.worldedit.util.Location loc = new com.sk89q.worldedit.util.Location(BukkitAdapter.adapt(world), x, y, z);
                    Set<ProtectedRegion> regions = query.getApplicableRegions(loc).getRegions();
                    for(ProtectedRegion r : regions){
                        if(CubeWorldGuardRegionCondition.this.regions.contains(r.getId())||regions.isEmpty())regionFound = true;
                    }
                }
                if(invert&&regionFound){
                    result = false;
                    if(ResourceSpawnerCore.debug)System.out.println("WorldGuardRegion fail: region found");
                }
                if(!invert&&!regionFound){
                    result = false;
                    if(ResourceSpawnerCore.debug)System.out.println("WorldGuardRegion fail: region missing");
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