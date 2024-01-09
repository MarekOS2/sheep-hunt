package pl.marekdev.sheephuntplugin.module;

import com.google.inject.AbstractModule;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class PluginModule extends AbstractModule {

    private final JavaPlugin plugin;

    public PluginModule(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(JavaPlugin.class).toInstance(plugin);
        bind(BukkitScheduler.class).toInstance(Bukkit.getScheduler());
    }
}
