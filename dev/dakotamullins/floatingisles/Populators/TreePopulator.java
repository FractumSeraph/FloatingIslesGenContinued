// 
// Decompiled by Procyon v0.5.36
// 

package dev.dakotamullins.floatingisles.Populators;

import org.bukkit.TreeType;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.Chunk;
import org.bukkit.World;
import java.util.Random;
import org.bukkit.generator.BlockPopulator;

public class TreePopulator extends BlockPopulator
{
    private final Random random;
    private final boolean moreTrees;
    
    public TreePopulator(final long seed, final boolean moreTrees) {
        this.random = new Random(seed);
        this.moreTrees = moreTrees;
    }
    
    public void populate(final World world, final Random r, final Chunk chunk) {
        if (!chunk.getBlock(0, 0, 0).getBiome().equals(Biome.FOREST) &&
                !chunk.getBlock(0, 0, 0).getBiome().equals((Object)Biome.BIRCH_FOREST_HILLS.name()) && //TODO Make this check for DARK_FOREST_HILLS too.
                !this.moreTrees && this.random.nextDouble() > 0.04) {
            return;
        }
        final int x = this.random.nextInt(16) + chunk.getX() * 16;
        final int z = this.random.nextInt(16) + chunk.getZ() * 16;
        final Location loc = new Location(world, x, world.getHighestBlockYAt(x, z), z);
        if (loc.getY() <= 1.0) {
            return;
        }
        final TreeType[] types = { TreeType.TREE, TreeType.BIRCH, TreeType.BIG_TREE };
        double chance = 0.7;
        TreeType[] array;
        for (int length = (array = types).length, i = 0; i < length; ++i) {
            final TreeType type = array[i];
            if (this.random.nextDouble() < chance) {
                world.generateTree(loc, type);
                return;
            }
            chance *= 0.5;
        }
        world.generateTree(loc, types[0]);
    }
}
