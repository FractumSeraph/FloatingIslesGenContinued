// 
// Decompiled by Procyon v0.5.36
// 

package dev.dakotamullins.floatingisles.Populators;

import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.Chunk;
import java.util.Random;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

public class GrassPopulator extends BlockPopulator
{
    public void populate(final World arg0, final Random arg1, final Chunk arg2) {
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                Block block = arg0.getHighestBlockAt(arg2.getX() * 16 + x, arg2.getZ() * 16 + z).getRelative(BlockFace.DOWN);
                if (block.getTypeId() != 0) {
                    if (block.getType().equals((Object)Material.LEAVES)) {
                        while (!block.getType().equals((Object)Material.STONE)) {
                            block = block.getRelative(BlockFace.DOWN);
                            if (block.getY() == 0) {
                                return;
                            }
                        }
                    }
                    block.setType(Material.GRASS);
                    double height = block.getY() - arg1.nextDouble() * 2.0 - 2.0;
                    if (height < 127.0 + arg1.nextDouble() * 2.0) {
                        height = 127.0 + arg1.nextDouble() * 2.0;
                    }
                    while (block.getY() > height) {
                        block = block.getRelative(BlockFace.DOWN);
                        if (block.getTypeId() != 1) {
                            break;
                        }
                        block.setType(Material.DIRT);
                    }
                }
            }
        }
    }
}
