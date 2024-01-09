package pl.marekdev.sheephuntplugin.item;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import pl.marekdev.sheephuntplugin.util.PluginKey;

public class SheepCompassItem {
    private final SheepCompassItemConfig sheepCompassItemConfig;

    @Getter
    private final ItemStack itemStack;
    private final JavaPlugin plugin;
    private BukkitTask bukkitTask;

    public SheepCompassItem(JavaPlugin plugin, SheepCompassItemConfig sheepCompassItemConfig) {
        this.itemStack = new ItemStack(Material.COMPASS);
        this.plugin = plugin;
        this.sheepCompassItemConfig = sheepCompassItemConfig;

        setupCompassMeta();
    }

    private void setupCompassMeta() {
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(sheepCompassItemConfig.getDisplayNameComponent());
        meta.getPersistentDataContainer().set(PluginKey.COMPASS.getNamespacedKey(), PersistentDataType.BOOLEAN, true);
        itemStack.setItemMeta(meta);
        System.out.println(itemStack.getItemMeta().getPersistentDataContainer().getKeys());
    }

    public void setCompassToPointAtSheep(Entity sheep, Player huntedPlayer, boolean nether) {
        if (nether) {
            if (bukkitTask != null) {
                bukkitTask.cancel();
                bukkitTask = null;
            }
//            Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Location lodestone = new Location(sheep.getWorld(), sheep.getX(), 20, sheep.getZ());
            lodestone.getBlock().setType(Material.LODESTONE);

            CompassMeta meta = (CompassMeta) itemStack.getItemMeta();
            meta.setLodestoneTracked(true);
            meta.setLodestone(lodestone);
            itemStack.setItemMeta(meta);
//            }, 1, 1);

        } else {
            huntedPlayer.setCompassTarget(sheep.getLocation());
        }
    }

}
