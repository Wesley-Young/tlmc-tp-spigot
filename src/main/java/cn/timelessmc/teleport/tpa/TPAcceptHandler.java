package cn.timelessmc.teleport.tpa;

import java.util.List;
import java.util.Optional;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TPAcceptHandler implements CommandExecutor {
    private final List<TPAEntry> requestsList;

    public TPAcceptHandler(List<TPAEntry> requestsList) {
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
                } else {
                    TPAEntry entry = pull.orElseThrow();
                    if (entry.isExpired()) {
                        executor.sendRawMessage("§cThe request has expired!");
                        this.requestsList.remove(entry);
                    } else {
                        entry.from().teleport(entry.to());
                        this.requestsList.remove(entry);
                        entry.from().sendRawMessage("§aSuccessfully teleported you to §6" + executor.getName());
                        executor.sendRawMessage("§aSuccessfully teleported §6" + entry.from().getName() + " §ato you");
                    }
                }
                return true;
            }
        } else {
            return false;
        }
    }
}
