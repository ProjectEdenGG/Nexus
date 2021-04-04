package me.pugabyte.nexus.features.minigames.models.perks.common;

import com.destroystokyo.paper.ParticleBuilder;
import me.pugabyte.nexus.features.minigames.models.perks.PerkCategory;
import me.pugabyte.nexus.features.minigames.models.perks.PerkOwner;
import me.pugabyte.nexus.features.minigames.models.perks.PerkOwnerService;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public abstract class ParticlePerk extends TickablePerk {
	@Override
	public PerkCategory getPerkCategory() {
		return PerkCategory.PARTICLE;
	}

	public abstract Particle getParticle();
	public double getSpeed() {
		return 1;
	}
	public Particle.DustOptions getDustOptions(Player player) {
		return null;
	}
	public int getCount() {
		return 5;
	}

	@Override
	public void tick(Player player) {
		particle(player, player.getWorld().getPlayers());
	}

	public void particle(Player player, List<Player> recipients) {
		if (player.isSneaking())
			return;
		Location location = player.getLocation().add(0, 0.5, 0);

		recipients = recipients.stream().filter(player1 -> {
			PerkOwner owner = new PerkOwnerService().get(player);
			return owner.getHideParticle().showParticle(getPerkCategory());
		}).collect(Collectors.toList());
		new ParticleBuilder(getParticle()).receivers(recipients).count(getCount()).offset(.15, .7, .15).location(location).extra(getSpeed()).data(getDustOptions(player)).spawn();
	}
}
