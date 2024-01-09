package pl.marekdev.sheephuntplugin.listener.item;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import pl.marekdev.sheephuntplugin.controller.SheepHuntController;
import pl.marekdev.sheephuntplugin.util.PluginKey;

import static pl.marekdev.sheephuntplugin.util.PluginKey.*;
import static pl.marekdev.sheephuntplugin.util.PluginKey.COMPASS;

@Singleton
public class DyeAndCompassDropListener implements Listener {

    private final SheepHuntController sheepHuntController;

    @Inject
    public DyeAndCompassDropListener(SheepHuntController sheepHuntController) {
        this.sheepHuntController = sheepHuntController;
    }

    @EventHandler
    private void onPlayerDropItem(PlayerDropItemEvent playerDropItemEvent) {
        if (sheepHuntController.getHuntedPlayer() == null) {
            return;
        }
        if (!playerDropItemEvent.getPlayer().getUniqueId().equals(sheepHuntController.getHuntedPlayer().getUniqueId())) {
            return;
        }
        ItemStack eventItem = playerDropItemEvent.getItemDrop().getItemStack();
        if (!eventItem.hasItemMeta()) return;
        PersistentDataContainer itemData = eventItem.getItemMeta().getPersistentDataContainer();

        if (itemData.has(COMPASS.getNamespacedKey()) || itemData.has(DYE.getNamespacedKey())) {
            playerDropItemEvent.setCancelled(true);
        }
    }
}
