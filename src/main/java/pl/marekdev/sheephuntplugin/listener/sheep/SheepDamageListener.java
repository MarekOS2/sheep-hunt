package pl.marekdev.sheephuntplugin.listener.sheep;

import com.google.inject.Singleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import pl.marekdev.sheephuntplugin.util.PluginKey;

import static pl.marekdev.sheephuntplugin.util.PluginKey.SHEEP;

@Singleton
public class SheepDamageListener implements Listener {
    @EventHandler
    private void onSheepDamaged(EntityDamageEvent entityDamageEvent) {
        if (entityDamageEvent.getEntity().getPersistentDataContainer().has(SHEEP.getNamespacedKey())) {
            entityDamageEvent.setCancelled(true);
        }
    }
}
