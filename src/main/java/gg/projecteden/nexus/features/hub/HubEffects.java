package gg.projecteden.nexus.features.hub;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.nexus.features.effects.Effects;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class HubEffects extends Effects {

	@Override
	public void onEnterRegion(Player player) {
		PotionEffect unluck = new PotionEffectBuilder()
			.type(PotionEffectType.UNLUCK)
			.maxDuration()
			.particles(false)
			.ambient(false)
			.build();

		player.removePotionEffect(PotionEffectType.UNLUCK);
		player.addPotionEffect(unluck);
	}

	@Override
	public void onExitRegion(Player player) {
		player.removePotionEffect(PotionEffectType.UNLUCK);
	}

	@Override
	public void particles() {
		List<ParticleBuilder> particles = List.of(
			new ParticleBuilder(Particle.DRIPPING_DRIPSTONE_WATER)
				.location(loc(100.5, 137.9, 39.5))
				.offset(0.15, 0, 0.15),

			new ParticleBuilder(Particle.WATER_SPLASH)
				.location(loc(81.5, 130.3, 60.5))
				.offset(0.15, 0, 0.15)

		);

		Tasks.repeat(0, 2, () -> {
			for (ParticleBuilder particleBuilder : particles) {
				final Location location = particleBuilder.location();
				if (location == null || !location.isChunkLoaded())
					continue;

				if (RandomUtils.chanceOf(50))
					if (hasPlayersNearby(location, 25))
						particleBuilder.spawn();
			}
		});
	}

}
