package cn.timelessmc.teleport.gui;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;

public class SnowballCommandHandler implements CommandExecutor {
    public static final ItemStack SNOWBALL;
    public static final ItemMeta SNOWBALL_META;

    static {
        SNOWBALL = new ItemStack(Material.SNOWBALL);
        ItemMeta snowballMeta = SNOWBALL.getItemMeta();

        assert snowballMeta != null;

        snowballMeta.setDisplayName("Mystery Ball");
        snowballMeta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
        SNOWBALL_META = snowballMeta;
        SNOWBALL.setItemMeta(SNOWBALL_META);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
                player.getInventory().addItem(SNOWBALL);
            }
        }

        return true;
    }
}
