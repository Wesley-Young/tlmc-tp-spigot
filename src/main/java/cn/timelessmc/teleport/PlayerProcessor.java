package cn.timelessmc.teleport;

import cn.timelessmc.teleport.gui.BedrockGUIFactory;
import cn.timelessmc.teleport.gui.SnowballCommandHandler;
import cn.timelessmc.teleport.home.HomeEntrySubMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerProcessor implements Listener {
    private final Map<String, HomeEntrySubMap> rootMap;
    private final FloodgateApi floodgateApi = FloodgateApi.getInstance();

    public PlayerProcessor(Map<String, HomeEntrySubMap> rootMap) {
        this.rootMap = rootMap;
    }

    @EventHandler
    public void playerLogged(@NotNull PlayerLoginEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        Path homesRoot = Paths.get("plugins", "tlmc-tp-spigot", "homes");
        Path homesDir = homesRoot.resolve(playerUUID + ".homes");

        try {
            if (Files.notExists(homesDir)) {
                if (Files.notExists(homesRoot)) {
                    Files.createDirectories(homesRoot);
                }

                Files.createFile(homesDir);
                this.rootMap.put(playerUUID.toString(), new HomeEntrySubMap());
                return;
            }

            Properties homes = new Properties();
            homes.load(Files.newInputStream(homesDir));
            this.rootMap.put(playerUUID.toString(), new HomeEntrySubMap(homes));
        } catch (Exception var6) {
            Bukkit.getLogger().log(Level.SEVERE, "An ERROR occurred when loading one player's home list.");
            Bukkit.getLogger().log(Level.SEVERE, "As a result, this player CANNOT call functions about homes.");
        }

    }

    @EventHandler
    public void playerQuited(@NotNull PlayerQuitEvent event) {
        String playerUUID = event.getPlayer().getUniqueId().toString();
        Path homesRoot = Paths.get("plugins", "tlmc-tp-spigot", "homes");
        Path homesDir = homesRoot.resolve(playerUUID + ".homes");

        try {
            HomeEntrySubMap subMap = this.rootMap.get(playerUUID);
            subMap.toProperties().store(Files.newOutputStream(homesDir), event.getPlayer().getName() + "'s home list. Don't modify!");
            this.rootMap.remove(event.getPlayer().getUniqueId().toString());
        } catch (IOException var6) {
            Bukkit.getLogger().log(Level.SEVERE, "An ERROR occurred when saving " + event.getPlayer().getName() + "'s home list.");
            Bukkit.getLogger().log(Level.SEVERE, "This player's data still remains in the RAM.");
        }

    }

    @EventHandler
    public void playerRespawned(@NotNull PlayerRespawnEvent event) {
        if (this.floodgateApi.isFloodgatePlayer(event.getPlayer().getUniqueId())) {
            event.getPlayer().getInventory().addItem(SnowballCommandHandler.SNOWBALL);
        }

    }

    @EventHandler
    public void mysterySnowballUsed(@NotNull ProjectileLaunchEvent event) {
        Projectile var4 = event.getEntity();
        if (var4 instanceof Snowball snowball) {
            ProjectileSource var5 = snowball.getShooter();
            if (var5 instanceof Player player) {
                if (this.floodgateApi.isFloodgatePlayer(player.getUniqueId())) {
                    FloodgatePlayer floodgatePlayer = this.floodgateApi.getPlayer(player.getUniqueId());
                    if (SnowballCommandHandler.SNOWBALL_META.equals(snowball.getItem().getItemMeta())) {
                        event.setCancelled(true);
                        if (player.isOp()) {
                            floodgatePlayer.sendForm(BedrockGUIFactory.getInstance().createRootFormForOP(floodgatePlayer));
                        } else {
                            floodgatePlayer.sendForm(BedrockGUIFactory.getInstance().createRootForm(floodgatePlayer));
                        }
                    }
                }
            }
        }

    }
}
