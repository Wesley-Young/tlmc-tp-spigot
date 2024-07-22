package cn.timelessmc.teleport.warp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DelWarpHandler implements TabExecutor {
    private final WarpEntryMap warpEntryMap;
    private final List<String> empty = List.of();

    public DelWarpHandler(WarpEntryMap warpEntryMap) {
        this.warpEntryMap = warpEntryMap;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (sender instanceof Player executor) {
            if (args.length != 1) {
                return false;
            } else {
                if (!this.warpEntryMap.containsKey(args[0])) {
                    executor.sendRawMessage("§6" + args[0] + " §cdoes not exist!");
                }

                this.warpEntryMap.remove(args[0]);
                executor.sendRawMessage("§aSuccessfully deleted §6" + args[0]);
                return true;
            }
        } else {
            return false;
        }
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        return switch (args.length) {
            case 0 -> this.warpEntryMap.keySet().stream().toList();
            case 1 -> this.warpEntryMap.keySet().stream().filter((s) -> s.startsWith(args[0])).toList();
            default -> this.empty;
        };
    }
}
