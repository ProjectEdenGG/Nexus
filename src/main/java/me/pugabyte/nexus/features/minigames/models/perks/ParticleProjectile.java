package me.pugabyte.nexus.features.minigames.models.perks;

import lombok.Data;
import me.pugabyte.nexus.features.minigames.models.perks.common.ParticleProjectilePerk;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.entity.Projectile;

@Data
public class ParticleProjectile {
    private final ParticleProjectilePerk perk;
    private final Projectile projectile;
    private final int taskId;
    public ParticleProjectile(ParticleProjectilePerk perk, Projectile projectile) {
        this.perk = perk;
        this.projectile = projectile;
        this.taskId = Tasks.repeat(1, 1, () -> {
            if (!projectile.isValid() || projectile.isDead() || projectile.isOnGround()) {
                Tasks.cancel(getTaskId());
                return;
            }
            perk.tick(projectile);
        });
    }
}
