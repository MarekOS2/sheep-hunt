package pl.marekdev.sheephuntplugin.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.marekdev.sheephuntplugin.controller.SheepHuntController;

import java.util.Collections;
import java.util.List;

@Singleton
public class EndSheepHuntCommand implements CommandExecutor, TabCompleter {

    private final SheepHuntController sheepHuntController;

    @Inject
    public EndSheepHuntCommand(SheepHuntController sheepHuntController) {
        this.sheepHuntController = sheepHuntController;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("sheep-hunt.start-sheep-hunt") && !sender.isOp()) return true;


        if (sheepHuntController.isSheepHuntGoingOn()) {
            sheepHuntController.endSheepHunt();
            sender.sendMessage("Sheep hunt ended");
            return true;
        }
        sender.sendMessage("Sheep hunt is not going on");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
