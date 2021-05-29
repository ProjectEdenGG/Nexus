package me.pugabyte.nexus.features.minigames.models.perks.common;

import com.destroystokyo.paper.ParticleBuilder;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.perks.PerkCategory;
import me.pugabyte.nexus.models.perkowner.PerkOwner;
import me.pugabyte.nexus.models.perkowner.PerkOwnerService;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public interface ParticleProjectilePerk extends IParticlePerk {
	default Particle.DustOptions getDustOptions(@NotNull Projectile projectile) {
		return null;
	}

	@Override
	default int getCount() {
		return 2;
	}

	@Override
	default double getOffsetH() {
		return .02;
	}

	@Override
	default double getOffsetV() {
		return getOffsetH();
	}

	@Override
	default @NotNull PerkCategory getPerkCategory() {
		return PerkCategory.ARROW_TRAIL;
	}

	default void tick(Projectile projectile, List<Player> players) {
		players = players.stream().filter(player -> {
			PerkOwner owner = new PerkOwnerService().get(player);
			return owner.getHideParticle().showParticle(getPerkCategory());
		}).collect(Collectors.toList());

		new ParticleBuilder(getParticle())
				.receivers(players)
				.count(getCount())
				.offset(getOffsetH(), getOffsetV(), getOffsetH())
				.location(projectile.getLocation())
				.extra(getSpeed())
				.data(getDustOptions(projectile))
				.spawn();
	}

	default void tick(Projectile projectile, Match match) {
		tick(projectile, match.getMinigamers().stream()
				.map(Minigamer::getPlayer)
				.collect(Collectors.toList()));
	}
}
