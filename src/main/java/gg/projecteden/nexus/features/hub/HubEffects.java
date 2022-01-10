package gg.projecteden.nexus.features.hub;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.nexus.features.effects.Effects;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import java.util.List;

public class HubEffects extends Effects {

	@Override
	public World getWorld() {
		return Bukkit.getWorld("server");
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
					if (hasNearbyPlayers(25, location))
						particleBuilder.spawn();
			}
		});
	}

}
