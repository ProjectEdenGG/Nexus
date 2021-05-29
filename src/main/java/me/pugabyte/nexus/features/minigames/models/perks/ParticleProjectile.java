package me.pugabyte.nexus.features.minigames.models.perks;

import lombok.Data;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.perks.common.ParticleProjectilePerk;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.entity.Projectile;

@Data
public class ParticleProjectile {
	private final ParticleProjectilePerk perk;
	private final Projectile projectile;
	private final int taskId;
	private final Match match;

	public ParticleProjectile(ParticleProjectilePerk perk, Projectile projectile, Match match) {
		this.perk = perk;
		this.projectile = projectile;
		this.match = match;
		this.taskId = Tasks.repeat(1, 1, () -> {
			if (!projectile.isValid() || projectile.isDead() || projectile.isOnGround()) {
				Tasks.cancel(getTaskId());
				return;
			}
			perk.tick(projectile, match);
		});
	}

}
