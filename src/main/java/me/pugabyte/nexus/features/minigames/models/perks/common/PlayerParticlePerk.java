package me.pugabyte.nexus.features.minigames.models.perks.common;

import com.destroystokyo.paper.ParticleBuilder;
import me.pugabyte.nexus.features.minigames.models.perks.PerkCategory;
import me.pugabyte.nexus.models.perkowner.PerkOwner;
import me.pugabyte.nexus.models.perkowner.PerkOwnerService;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public interface PlayerParticlePerk extends TickablePerk, IParticlePerk {
	default int getCount() {
		return 5;
	}

	@Override
	default @NotNull PerkCategory getPerkCategory() {
		return PerkCategory.PARTICLE;
	}

	default Particle.DustOptions getDustOptions(@NotNull Player player) {
		return null;
	}

	@Override
	default double getOffsetH() {
		return .15;
	}

	@Override
	default double getOffsetV() {
		return .7;
	}

	@Override
	default void tick(Player player) {
		particle(player, player.getWorld().getPlayers());
	}

	default void particle(Player player, List<Player> recipients) {
		if (player.isSneaking())
			return;

		Location location = player.getLocation().add(0, 0.5, 0);

		recipients = recipients.stream().filter(player1 -> {
			PerkOwner owner = new PerkOwnerService().get(player1);
			return owner.getHideParticle().showParticle(getPerkCategory());
		}).collect(Collectors.toList());

		new ParticleBuilder(getParticle())
				.receivers(recipients)
				.count(getCount())
				.offset(getOffsetH(), getOffsetV(), getOffsetH())
				.location(location)
				.extra(getSpeed())
				.data(getDustOptions(player))
				.spawn();
	}
}
