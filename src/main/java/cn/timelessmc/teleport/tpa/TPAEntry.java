package cn.timelessmc.teleport.tpa;

import org.bukkit.entity.Player;

import java.time.Instant;

public record TPAEntry(Instant timeStamp, Player from, Player to, int expirySeconds) {
    public boolean isExpired() {
        return Instant.now().isAfter(this.timeStamp.plusSeconds(this.expirySeconds));
    }

    public boolean canBeDeleted() {
        return Instant.now().isAfter(this.timeStamp.plusSeconds(this.expirySeconds + 10));
    }
}
