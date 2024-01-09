package pl.marekdev.sheephuntplugin.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import pl.marekdev.sheephuntplugin.item.DyeItemConfig;
import pl.marekdev.sheephuntplugin.item.SheepCompassItem;
import pl.marekdev.sheephuntplugin.item.SheepCompassItemConfig;
import pl.marekdev.sheephuntplugin.scoreboard.ShepHuntScoreboard;
import pl.marekdev.sheephuntplugin.util.PluginKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static org.bukkit.Material.*;
import static org.bukkit.World.Environment.NETHER;
import static org.bukkit.World.Environment.NORMAL;
import static pl.marekdev.sheephuntplugin.util.PluginKey.SHEEP;

@Singleton
public class SheepHuntController {
    private final Map<Material, Material> dyesAndWools;

    @Getter
    private final int sheepDyedToWin;
    private final int sheepSpawnRadius;
    private final SheepCompassItemConfig sheepCompassItemConfig;
    private final DyeItemConfig dyeItemConfig;
    private final JavaPlugin plugin;
    @Getter
    private int sheepDyed;
    @Getter
    private Sheep currentSheepToDye;
    @Getter
    private SheepCompassItem sheepCompassItem;
    @Getter
    private Player huntedPlayer;
    @Getter
    private boolean sheepHuntGoingOn;
    @Getter
    private ShepHuntScoreboard scoreboard;
    private ItemStack currentDye;

    @Getter
    private boolean netherSheep;


    @Inject
    public SheepHuntController(JavaPlugin plugin, SheepCompassItemConfig sheepCompassItemConfig, DyeItemConfig dyeItemConfig) {
        this.plugin = plugin;
        this.dyeItemConfig = dyeItemConfig;
        this.dyesAndWools = getDyesAndWools();
        this.sheepCompassItemConfig = sheepCompassItemConfig;

        this.sheepSpawnRadius = plugin.getConfig().getInt("sheep-spawn-radius");
        this.sheepDyedToWin = plugin.getConfig().getInt("sheep-to-dye-to-win");
    }

    public void startSheepHunt(Player huntedPlayer) {
        if (sheepHuntGoingOn || this.huntedPlayer != null || this.sheepCompassItem != null) {
            throw new IllegalStateException();
        }

        this.huntedPlayer = huntedPlayer;
        this.sheepCompassItem = new SheepCompassItem(plugin, sheepCompassItemConfig);
        this.huntedPlayer.getInventory().addItem(sheepCompassItem.getItemStack());
        this.sheepHuntGoingOn = true;

        this.scoreboard = new ShepHuntScoreboard(this);
        Bukkit.getOnlinePlayers().forEach(player -> scoreboard.add(player));

        spawnSheep();
    }

    public Material addRandomSheepDyeToInventory() {
        List<Material> keysList = new ArrayList<>(dyesAndWools.keySet());
        Material material = keysList.get(ThreadLocalRandom.current().nextInt(keysList.size()));

        this.currentDye = dyeItemConfig.getDyeItem(material);

        Bukkit.getScheduler().runTaskLater(plugin, () -> this.huntedPlayer.getInventory().setItem(8, currentDye), 1);
        return material;
    }

    public void spawnSheep() {
        Location sheepSpawnLocation = getSheepSpawnLocation();

//        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Material addedDye = addRandomSheepDyeToInventory();
            spawnBlockBelowSheep(sheepSpawnLocation, addedDye);

            this.currentSheepToDye = (Sheep) huntedPlayer.getWorld().spawnEntity(sheepSpawnLocation, EntityType.SHEEP);
            this.currentSheepToDye.getPersistentDataContainer().set(
                    SHEEP.getNamespacedKey(),
                    PersistentDataType.BOOLEAN,
                    true
            );
            sheepCompassItem.setCompassToPointAtSheep(currentSheepToDye, huntedPlayer, netherSheep);
//        }, 1);
    }

    private void spawnBlockBelowSheep(Location sheepSpawnLocation, Material dye) {
        Location cloned = sheepSpawnLocation.clone();
        cloned.subtract(0, 1, 0).getBlock().setType(dyesAndWools.get(dye));
    }

    public boolean addSheepDyed() {
        sheepDyed++;

        if (sheepDyed == sheepDyedToWin) {
            endSheepHunt();
            return true;
        }
        String message = plugin.getConfig().getString("player-dyed-sheep-message");
        String replaced = message.replace("%current%", String.valueOf(sheepDyed)).replace("%target%", String.valueOf(sheepDyedToWin));

        this.scoreboard.update();
        this.huntedPlayer.sendMessage(Component.text(ChatColor.translateAlternateColorCodes('&', replaced)));
        return false;
    }

    public void endSheepHunt() {
        if (this.sheepDyed == this.sheepDyedToWin) {
            String message = Objects.requireNonNull(this.plugin.getConfig().getString("hunt-completed-message-all-chat"));
            Bukkit.getServer().broadcast(Component.text(ChatColor.translateAlternateColorCodes('&', message.replace("%playerName%", huntedPlayer.getName()))));
        }

        this.scoreboard.delete();

        removeCompass(huntedPlayer.getInventory());
        this.huntedPlayer.getInventory().remove(currentDye);
        tearDown();
    }

    private void removeCompass(Inventory inventory) {
        for (int i = 0; i <= 44; i++) {
            ItemStack inventoryItem = inventory.getItem(i);
            if (inventoryItem != null && inventoryItem.hasItemMeta() && inventoryItem.getItemMeta().getPersistentDataContainer().has(PluginKey.COMPASS.getNamespacedKey())) {
                inventory.remove(inventoryItem);
            }
        }
    }

    private void tearDown() {
        this.sheepHuntGoingOn = false;
        this.huntedPlayer = null;
        this.sheepCompassItem = null;
        this.currentDye = null;
        this.currentSheepToDye = null;
        this.scoreboard = null;
        this.sheepDyed = 0;
        this.netherSheep = false;
    }

    private Location getSheepSpawnLocation() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        boolean nether = random.nextInt(10) > 8.5;
        int randomX = random.nextInt(-sheepSpawnRadius, sheepSpawnRadius);
        int randomZ = random.nextInt(-sheepSpawnRadius, sheepSpawnRadius);

        Location playerLocation = huntedPlayer.getLocation();

        int sheepSpawnX;
        int sheepSpawnZ;

        if (nether) {
            netherSheep = true;
            this.huntedPlayer.sendMessage(Component.text("NETHER!").color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
            sheepSpawnX = playerLocation.getBlockX() / 8 + randomX;
            sheepSpawnZ = playerLocation.getBlockZ() / 8 + randomZ;
        } else {
            netherSheep = false;
            sheepSpawnX = playerLocation.getBlockX() + randomX;
            sheepSpawnZ = playerLocation.getBlockZ() + randomZ;
        }

        World spawnWorld = nether ? getNetherWorld() : getNormalWorld();

        System.out.println(spawnWorld.getEnvironment());

        loadChunkAt(spawnWorld, sheepSpawnX, sheepSpawnZ);
        int sheepSpawnY = nether ?
                getBlockYOfSheepSpawnInNether(spawnWorld, sheepSpawnX, sheepSpawnZ) :
                spawnWorld.getHighestBlockYAt(sheepSpawnX, sheepSpawnZ);

        Location location = new Location(spawnWorld, sheepSpawnX, sheepSpawnY + 1, sheepSpawnZ);
        huntedPlayer.teleport(location);
        return location;
    }

    private int getBlockYOfSheepSpawnInNether(World world, int sheepSpawnX, int sheepSpawnZ) {
        int highestBlock = world.getHighestBlockYAt(sheepSpawnX, sheepSpawnZ);
        for (int sheepSpawnY = 0; sheepSpawnY < highestBlock; sheepSpawnY += 2) {
            if (world.getBlockAt(sheepSpawnX, sheepSpawnY, sheepSpawnZ).isEmpty() && world.getBlockAt(sheepSpawnX, sheepSpawnY + 1, sheepSpawnZ).isEmpty()) {
                return sheepSpawnY;
            }
        }

        int alternativeSheepSpawnY = highestBlock - 20;
        world.getBlockAt(sheepSpawnX, alternativeSheepSpawnY, sheepSpawnZ).setType(AIR);
        world.getBlockAt(sheepSpawnX, alternativeSheepSpawnY + 1, sheepSpawnZ).setType(AIR);
        world.getBlockAt(sheepSpawnX, alternativeSheepSpawnY + 2, sheepSpawnZ).setType(AIR);
        return alternativeSheepSpawnY;
    }

    private World getNormalWorld() {
        return Bukkit.getWorlds().stream()
                .filter(world -> {
                    if(world.getEnvironment().equals(NORMAL)) {
                        System.out.println(world.getEnvironment());
                        System.out.println(world.getName());
                        return true;
                    } else {
                        return false;
                    }
                })
                .findFirst()
                .get();
    }

    private World getNetherWorld() {
        return Bukkit.getWorlds().stream()
                .filter(world -> {
                    if(world.getEnvironment().equals(NETHER)) {
                        System.out.println(world.getEnvironment());
                        System.out.println(world.getName());
                        return true;
                    } else {
                        return false;
                    }
                })
                .findFirst()
                .get();
    }

    private void loadChunkAt(World world, int x, int z) {
        Chunk chunk = new Location(world, x, z, 0).getChunk();
        if (!chunk.isLoaded()) {
            chunk.load();
        }
    }

    private Map<Material, Material> getDyesAndWools() {
        return Map.ofEntries(
                Map.entry(LIME_DYE, LIME_WOOL),
                Map.entry(PINK_DYE, PINK_WOOL),
                Map.entry(BLUE_DYE, BLUE_WOOL),
                Map.entry(BROWN_DYE, BROWN_WOOL),
                Map.entry(CYAN_DYE, CYAN_WOOL),
                Map.entry(ORANGE_DYE, ORANGE_WOOL),
                Map.entry(GREEN_DYE, GREEN_WOOL),
                Map.entry(PURPLE_DYE, PURPLE_WOOL),
                Map.entry(RED_DYE, RED_WOOL),
                Map.entry(MAGENTA_DYE, MAGENTA_WOOL),
                Map.entry(LIGHT_BLUE_DYE, LIGHT_BLUE_WOOL),
                Map.entry(YELLOW_DYE, YELLOW_WOOL)
        );
    }
}
