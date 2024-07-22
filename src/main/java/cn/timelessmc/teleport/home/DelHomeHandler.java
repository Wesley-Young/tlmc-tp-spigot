package cn.timelessmc.teleport.home;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class DelHomeHandler implements TabExecutor {
    private final Map<String, HomeEntrySubMap> rootMap;
    private final List<String> empty = List.of();

    public DelHomeHandler(Map<String, HomeEntrySubMap> rootMap) {
        this.rootMap = rootMap;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (sender instanceof Player executor) {
            if (args.length != 1) {
                return false;
            } else {
                HomeEntrySubMap subMap = this.rootMap.get(executor.getUniqueId().toString());
                if (!subMap.containsKey(args[0])) {
                    executor.sendRawMessage("§6" + args[0] + " §cdoes not exist!");
                } else {
                    subMap.remove(args[0]);
                    executor.sendRawMessage("§aSuccessfully deleted §6" + args[0]);
                }
                return true;
            }
        } else {
            return false;
        }
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (sender instanceof Player executor) {
            HomeEntrySubMap subMap = this.rootMap.get(executor.getUniqueId().toString());
            if (subMap == null) {
                return null;
            } else {
                return switch (args.length) {
                    case 0 -> subMap.keySet().stream().toList();
                    case 1 -> subMap.keySet().stream().filter((s) -> s.startsWith(args[0])).toList();
                    default -> this.empty;
                };
            }
        } else {
            return null;
        }
    }
}
