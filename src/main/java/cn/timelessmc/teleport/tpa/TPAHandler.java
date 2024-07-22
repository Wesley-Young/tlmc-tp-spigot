package cn.timelessmc.teleport.tpa;

import cn.timelessmc.teleport.gui.BedrockGUIFactory;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;

public class TPAHandler implements CommandExecutor {
    private final List<TPAEntry> requestsList;
    private final int expirySeconds;

    public TPAHandler(List<TPAEntry> requestsList, int expirySeconds) {
        this.requestsList = requestsList;
        this.expirySeconds = expirySeconds;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (sender instanceof Player executor) {
            if (args.length != 1) {
                return false;
            } else {
                Player targetPlayer = Bukkit.getPlayer(args[0]);
                if (targetPlayer == null) {
                    executor.sendRawMessage("§cNo such player named §6" + args[0]);
                } else {
                    for (TPAEntry entry : requestsList.stream().filter((entry) -> entry.to() == targetPlayer).toList()) {
                        if (!entry.isExpired()) {
                            executor.sendRawMessage("§a" + targetPlayer.getName() + " had been requested by another player.");
                            executor.sendRawMessage("§aPlease wait for a moment.");
                            return true;
                        }
                        if (entry.canBeDeleted()) {
                            this.requestsList.remove(entry);
                        }
                    }

                    if (FloodgateApi.getInstance().isFloodgatePlayer(targetPlayer.getUniqueId())) {
                        BedrockGUIFactory factory = BedrockGUIFactory.getInstance();
                        FloodgatePlayer aTarget = factory.toFloodgatePlayer(targetPlayer);
                        aTarget.sendForm(factory.createTPAConfirmationForm(aTarget, executor.getName()));
                    }

                    this.requestsList.add(new TPAEntry(Instant.now(), executor, targetPlayer, this.expirySeconds));
                    targetPlayer.sendRawMessage("§ePlayer §6" + executor.getName() + " §esent a request to teleport to you.");
                    targetPlayer.sendRawMessage("§eTo accept, enter §a/tpaccept §ewithin " + this.expirySeconds + " seconds;");
                    targetPlayer.sendRawMessage("§eTo deny, enter §c/tpdeny.");
                }
                return true;
            }
        } else {
            return false;
        }
    }
}
