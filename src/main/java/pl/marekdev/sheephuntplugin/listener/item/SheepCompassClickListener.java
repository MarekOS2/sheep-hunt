package pl.marekdev.sheephuntplugin.listener.item;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import pl.marekdev.sheephuntplugin.controller.SheepHuntController;
import pl.marekdev.sheephuntplugin.util.PluginKey;

import javax.inject.Named;

@Singleton
public class SheepCompassClickListener implements Listener {

    private final SheepHuntController sheepHuntController;

    @Inject
    public SheepCompassClickListener(SheepHuntController sheepHuntController) {
        this.sheepHuntController = sheepHuntController;
    }

    @EventHandler
    private void onCompassClick(PlayerInteractEvent playerInteractEvent) {
        if (!sheepHuntController.isSheepHuntGoingOn()) {
            return;
        }
        if (playerInteractEvent.getItem() == null) return;
        ItemStack eventItem = playerInteractEvent.getItem();
        if (!eventItem.hasItemMeta()) return;

        if (eventItem.getItemMeta().getPersistentDataContainer().has(PluginKey.COMPASS.getNamespacedKey())) {
            Location sheepLocation = sheepHuntController.getCurrentSheepToDye().getLocation();
            playerInteractEvent.getPlayer().sendMessage(
                    Component.text("Sheep location:" + (sheepHuntController.isNetherSheep() ? ChatColor.RED + " nether: TRUE " : "") + " x: %d, y: %d z:%d".formatted(
                            sheepLocation.getBlockX(),
                            sheepLocation.getBlockY(),
                            sheepLocation.getBlockZ()))
                            .color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD));
        }
    }
}
