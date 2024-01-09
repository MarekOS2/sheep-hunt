package pl.marekdev.sheephuntplugin.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.marekdev.sheephuntplugin.controller.SheepHuntController;

import java.util.List;
@Singleton
public class StartSheepHuntPluginCommand implements CommandExecutor, TabCompleter {

    private final SheepHuntController sheepHuntController;

    @Inject
    public StartSheepHuntPluginCommand(SheepHuntController sheepHuntController) {
        this.sheepHuntController = sheepHuntController;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("sheep-hunt.start-sheep-hunt") && !sender.isOp()) return true;
        if (!(sender instanceof Player player)) return true;

        if (sheepHuntController.isSheepHuntGoingOn()) {
            player.sendMessage(Component.text("Sheep hunt has already been started."));
            return true;
        }

        sheepHuntController.startSheepHunt(player);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
