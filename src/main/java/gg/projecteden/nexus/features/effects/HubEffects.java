package gg.projecteden.nexus.features.effects;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HubEffects extends Effects {

	@Override
	public void particles() {
		ParticleBuilder creative_splash = new ParticleBuilder(Particle.WATER_SPLASH)
			.location(loc(81.5, 130.3, 60.5))
			.offset(0.15, 0, 0.15);

		ParticleBuilder creative_drip = new ParticleBuilder(Particle.DRIPPING_DRIPSTONE_WATER)
			.location(loc(100.5, 137.9, 39.5))
			.offset(0.15, 0, 0.15);

		List<ParticleBuilder> particles = new ArrayList<>(Arrays.asList(creative_drip, creative_splash));

		Tasks.repeat(0, 2, () -> {
			for (ParticleBuilder particleBuilder : particles) {
				if (RandomUtils.chanceOf(50)) {
					if (hasNearbyPlayers(25, particleBuilder.location()))
						particleBuilder.spawn();
				}
			}
		});
	}

	@Override
	public World getWorld() {
		return Bukkit.getWorld("server");
	}


}
