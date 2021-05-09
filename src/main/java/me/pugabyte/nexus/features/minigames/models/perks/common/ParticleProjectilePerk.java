package me.pugabyte.nexus.features.minigames.models.perks.common;

import com.destroystokyo.paper.ParticleBuilder;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.perks.Perk;
import me.pugabyte.nexus.features.minigames.models.perks.PerkCategory;
import me.pugabyte.nexus.models.perkowner.PerkOwner;
import me.pugabyte.nexus.models.perkowner.PerkOwnerService;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public abstract class ParticleProjectilePerk extends Perk implements IParticlePerk {
	public Particle.DustOptions getDustOptions(@NotNull Projectile projectile) {
		return null;
	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public double getOffsetH() {
		return .02;
	}

	@Override
	public double getOffsetV() {
		return getOffsetH();
	}

	@Override
	public PerkCategory getPerkCategory() {
		return PerkCategory.ARROW_TRAIL;
	}

	public void tick(Projectile projectile, List<Player> players) {
		players = players.stream().filter(player -> {
			PerkOwner owner = new PerkOwnerService().get(player);
			return owner.getHideParticle().showParticle(getPerkCategory());
		}).collect(Collectors.toList());
		new ParticleBuilder(getParticle()).receivers(players).count(getCount()).offset(getOffsetH(), getOffsetV(), getOffsetH()).location(projectile.getLocation()).extra(getSpeed()).data(getDustOptions(projectile)).spawn();
	}

	public void tick(Projectile projectile, Match match) {
		tick(projectile, match.getMinigamers().stream().map(Minigamer::getPlayer).collect(Collectors.toList()));
	}
}
