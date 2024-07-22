package cn.timelessmc.teleport.home;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class SetHomeHandler implements TabExecutor {
    private final int maxHomes;
    private final Map<String, HomeEntrySubMap> rootMap;
    private final List<String> empty = List.of();

    public SetHomeHandler(Map<String, HomeEntrySubMap> rootMap, int maxHomes) {
        this.maxHomes = maxHomes;
        this.rootMap = rootMap;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (sender instanceof Player executor) {
            if (args.length != 1) {
                return false;
            } else {
                HomeEntrySubMap subMap = this.rootMap.get(executor.getUniqueId().toString());
                if (subMap.containsKey(args[0])) {
                    executor.sendRawMessage("§6" + args[0] + " §calready exists!");
                    return true;
                } else if (subMap.size() >= this.maxHomes) {
                    executor.sendRawMessage("§cYou can have " + this.maxHomes + " homes at most!");
                    return true;
                } else {
                    subMap.put(args[0], new HomeEntry(executor.getWorld(), executor.getLocation().getBlockX(), executor.getLocation().getBlockY(), executor.getLocation().getBlockZ()));
                    executor.sendRawMessage("§aSuccessfully added §6" + args[0]);
                    return true;
                }
            }
        } else {
            return false;
        }
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        } else {
            return args.length > 1 ? this.empty : List.of("<name>");
        }
    }
}
