package me.pugabyte.nexus.features.minigames.models.perks.common;

import com.destroystokyo.paper.ParticleBuilder;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.perks.Perk;
import me.pugabyte.nexus.features.minigames.models.perks.PerkOwner;
import me.pugabyte.nexus.features.minigames.models.perks.PerkOwnerService;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public abstract class TickablePerk extends Perk {
	public void tick(Minigamer minigamer) {
		tick(minigamer.getPlayer());
	}

	public abstract void tick(Player player);

	public void particle(Player player, Particle particle, List<Player> recipients) {
		particle(player, particle, 1d, recipients);
	}

	public void particle(Player player, Particle particle, double speed, List<Player> recipients) {
		// don't play particles on sneaking players
		if (player.isSneaking())
			return;

		particle(player.getLocation().add(0, 0.5, 0), particle, speed, recipients);
	}

	public void particle(Location location, Particle particle, double speed, List<Player> recipients) {
		recipients = recipients.stream().filter(player -> {
			PerkOwner owner = new PerkOwnerService().get(player);
			return owner.getHideParticle().showParticle(getPerkCategory());
		}).collect(Collectors.toList());
		new ParticleBuilder(particle).receivers(recipients).count(5).offset(.15, .7, .15).location(location).extra(speed).spawn();
	}
}
