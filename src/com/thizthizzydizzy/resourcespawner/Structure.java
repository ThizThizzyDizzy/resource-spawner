package com.thizthizzydizzy.resourcespawner;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
public class Structure{
    public HashMap<Location, BlockData> data = new HashMap<>();
    public void addBlock(int x, int y, int z, BlockData block){
        data.put(new Location(null, x,y,z), block);
    }
    public void normalize(){
        if(ResourceSpawnerCore.debug)System.out.println("Normalizing structure");
        if(data.isEmpty())return;
        HashMap<Location, BlockData> copy = new HashMap<>(data);
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for(Location pos : copy.keySet()){
            if(pos.getBlockX()<minX)minX = pos.getBlockX();
            if(pos.getBlockY()<minY)minY = pos.getBlockY();
            if(pos.getBlockZ()<minZ)minZ = pos.getBlockZ();
            if(pos.getBlockX()>maxX)maxX = pos.getBlockX();
            if(pos.getBlockY()>maxY)maxY = pos.getBlockY();
            if(pos.getBlockZ()>maxZ)maxZ = pos.getBlockZ();
        }
        int xOff = -(minX+maxX)/2;
        int yOff = -(minY+maxY)/2;
        int zOff = -(minZ+maxZ)/2;
        if(ResourceSpawnerCore.debug)System.out.println("X "+minX+" "+maxX+" +"+xOff);
        if(ResourceSpawnerCore.debug)System.out.println("Y "+minY+" "+maxY+" +"+yOff);
        if(ResourceSpawnerCore.debug)System.out.println("Z "+minZ+" "+maxZ+" +"+zOff);
        for(Location pos : copy.keySet()){
            data.put(new Location(null, pos.getBlockX()+xOff, pos.getBlockY()+yOff, pos.getBlockZ()+zOff), copy.get(pos));
        }
    }
    private Structure[] rotationCache = new Structure[4];
    /**
     * Gets a rotated version of this structure. Results may be unpredictable if the structure is not normalized first.
     * @param rot the number of clockwize 90 degree turns to make (0-3)
     * @return a cached or new Structure representing the rotated structure
     */
    public Structure getRotated(int rot){
        if(rotationCache[rot]!=null)return rotationCache[rot];
        switch(rot){
            case 0:
                return rotationCache[0] = this;
            case 1:
                return rotationCache[1] = rotate();
            case 2:
                return rotationCache[2] = rotate().rotate();
            case 3:
                return rotationCache[3] = rotate().rotate().rotate();
            default:
                throw new IllegalArgumentException("Invalid structure rotation: "+rot+" (expected 0-3)");
        }
    }
    private Structure rotate(){
        Structure rotated = new Structure();
        for(Location key : data.keySet()){
            Location rotatedKey = new Location(key.getWorld(), -key.getZ(), key.getY(), key.getX());
            rotated.data.put(rotatedKey, data.get(key));
        }
        rotated.normalize();
        return rotated;
    }
}