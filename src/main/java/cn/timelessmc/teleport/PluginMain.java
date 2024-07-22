package cn.timelessmc.teleport;

import cn.timelessmc.teleport.gui.BedrockGUIFactory;
import cn.timelessmc.teleport.gui.SnowballCommandHandler;
import cn.timelessmc.teleport.home.DelHomeHandler;
import cn.timelessmc.teleport.home.HomeEntrySubMap;
import cn.timelessmc.teleport.home.HomeHandler;
import cn.timelessmc.teleport.home.SetHomeHandler;
import cn.timelessmc.teleport.tpa.TPAEntry;
import cn.timelessmc.teleport.tpa.TPAHandler;
import cn.timelessmc.teleport.tpa.TPAcceptHandler;
import cn.timelessmc.teleport.tpa.TPDenyHandler;
import cn.timelessmc.teleport.warp.DelWarpHandler;
import cn.timelessmc.teleport.warp.WarpEntryMap;
import cn.timelessmc.teleport.warp.WarpHandler;
import cn.timelessmc.teleport.warp.SetWarpHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.swing.Timer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;

public class PluginMain extends JavaPlugin {
    private WarpEntryMap warpEntryMap;
    private Timer autoSaver;

    public void onEnable() {
        this.getLogger().log(Level.INFO, "Loading system config...");
        FileConfiguration sysConfig = this.getConfig();
        Object maxHomes = sysConfig.get("max-homes");
        if (maxHomes == null | !(maxHomes instanceof Integer)) {
            sysConfig.set("max-homes", 16);
        }

        Object tpaExpirySeconds = sysConfig.get("tpa-expiry-seconds");
        if (tpaExpirySeconds == null | !(tpaExpirySeconds instanceof Integer)) {
            sysConfig.set("tpa-expiry-seconds", 30);
        }

        Object warpAutoSavingIntervalSeconds = sysConfig.get("warp-auto-saving-interval-seconds");
        if (warpAutoSavingIntervalSeconds == null | !(warpAutoSavingIntervalSeconds instanceof Integer)) {
            sysConfig.set("warp-auto-saving-interval-seconds", 600);
        }

        List<String> warpTypes = sysConfig.getStringList("warp-types");
        if (warpTypes.isEmpty()) {
            warpTypes = List.of("nature", "machine", "structure");
            sysConfig.set("warp-types", warpTypes);
        }

        this.getLogger().log(Level.INFO, "Loading warp list...");
        Properties warps = new Properties();
        Path configDir = Paths.get("plugins", "tlmc-tp-spigot");
        Path warpsDir = configDir.resolve("warps");

        try {
            if (!Files.isDirectory(configDir)) {
                Files.createDirectories(configDir);
            }

            if (!Files.exists(warpsDir)) {
                Files.createFile(warpsDir);
            }

            warps.load(Files.newInputStream(warpsDir));
            this.warpEntryMap = new WarpEntryMap(warps, warpTypes);
            this.registerCommandWithTabCompleter("warp", new WarpHandler(this.warpEntryMap));
            this.registerCommandWithTabCompleter("setwarp", new SetWarpHandler(this.warpEntryMap));
            this.registerCommandWithTabCompleter("delwarp", new DelWarpHandler(this.warpEntryMap));
            this.autoSaver = new Timer(sysConfig.getInt("warp-auto-saving-interval-seconds"), (event) -> {
                try {
                    this.warpEntryMap.saveToStream(Files.newOutputStream(Paths.get("plugins", "tlmc-tp-spigot", "warps")));
                } catch (IOException var3) {
                    throw new RuntimeException(var3);
                }
            });
            this.autoSaver.start();
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "An ERROR occurred when loading warp list.");
            this.getLogger().log(Level.SEVERE, "As a result, functions about warps are DISABLED.");
        }

        this.getLogger().log(Level.INFO, "Initializing home list...");
        Map<String, HomeEntrySubMap> homeEntryRootMap = new LinkedHashMap<>();
        HomeHandler homeHandler = new HomeHandler(homeEntryRootMap);
        SetHomeHandler setHomeHandler = new SetHomeHandler(homeEntryRootMap, sysConfig.getInt("max-homes"));
        DelHomeHandler delHomeHandler = new DelHomeHandler(homeEntryRootMap);
        this.registerCommandWithTabCompleter("home", homeHandler);
        this.registerCommandWithTabCompleter("sethome", setHomeHandler);
        this.registerCommandWithTabCompleter("delhome", delHomeHandler);
        this.getLogger().log(Level.INFO, "Initializing /tpa functions...");
        List<TPAEntry> tpaEntries = Collections.synchronizedList(new LinkedList<>());
        TPAHandler tpaHandler = new TPAHandler(tpaEntries, sysConfig.getInt("tpa-expiry-seconds"));
        TPAcceptHandler tpAcceptHandler = new TPAcceptHandler(tpaEntries);
        TPDenyHandler tpDenyHandler = new TPDenyHandler(tpaEntries);
        this.registerCommand("tpa", tpaHandler);
        this.registerCommand("tpaccept", tpAcceptHandler);
        this.registerCommand("tpdeny", tpDenyHandler);
        new BedrockGUIFactory(this.warpEntryMap, homeEntryRootMap);
        this.registerCommand("snowball", new SnowballCommandHandler());
        Bukkit.getPluginManager().registerEvents(new PlayerProcessor(homeEntryRootMap), this);
    }

    public void onDisable() {
        this.getLogger().log(Level.INFO, "Saving system config...");
        this.saveConfig();
        this.getLogger().log(Level.INFO, "Saving warp lists...");
        this.autoSaver.stop();

        try {
            this.warpEntryMap.saveToStream(Files.newOutputStream(Paths.get("plugins", "tlmc-tp-spigot", "warps")));
        } catch (Exception var2) {
            this.getLogger().log(Level.SEVERE, "An ERROR occurred when saving warp list.");
        }

    }

    private void registerCommand(String cmdName, CommandExecutor executor) {
        PluginCommand cmd = Bukkit.getPluginCommand(cmdName);
        if (cmd != null) {
            cmd.setExecutor(executor);
        }

    }

    private void registerCommandWithTabCompleter(String cmdName, TabExecutor executor) {
        PluginCommand cmd = Bukkit.getPluginCommand(cmdName);
        if (cmd != null) {
            cmd.setExecutor(executor);
            cmd.setTabCompleter(executor);
        }

    }
}
