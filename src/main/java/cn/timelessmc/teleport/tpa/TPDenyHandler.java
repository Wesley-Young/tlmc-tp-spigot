package cn.timelessmc.teleport.tpa;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class TPDenyHandler implements CommandExecutor {
    private final List<TPAEntry> requestsList;

    public TPDenyHandler(List<TPAEntry> requestsList) {
        this.requestsList = requestsList;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (sender instanceof Player executor) {
            if (args.length != 0) {
                return false;
            } else {
                Optional<TPAEntry> pull = this.requestsList.stream().filter((entry) -> entry.to() == executor).findAny();
                if (pull.isEmpty()) {
                    executor.sendRawMessage("§cYou do not have a request!");
                    return true;
                } else {
                    TPAEntry entry = pull.orElseThrow();
                    this.requestsList.remove(entry);
                    executor.sendRawMessage("§aDenied request from §6" + entry.from().getName());
                    entry.from().sendRawMessage("§cYour request is turned down by §6" + executor.getName());
                    return true;
                }
            }
        } else {
            return false;
        }
    }
}
