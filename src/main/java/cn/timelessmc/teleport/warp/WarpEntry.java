package cn.timelessmc.teleport.warp;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record WarpEntry(String type, String creator, World world, int x, int y, int z, String description) {
    @Contract("_ -> new")
    public static @NotNull WarpEntry parse(@NotNull String str) {
        String[] args = str.split(" ", 7);
        if (args.length != 7) {
            throw new IllegalArgumentException();
        } else {
            World world = Bukkit.getWorld(args[2]);
            if (world == null) {
                throw new IllegalArgumentException();
            } else {
                return new WarpEntry(args[0], args[1], world, Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]), args[6]);
            }
        }
    }

    public @NotNull String toString() {
        return this.type + " " + this.creator + " " + this.world.getName() + " " + this.x + " " + this.y + " " + this.z + " " + this.description;
    }
}
