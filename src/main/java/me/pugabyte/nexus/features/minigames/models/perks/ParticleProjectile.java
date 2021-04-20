package me.pugabyte.nexus.features.minigames.models.perks;

import lombok.Data;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.perks.common.ParticleProjectilePerk;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;

@Data
public class ParticleProjectile {
	private final ParticleProjectilePerk perk;
	private final Projectile projectile;
	private final Match match;

	public ParticleProjectile(ParticleProjectilePerk perk, Projectile projectile, Match match) {
		this.perk = perk;
		this.projectile = projectile;
		this.match = match;
		Tasks.repeat(1, 1, new BukkitRunnable() {
			@Override
			public void run() {
				if (!projectile.isValid() || projectile.isDead() || projectile.isOnGround()) {
					cancel();
					return;
				}
				perk.tick(projectile, match);
			}
		});
	}

}
