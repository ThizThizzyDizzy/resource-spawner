package com.thizthizzydizzy.resourcespawner;
import java.util.HashMap;
import org.bukkit.block.data.BlockData;
public class Structure{
    public HashMap<int[], BlockData> data = new HashMap<>();
    public void addBlock(int x, int y, int z, BlockData block){
        data.put(new int[]{x,y,z}, block);
    }
    public void normalize(){
        if(data.isEmpty())return;
        HashMap<int[], BlockData> copy = new HashMap<>(data);
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for(int[] pos : copy.keySet()){
            if(pos[0]>minX)minX = pos[0];
            if(pos[1]>minY)minY = pos[1];
            if(pos[2]>minZ)minZ = pos[2];
            if(pos[0]<maxX)maxX = pos[0];
            if(pos[1]<maxY)maxY = pos[1];
            if(pos[2]<maxZ)maxZ = pos[2];
        }
        int xOff = -(minX+maxX)/2;
        int yOff = -(minY+maxY)/2;
        int zOff = -(minZ+maxZ)/2;
        for(int[] pos : copy.keySet()){
            data.put(new int[]{pos[0]+xOff, pos[1]+yOff, pos[2]+zOff}, copy.get(pos));
        }
    }
}