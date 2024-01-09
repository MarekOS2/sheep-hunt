package pl.marekdev.sheephuntplugin.listener.sheep;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.bukkit.inventory.ItemStack;
import pl.marekdev.sheephuntplugin.controller.SheepHuntController;
import pl.marekdev.sheephuntplugin.util.PluginKey;

import static pl.marekdev.sheephuntplugin.util.PluginKey.DYE;
import static pl.marekdev.sheephuntplugin.util.PluginKey.SHEEP;

@Singleton
public class SheepDyeListener implements Listener {

    private final SheepHuntController sheepHuntController;

    @Inject
    public SheepDyeListener(SheepHuntController sheepHuntController) {
        this.sheepHuntController = sheepHuntController;
    }

    @EventHandler
    private void onSheepDyed(SheepDyeWoolEvent sheepDyeWoolEvent) {
        if (!sheepHuntController.isSheepHuntGoingOn()) {
            return;
        }

        Sheep sheep = sheepDyeWoolEvent.getEntity();
        Player eventPlayer = sheepDyeWoolEvent.getPlayer();

        if (!sheep.getPersistentDataContainer().has(SHEEP.getNamespacedKey())) {
            if (eventPlayer == null) {
                return;
            }
            ItemStack itemInMainHand = eventPlayer.getInventory().getItemInMainHand();
            if (!itemInMainHand.hasItemMeta()) {
                return;
            }
            if (itemInMainHand.getItemMeta().getPersistentDataContainer().has(DYE.getNamespacedKey())) {
                sheepDyeWoolEvent.setCancelled(true);
                return;
            }
            return;
        }

        if (eventPlayer == null) return;
        ItemStack itemInMainHand = eventPlayer.getInventory().getItemInMainHand();

        if (!itemInMainHand.hasItemMeta() || !itemInMainHand.getItemMeta().getPersistentDataContainer().has(DYE.getNamespacedKey())) {
            sheepDyeWoolEvent.setCancelled(true);
            return;
        }
        if (!eventPlayer.getUniqueId().equals(sheepHuntController.getHuntedPlayer().getUniqueId())) {
            sheepDyeWoolEvent.setCancelled(true);
            return;
        }

        sheep.getPersistentDataContainer().remove(SHEEP.getNamespacedKey());
        boolean sheepHuntEnded = sheepHuntController.addSheepDyed();

        if (!sheepHuntEnded) {
            sheepHuntController.spawnSheep();
        }
    }
}
