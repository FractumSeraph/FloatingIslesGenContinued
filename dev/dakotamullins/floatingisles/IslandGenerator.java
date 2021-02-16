// 
// Decompiled by Procyon v0.5.36
// 

package dev.dakotamullins.floatingisles;

import dev.dakotamullins.floatingisles.Populators.TreePopulator;
import org.bukkit.Location;
import dev.dakotamullins.floatingisles.Populators.GrassPopulator;
import java.util.ArrayList;
import org.bukkit.generator.BlockPopulator;
import java.util.List;
import org.bukkit.util.noise.PerlinOctaveGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import java.util.Random;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;

public class IslandGenerator extends ChunkGenerator
{
    double scale;
    double threshold;
    long seed;
    boolean moreTrees;
    
    void setBlock(final int x, final int y, final int z, final byte[][] chunk, final Material material) {
        if (chunk[y >> 4] == null) {
            chunk[y >> 4] = new byte[4096];
        }
        if (y > 256 || y < 0 || x > 16 || x < 0 || z > 16 || z < 0) {
            return;
        }
        try {
            chunk[y >> 4][(y & 0xF) << 8 | z << 4 | x] = (byte)material.getId();
        }
        catch (Exception ex) {}
    }
    
    public IslandGenerator(final double scale, final double threshold, final long seed, final boolean moreTrees) {
        this.scale = scale;
        this.threshold = threshold;
        this.seed = seed;
        this.moreTrees = moreTrees;
    }
    
    public byte[][] generateBlockSections(final World world, final Random rand, final int ChunkX, final int ChunkZ, final ChunkGenerator.BiomeGrid biome) {
        final byte[][] chunk = new byte[world.getMaxHeight() / 16][];
        final SimplexOctaveGenerator islands = new SimplexOctaveGenerator(this.seed, 8);
        final PerlinOctaveGenerator roughMap = new PerlinOctaveGenerator(this.seed, 8);
        final SimplexOctaveGenerator bottum = new SimplexOctaveGenerator(this.seed, 8);
        islands.setScale(1.0 / this.scale);
        roughMap.setScale(0.0625);
        bottum.setScale(0.25);
        final double ratio = 1.0 - 1.0 / (this.scale / 64.0 * 5.0);
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                final int realX = x + ChunkX * 16;
                final int realZ = z + ChunkZ * 16;
                final double frequency = 0.5;
                final double amplitude = 0.5;
                double thickness = (islands.noise((double)realX, (double)realZ, frequency, amplitude) - this.threshold) / (1.0 - this.threshold) * this.scale;
                if (thickness > 0.0) {
                    double bottomThick = thickness / 8.0;
                    thickness = (Math.pow(ratio, thickness) - 1.0) / (ratio - 1.0);
                    final double roughness = (roughMap.noise((double)realX, (double)realZ, frequency, amplitude) + 1.0) / 2.0 * 1.5;
                    final double height = 128.0;
                    for (int y = (int)height; y < height + thickness * roughness; ++y) {
                        this.setBlock(x, y, z, chunk, Material.STONE);
                    }
                    bottomThick = (bottomThick + thickness / 2.0) / 2.0;
                    double bottomRough = (bottum.noise((double)realX, (double)realZ, frequency, amplitude) - 1.0) / 2.0;
                    bottomRough *= thickness;
                    for (int y2 = (int)height; y2 > height - bottomThick + bottomRough; --y2) {
                        this.setBlock(x, y2, z, chunk, Material.STONE);
                    }
                }
            }
        }
        return chunk;
    }
    
    public List<BlockPopulator> getDefaultPopulators(final World world) {
        final List<BlockPopulator> pops = new ArrayList<BlockPopulator>();
        pops.add(new GrassPopulator());
        pops.add(new TreePopulator(this.seed, this.moreTrees));
        return pops;
    }
    
    public boolean canSpawn(final World world, final int x, final int z) {
        return world.getHighestBlockYAt(x, z) > 0;
    }
    
    public Location getFixedSpawnLocation(final World world, final Random random) {
        return this.getNearestSpawn(world, 0.0, 0.0);
    }
    
    public Location getNearestSpawn(final World world, double x, final double z) {
        final SimplexOctaveGenerator islands = new SimplexOctaveGenerator(world, 8);
        islands.setScale(1.0 / this.scale);
        double prevThick;
        double thickness = prevThick = (islands.noise(x, 0.0, 0.5, 0.5) - this.threshold) / (1.0 - this.threshold);
        if (thickness <= 0.0) {
            while (thickness <= 0.0 || prevThick <= thickness) {
                ++x;
                prevThick = thickness;
                thickness = islands.noise(x, z, 0.5, 0.5) * 16.0 - 8.0;
            }
        }
        int y = world.getHighestBlockYAt((int)x, (int)z);
        if (y < 128) {
            y = (int)(130.0 + thickness);
        }
        return new Location(world, x, (double)y, z);
    }
}
