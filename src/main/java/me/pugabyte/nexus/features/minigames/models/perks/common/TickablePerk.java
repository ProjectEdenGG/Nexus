package me.pugabyte.nexus.features.minigames.models.perks.common;

import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.perks.Perk;
import me.pugabyte.nexus.features.particles.ParticleUtils;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public abstract class TickablePerk extends Perk {
	public void tick(Minigamer minigamer) {
		tick(minigamer.getPlayer());
	}

	public abstract void tick(Player player);

	public static void particle(Player player, Particle particle) {
		particle(player, particle, 1d);
	}

	public static void particle(Player player, Particle particle, double speed) {
		// don't play particles on sneaking players
		if (player.isSneaking())
			return;

		ParticleUtils.display(particle, player.getLocation().add(0, 0.5, 0), 5, .15d, .7d, .15d, speed);
	}
}
