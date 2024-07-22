package cn.timelessmc.teleport.warp;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WarpHandler implements TabExecutor {
    private final WarpEntryMap warpEntryMap;
    private final List<String> empty = List.of();

    public WarpHandler(WarpEntryMap warpEntryMap) {
        this.warpEntryMap = warpEntryMap;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (sender instanceof Player executor) {
            if (args.length != 1) {
                return false;
            } else if (!this.warpEntryMap.containsKey(args[0])) {
                executor.sendRawMessage("§cThere is not such a warp entry §6" + args[0]);
                return true;
            } else {
                WarpEntry entry = this.warpEntryMap.get(args[0]);
                executor.teleport(new Location(entry.world(), entry.x(), entry.y(), entry.z()));
                executor.sendRawMessage("§aSuccessfully teleported you to §6" + args[0]);
                return true;
            }
        } else {
            return false;
        }
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        return switch (args.length) {
            case 0 -> this.warpEntryMap.keySet().stream().toList();
            case 1 -> this.warpEntryMap.keySet().stream().filter((s) -> s.startsWith(args[0])).toList();
            default -> this.empty;
        };
    }
}
