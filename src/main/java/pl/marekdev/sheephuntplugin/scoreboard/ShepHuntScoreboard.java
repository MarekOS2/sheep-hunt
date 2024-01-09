package pl.marekdev.sheephuntplugin.scoreboard;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import pl.marekdev.sheephuntplugin.scoreboard.fastboard.FastBoard;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import pl.marekdev.sheephuntplugin.controller.SheepHuntController;
import pl.marekdev.sheephuntplugin.scoreboard.fastboard.FastBoardBase;

import java.util.HashMap;
import java.util.UUID;

public class ShepHuntScoreboard {

    private final SheepHuntController sheepHuntController;
    private final HashMap<UUID, FastBoard> scoreboards = new HashMap<>();

    public ShepHuntScoreboard(SheepHuntController sheepHuntController) {
        this.sheepHuntController = sheepHuntController;
    }

    public void add(Player player) {
        if (scoreboards.containsKey(player.getUniqueId())) {
            return;
        }

        FastBoard scoreboard = new FastBoard(player);
        setup(scoreboard);

        this.scoreboards.put(player.getUniqueId(), scoreboard);
    }

    public void update() {
        scoreboards.values().forEach(this::updateDescription);
    }

    public void delete() {
        scoreboards.values().forEach(FastBoardBase::delete);
    }

    public void remove(Player player) {
        FastBoard removed = this.scoreboards.remove(player.getUniqueId());
        if (removed != null) removed.delete();
    }

    private void setup(FastBoard scoreboard) {
        updateTitle(scoreboard);
        updateDescription(scoreboard);
    }

    private void updateTitle(FastBoard scoreboard) {
        scoreboard.updateTitle(Component.text("Hunted player: ").decorate(TextDecoration.BOLD));
    }

    private void updateDescription(FastBoard scoreboard) {
        scoreboard.updateLines(
                Component.text("  " + sheepHuntController.getHuntedPlayer().getName()).color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD),
                Component.text(" "),
                Component.text("  Sheep dyed:  %d".formatted(sheepHuntController.getSheepDyed())).color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD),
                Component.text("  Sheep to win: %d".formatted(sheepHuntController.getSheepDyedToWin())).color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD),
                Component.text(" ")
        );
    }

}
