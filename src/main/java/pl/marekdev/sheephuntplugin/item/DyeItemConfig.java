package pl.marekdev.sheephuntplugin.item;

import com.google.inject.Inject;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import pl.marekdev.sheephuntplugin.util.PluginKey;

import java.util.Optional;

public class DyeItemConfig {
    private final ConfigurationSection itemConfig;
    @Getter
    private final Component displayNameComponent;


    @Inject
    public DyeItemConfig(JavaPlugin plugin) {
        this.itemConfig = plugin.getConfig().getConfigurationSection("sheep-dye");
        this.displayNameComponent = getDisplayNameComponentFromConfig();
    }

    private Component getDisplayNameComponentFromConfig() {
        String displayName = itemConfig.getString(
                "display-name",
                "baguette name is null"
        );
        Optional<TextColor> colorOptional = getDisplayNameColorFromConfig();
        Component component = Component.text(displayName)
                .color(colorOptional.orElse(NamedTextColor.AQUA))
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);


        if (itemConfig.getBoolean("display-name-is-bold", false)) {
            return component.decorate(TextDecoration.BOLD);

        }
        return component;
    }

    public ItemStack getDyeItem(Material material) {
        ItemStack dyeItem = new ItemStack(material);
        ItemMeta meta = dyeItem.getItemMeta();
        meta.displayName(displayNameComponent);
        meta.getPersistentDataContainer().set(PluginKey.DYE.getNamespacedKey(), PersistentDataType.BOOLEAN, true);

        dyeItem.setItemMeta(meta);
        return dyeItem;
    }

    private Optional<TextColor> getDisplayNameColorFromConfig() {
        ConfigurationSection colorConfigSection = itemConfig.getConfigurationSection("display-name-color");

        if (colorConfigSection == null) return Optional.empty();
        TextColor textColor = TextColor.color(
                colorConfigSection.getInt("red"),
                colorConfigSection.getInt("green"),
                colorConfigSection.getInt("blue")
        );
        return Optional.of(textColor);
    }
}
