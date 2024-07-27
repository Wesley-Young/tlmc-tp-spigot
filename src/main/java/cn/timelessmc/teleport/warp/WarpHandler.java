package cn.timelessmc.teleport.warp;

import com.github.promeg.pinyinhelper.Pinyin;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


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
       HashMap <String, String> pinYinWarpList = getPinYinWarpList(this.warpEntryMap.keySet());
        return switch (args.length) {
            case 0 -> this.warpEntryMap.keySet().stream().toList();
//            case 1 -> this.warpEntryMap.keySet().stream().filter((s) -> s.startsWith(args[0])).toList();
            case 1 -> {
                String[] argss = pinYinWarpList.keySet().stream().filter((s) -> s.startsWith(args[0])).toArray(String[]::new);
                for (int i = 0; i < argss.length; i++) {
                    argss[i] = pinYinWarpList.get(argss[i]);
                }
                yield List.of(argss);
            }
            default -> this.empty;
        };
    }

    public HashMap<String, String> getPinYinWarpList(Set<String> warpList) {
        HashMap<String, String> pinYinWarpList = new HashMap<>();
        for(String warpName:warpList) {
            String[] pinyinWarpNames = Pinyin.toPinyin(warpName, ",").split(",");
            StringBuilder quanPin= new StringBuilder();
            StringBuilder jianPin= new StringBuilder();
            for (String pinyinWarpName : pinyinWarpNames) {
                quanPin.append(pinyinWarpName.toLowerCase());
                jianPin.append(pinyinWarpName.toLowerCase().charAt(0));
            }
            pinYinWarpList.put(quanPin.toString(), warpName);
            pinYinWarpList.put(jianPin.toString(), warpName);
        }
        return pinYinWarpList;
    }
}
