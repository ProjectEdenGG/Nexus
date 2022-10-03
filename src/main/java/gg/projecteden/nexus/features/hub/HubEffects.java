package gg.projecteden.nexus.features.hub;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.effects.Effects;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.ArrayList;
import java.util.List;

public class HubEffects extends Effects {
	// HALLOWEEN
	private final List<Location> bloodDripLocations = new ArrayList<>() {{
		add(locTemp(183, 172, -1042));
		add(locTemp(184, 172, -1045));
		add(locTemp(178, 172, -1045));
		add(locTemp(179, 171, -1042));
		add(locTemp(178, 172, -1045));
		add(locTemp(177, 170, -1044));
		add(locTemp(179, 170, -1046));
		add(locTemp(185, 170, -1044));
		add(locTemp(184, 169, -1043));
		add(locTemp(182, 169, -1043));
		add(locTemp(180, 169, -1042));
		add(locTemp(179, 168, -1044));
		add(locTemp(182, 168, -1044));
	}};

	private final Location heartLocation = locTemp(181, 172, -1044);
	private final Location bloodLocation = locTemp(181, 170, -1044);

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

		// HALLOWEEN

		SoundBuilder heartbeat = new SoundBuilder("custom.misc.halloween.heartbeat").location(heartLocation).volume(2).pitch(0.1);
		ParticleBuilder heartParticles = new ParticleBuilder(Particle.CRIMSON_SPORE).location(heartLocation).offset(3, 2, 3).count(50).extra(0);
		Tasks.repeat(0, TickTime.SECOND.x(2.8), () -> {
			heartbeat.play();

			if (hasPlayersNearby(heartParticles.location(), 25))
				heartParticles.spawn();

		});

		SoundBuilder bloodGush = new SoundBuilder("custom.misc.halloween.blood").location(bloodLocation).volume(0.8).pitch(0.1);
		Tasks.repeat(0, TickTime.TICK.x(16), bloodGush::play);

		ParticleBuilder bloodDrip = new ParticleBuilder(Particle.FALLING_LAVA).extra(0.1).count(1);
		Tasks.repeat(0, TickTime.SECOND, () -> {
			for (Location location : bloodDripLocations) {
				if (location == null || !location.isChunkLoaded())
					continue;

				if (RandomUtils.chanceOf(25) && hasPlayersNearby(location, 25)) {
					int randomWait = RandomUtils.randomInt(1, 20);
					Tasks.wait(randomWait, () -> bloodDrip.location(location.toCenterLocation().add(0, 0.45, 0)).spawn());
				}
			}
		});
	}

	private Location locTemp(double x, double y, double z) {
		return new Location(Bukkit.getWorld("buildadmin"), x, y, z);
	}

}
