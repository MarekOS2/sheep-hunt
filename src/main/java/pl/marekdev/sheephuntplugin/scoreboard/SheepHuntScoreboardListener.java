package pl.marekdev.sheephuntplugin.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.marekdev.sheephuntplugin.controller.SheepHuntController;

public class SheepHuntScoreboardListener implements Listener {

    private final ShepHuntScoreboard scoreboard;

    public SheepHuntScoreboardListener(SheepHuntController sheepHuntController) {
       this.scoreboard = sheepHuntController.getScoreboard();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent playerJoinEvent) {
        this.scoreboard.add(playerJoinEvent.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent playerQuitEvent) {
        Player eventPlayer = playerQuitEvent.getPlayer();
        this.scoreboard.remove(eventPlayer);
    }
}
