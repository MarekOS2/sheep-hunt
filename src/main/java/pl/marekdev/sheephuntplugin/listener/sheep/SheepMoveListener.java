package pl.marekdev.sheephuntplugin.listener.sheep;

import com.google.inject.Singleton;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.marekdev.sheephuntplugin.util.PluginKey;

import static pl.marekdev.sheephuntplugin.util.PluginKey.SHEEP;

@Singleton
public class SheepMoveListener implements Listener {

    @EventHandler
    private void onSheepMove(EntityMoveEvent entityMoveEvent) {
        if (!entityMoveEvent.getEntity().getPersistentDataContainer().has(SHEEP.getNamespacedKey())) return;
        if (entityMoveEvent.hasChangedBlock()) {
            entityMoveEvent.setCancelled(true);
        }
    }
}
