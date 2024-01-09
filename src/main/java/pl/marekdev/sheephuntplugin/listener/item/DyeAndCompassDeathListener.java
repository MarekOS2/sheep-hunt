package pl.marekdev.sheephuntplugin.listener.item;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import pl.marekdev.sheephuntplugin.controller.SheepHuntController;
import pl.marekdev.sheephuntplugin.util.PluginKey;

import java.util.*;

import static pl.marekdev.sheephuntplugin.util.PluginKey.*;
import static pl.marekdev.sheephuntplugin.util.PluginKey.DYE;

@Singleton
public class DyeAndCompassDeathListener implements Listener {

    private final SheepHuntController sheepHuntController;

    @Inject
    public DyeAndCompassDeathListener(SheepHuntController sheepHuntController) {
        this.sheepHuntController = sheepHuntController;
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent playerDeathEvent) {
        if (sheepHuntController.getHuntedPlayer() == null) {
            return;
        }

        if (!sheepHuntController.getHuntedPlayer().getUniqueId().equals(playerDeathEvent.getPlayer().getUniqueId())) {
            return;
        }

        for (Iterator<ItemStack> iterator = playerDeathEvent.getDrops().iterator(); iterator.hasNext(); ) {
            ItemStack drop = iterator.next();
            if (!drop.hasItemMeta()) continue;

            ItemMeta meta = drop.getItemMeta();
            PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

            if (dataContainer.has(DYE.getNamespacedKey()) || dataContainer.has(COMPASS.getNamespacedKey())) {
                playerDeathEvent.getItemsToKeep().add(drop);
                iterator.remove();
            }
        }
    }

}
