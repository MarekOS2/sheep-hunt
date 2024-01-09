package pl.marekdev.sheephuntplugin.util;

import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Sheep;
import pl.marekdev.sheephuntplugin.SheepHuntPlugin;

@Getter
public enum PluginKey {

    SHEEP(new NamespacedKey(SheepHuntPlugin.getPlugin(SheepHuntPlugin.class), "sheep")),
    COMPASS(new NamespacedKey(SheepHuntPlugin.getPlugin(SheepHuntPlugin.class), "compass")),
    DYE(new NamespacedKey(SheepHuntPlugin.getPlugin(SheepHuntPlugin.class), "dye"));

    private final NamespacedKey namespacedKey;

    PluginKey(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }
}
