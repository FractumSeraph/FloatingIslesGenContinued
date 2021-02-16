// 
// Decompiled by Procyon v0.5.36
// 

package dev.dakotamullins.floatingisles;

import org.bukkit.event.EventHandler;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.WorldType;
import org.bukkit.WorldCreator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import java.util.logging.Logger;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class FloatingIsles extends JavaPlugin implements Listener
{
    public final Logger logger;
    
    public FloatingIsles() {
        this.logger = Bukkit.getLogger();
    }
    
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this);
        this.saveDefaultConfig();
        final PluginDescriptionFile pdffile = this.getDescription();
        this.logger.info(String.valueOf(pdffile.getName()) + " version " + pdffile.getVersion() + " is now enabled.");
    }
    
    public void onDisable() {
        final PluginDescriptionFile pdffile = this.getDescription();
        this.logger.info(String.valueOf(pdffile.getName()) + " is now disabled");
    }
    
    public ChunkGenerator getDefaultWorldGenerator(final String worldName, final String GenId) {
        double scale = this.getConfig().getDouble("world-gen.scale");
        double threshold = this.getConfig().getDouble("world-gen.threshold");
        if (scale > 256.0 || scale < 16.0) {
            this.logger.info("[FloatingIsles] Scale for world \"" + worldName + "\" generation is invalid: It must be a between 16 and 256.");
            this.logger.info("[FloatingIsles] Setting scale to 64.");
            scale = 64.0;
            this.getConfig().set("world-gen.scale", (Object)64.0);
            this.saveConfig();
        }
        if (threshold < 0.0 || threshold > 1.0) {
            this.logger.info("[FloatingIsles] Threshold for world \"" + worldName + "\" generation is invalid: It must be a between 0.0 and 1.0");
            this.logger.info("[FloatingIsles] Setting threshold to 0.5");
            threshold = 0.5;
            this.getConfig().set("world-gen.threshold", (Object)0.5);
            this.saveConfig();
        }
        long seed = Bukkit.getWorlds().get(0).getSeed();
        if (this.getConfig().isLong("world-gen.seed")) {
            seed = this.getConfig().getLong("world-gen.seed");
        }
        else if (this.getConfig().isString("world-gen.seed")) {
            if (!this.getConfig().getString("world-gen.seed").equalsIgnoreCase("default")) {
                this.getConfig().set("world-gen.seed", (Object)"default");
            }
        }
        else {
            this.getConfig().set("world-gen.seed", (Object)"default");
        }
        this.logger.info("[FloatingIsles] Generating world \"" + worldName + "\" with a scale of " + scale + " a threshold of " + threshold + " and a seed of " + seed);
        return new IslandGenerator(scale, threshold, seed, this.getConfig().getBoolean("world-gen.more-trees"));
    }
    
    public boolean loadWorld() {
        final String name = this.getConfig().getString("gen-world");
        if (name == null || name == "" || name == "none" || name == "false") {
            return false;
        }
        if (Bukkit.getWorld(name) != null) {
            return false;
        }
        final WorldCreator creator = new WorldCreator(name);
        creator.generator(this.getDefaultWorldGenerator(name, null));
        creator.type(WorldType.FLAT);
        this.getServer().createWorld(creator);
        return true;
    }
    
    @EventHandler
    public void onPluginLoad(final PluginEnableEvent event) {
        final Plugin plugin = event.getPlugin();
        if (!plugin.getDescription().getLoad().equals((Object)PluginLoadOrder.POSTWORLD)) {
            return;
        }
        this.loadWorld();
    }
}
