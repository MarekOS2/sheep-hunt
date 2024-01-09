package pl.marekdev.sheephuntplugin.listener.item;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import pl.marekdev.sheephuntplugin.controller.SheepHuntController;

import static org.bukkit.World.Environment.*;

@Singleton
public class CompassNetherListener implements Listener {

    private final SheepHuntController sheepHuntController;

    @Inject
    public CompassNetherListener(SheepHuntController sheepHuntController) {
        this.sheepHuntController = sheepHuntController;
    }

    @EventHandler
    private void onNetherEnter(PlayerChangedWorldEvent playerChangedWorldEvent) {
        if (!sheepHuntController.isSheepHuntGoingOn()) return;
        if (!sheepHuntController.isNetherSheep()) return;
        if (!sheepHuntController.getHuntedPlayer().getUniqueId().equals(playerChangedWorldEvent.getPlayer().getUniqueId())) return;

        World world = playerChangedWorldEvent.getPlayer().getWorld();
        if (!world.getEnvironment().equals(NETHER)) return;

        System.out.println(sheepHuntController.getCurrentSheepToDye().getLocation().getWorld().getEnvironment() + "0");
        System.out.println(playerChangedWorldEvent.getPlayer().getWorld().getEnvironment() + "1");

        sheepHuntController.getSheepCompassItem().setCompassToPointAtSheep(sheepHuntController.getCurrentSheepToDye(), sheepHuntController.getHuntedPlayer(), true);
    }
}
