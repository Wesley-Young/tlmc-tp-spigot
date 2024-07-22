package cn.timelessmc.teleport.warp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class SetWarpHandler implements TabExecutor {
    private final List<String> types;
    private final WarpEntryMap warpEntryMap;
    private final List<String> l2 = List.of("<name>");
    private final List<String> l3 = List.of("<description>");

    public SetWarpHandler(@NotNull WarpEntryMap warpEntryMap) {
        this.warpEntryMap = warpEntryMap;
        this.types = warpEntryMap.getTypes();
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (sender instanceof Player executor) {
            if (!sender.isOp()) {
                return false;
            } else if (args.length != 3) {
                return false;
            } else if (!this.types.contains(args[0])) {
                executor.sendRawMessage("§cBad type!");
                return true;
            } else if (this.warpEntryMap.containsKey(args[1])) {
                executor.sendRawMessage("§6" + args[1] + " §calready exists!");
                return true;
            } else {
                WarpEntry entry = new WarpEntry(args[0], executor.getName(), executor.getWorld(), executor.getLocation().getBlockX(), executor.getLocation().getBlockY(), executor.getLocation().getBlockZ(), args[2]);
                this.warpEntryMap.put(args[1], entry);
                executor.sendRawMessage("§aSuccessfully added §6" + args[1]);
                return true;
            }
        } else {
            return false;
        }
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        } else if (!sender.isOp()) {
            return null;
        } else {
            return switch (args.length) {
                case 0 -> this.types;
                case 1 -> this.types.contains(args[0]) ? null : this.types.stream().filter((s) -> s.startsWith(args[0])).collect(Collectors.toList());
                case 2 -> this.l2;
                case 3 -> this.l3;
                default -> null;
            };
        }
    }
}
