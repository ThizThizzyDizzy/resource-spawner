package com.thizthizzydizzy.resourcespawner.scanner;
import com.thizthizzydizzy.resourcespawner.ResourceSpawnerCore;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;
import org.hjson.JsonObject;
public class DirectionStructureScanner extends StructureScanner{
    private boolean showDistance;
    private boolean useDistancePrefixes;
    private boolean useIntercardinals;
    private boolean useSecondaryIntercardinals;
    private boolean showDirection;
    @Override
    public Scanner newInstance(){
        return new DirectionStructureScanner();
    }
    @Override
    public void loadFromConfig(ResourceSpawnerCore plugin, JsonObject obj){
        showDistance = obj.getBoolean("show_distance", false);
        useDistancePrefixes = obj.getBoolean("use_distance_prefixes", true);
        showDirection = obj.getBoolean("show_direction", false);
        useIntercardinals = obj.getBoolean("use_intercardinals", true);
        useSecondaryIntercardinals = obj.getBoolean("use_secondary_intercardinals", false);
    }
    @Override
    public String format(String displayName, Location playerPos, Location structurePos){
        String str = displayName;
        if(showDirection||showDistance)str+=" (";
        if(showDistance){
            int distance = (int)playerPos.distance(structurePos);
            String unit = "m";
            if(useDistancePrefixes){
                if(distance>=1000){
                    distance/=1000;
                    unit = "km";
                    if(distance>=1000){
                        distance/=1000;
                        unit = "Mm";
                    }
                }
            }
            str+=distance+unit;
        }
        if(showDistance&&showDirection)str+=" ";
        if(showDirection){
            Vector v = structurePos.subtract(playerPos).toVector().normalize();
            HashMap<Vector, String> directions = new HashMap<>();
            directions.put(BlockFace.NORTH.getDirection(), "N");
            directions.put(BlockFace.SOUTH.getDirection(), "S");
            directions.put(BlockFace.EAST.getDirection(), "E");
            directions.put(BlockFace.WEST.getDirection(), "W");
            directions.put(BlockFace.UP.getDirection(), "UP");
            directions.put(BlockFace.DOWN.getDirection(), "DOWN");
            if(useIntercardinals){
                directions.put(BlockFace.NORTH_EAST.getDirection(), "NE");
                directions.put(BlockFace.NORTH_WEST.getDirection(), "NW");
                directions.put(BlockFace.SOUTH_EAST.getDirection(), "SE");
                directions.put(BlockFace.SOUTH_WEST.getDirection(), "SW");
            }
            if(useSecondaryIntercardinals){
                directions.put(BlockFace.EAST_NORTH_EAST.getDirection(), "ENE");
                directions.put(BlockFace.EAST_SOUTH_EAST.getDirection(), "ESE");
                directions.put(BlockFace.NORTH_NORTH_EAST.getDirection(), "NNE");
                directions.put(BlockFace.NORTH_NORTH_WEST.getDirection(), "NNW");
                directions.put(BlockFace.SOUTH_SOUTH_EAST.getDirection(), "SSE");
                directions.put(BlockFace.SOUTH_SOUTH_WEST.getDirection(), "SSW");
                directions.put(BlockFace.WEST_NORTH_WEST.getDirection(), "WNW");
                directions.put(BlockFace.WEST_SOUTH_WEST.getDirection(), "WSW");
            }
            String closest = null;
            double closestAngle = 0;
            for(Vector vec : directions.keySet()){
                double angle = vec.normalize().angle(v);
                if(closest==null||angle<closestAngle){
                    closest = directions.get(vec);
                    closestAngle = angle;
                }
            }
            str+=closest;
        }
        if(showDirection||showDistance)str+=")";
        return str;
    }
}