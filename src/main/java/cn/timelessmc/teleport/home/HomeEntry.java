package cn.timelessmc.teleport.home;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record HomeEntry(World world, int x, int y, int z) {
    public @NotNull String toString() {
        return this.world.getName() + " " + this.x + " " + this.y + " " + this.z;
   }

   @Contract("_ -> new")
   public static @NotNull HomeEntry parse(@NotNull String str) {
      String[] args = str.split(" ", 4);
      if (args.length != 4) {
         throw new IllegalArgumentException();
      } else {
         World world = Bukkit.getWorld(args[0]);
         if (world == null) {
            throw new IllegalArgumentException();
         } else {
            return new HomeEntry(world, Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
         }
      }
   }
}
