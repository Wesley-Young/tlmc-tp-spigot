package cn.timelessmc.teleport.home;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HomeHandler implements TabExecutor {
    private final Map<String, HomeEntrySubMap> rootMap;
    private final List<String> empty = List.of();

    public HomeHandler(Map<String, HomeEntrySubMap> rootMap) {
        this.rootMap = rootMap;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (sender instanceof Player executor) {
            if (args.length != 1) {
                return false;
            } else {
                HomeEntrySubMap subMap = this.rootMap.get(executor.getUniqueId().toString());
                if (!subMap.containsKey(args[0])) {
                    executor.sendRawMessage("§cYou do not have such a home §6" + args[0]);
                } else {
                    HomeEntry entry = subMap.get(args[0]);
                    executor.teleport(new Location(entry.world(), entry.x(), entry.y(), entry.z()));
                    executor.sendRawMessage("§aSuccessfully teleported you to §6" + args[0]);
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
                    case 1 ->
                            subMap.keySet().stream().filter((s) -> s.startsWith(args[0])).collect(Collectors.toList());
                    default -> this.empty;
                };
            }
        } else {
            return null;
        }
    }
}
