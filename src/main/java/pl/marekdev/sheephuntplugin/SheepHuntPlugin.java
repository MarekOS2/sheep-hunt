package pl.marekdev.sheephuntplugin;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.marekdev.sheephuntplugin.command.EndSheepHuntCommand;
import pl.marekdev.sheephuntplugin.command.StartSheepHuntPluginCommand;
import pl.marekdev.sheephuntplugin.listener.item.CompassNetherListener;
import pl.marekdev.sheephuntplugin.listener.item.DyeAndCompassDeathListener;
import pl.marekdev.sheephuntplugin.listener.item.DyeAndCompassDropListener;
import pl.marekdev.sheephuntplugin.listener.item.SheepCompassClickListener;
import pl.marekdev.sheephuntplugin.listener.sheep.SheepDamageListener;
import pl.marekdev.sheephuntplugin.listener.sheep.SheepDyeListener;
import pl.marekdev.sheephuntplugin.listener.sheep.SheepMoveListener;
import pl.marekdev.sheephuntplugin.module.PluginModule;

import java.util.Objects;

public final class SheepHuntPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getConfig().options().parseComments(true);
        saveDefaultConfig();
        Injector injector = Guice.createInjector(new PluginModule(this));
        registerCommands(injector);
        registerListeners(injector);
    }

    private void registerListeners(Injector injector) {
        Bukkit.getPluginManager().registerEvents(injector.getInstance(SheepDyeListener.class), this);
        Bukkit.getPluginManager().registerEvents(injector.getInstance(SheepDamageListener.class), this);
        Bukkit.getPluginManager().registerEvents(injector.getInstance(SheepMoveListener.class), this);

        Bukkit.getPluginManager().registerEvents(injector.getInstance(DyeAndCompassDeathListener.class), this);
        Bukkit.getPluginManager().registerEvents(injector.getInstance(DyeAndCompassDropListener.class), this);
        Bukkit.getPluginManager().registerEvents(injector.getInstance(SheepCompassClickListener.class), this);
        Bukkit.getPluginManager().registerEvents(injector.getInstance(CompassNetherListener.class), this);
    }

    private void registerCommands(Injector injector) {
        Objects.requireNonNull(getCommand("sheep-hunt-start")).setExecutor(injector.getInstance(StartSheepHuntPluginCommand.class));
        Objects.requireNonNull(getCommand("sheep-hunt-end")).setExecutor(injector.getInstance(EndSheepHuntCommand.class));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveConfig();
    }
}
