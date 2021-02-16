// 
// Decompiled by Procyon v0.5.36
// 

package dev.dakotamullins.floatingisles;

import org.bukkit.Chunk;
import java.util.HashSet;
import org.bukkit.World;

public class Blocks
{
    public static final int[][] mods;
    
    static {
        mods = new int[][] { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };
    }
    
    public static void loadArea(final World world, final int x, final int z, final int radius) {
        final HashSet<Chunk> chunks = new HashSet<Chunk>();
        chunks.add(world.getBlockAt(x, 0, z).getChunk());
        for (int i = 0; i < radius; ++i) {
            for (final Chunk chunk : new HashSet<Chunk>(chunks)) {
                int[][] mods;
                for (int length = (mods = Blocks.mods).length, j = 0; j < length; ++j) {
                    final int[] mod = mods[j];
                    chunks.add(world.getChunkAt(chunk.getX() + mod[0], chunk.getZ() + mod[1]));
                }
            }
        }
        for (final Chunk chunk2 : chunks) {
            chunk2.load();
        }
    }
}
