package gg.projecteden.nexus.features.hub;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.effects.Effects;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class HubEffects extends Effects {
	// HALLOWEEN
	private final List<Location> bloodDripLocations = new ArrayList<>() {{
		add(location(2, 122, 57));
		add(location(3, 122, 54));
		add(location(-3, 122, 54));
		add(location(-2, 121, 57));
		add(location(-4, 120, 55));
		add(location(-2, 120, 53));
		add(location(4, 120, 55));
		add(location(3, 119, 56));
		add(location(1, 119, 56));
		add(location(-1, 119, 57));
		add(location(-2, 118, 55));
		add(location(1, 118, 55));
	}};

	private final Location heartLocation = location(0, 123, 55);
	private final Location bloodLocation = location(0, 121, 55);

	@Override
	public @NotNull String getRegion() {
		return Hub.getBaseRegion();
	}

	@Override
	public void onEnterRegion(Player player) {
		PotionEffect unluck = new PotionEffectBuilder()
			.type(PotionEffectType.UNLUCK)
			.infinite()
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
				.location(location(100.5, 137.9, 39.5))
				.offset(0.15, 0, 0.15),

			new ParticleBuilder(Particle.SPLASH)
				.location(location(81.5, 130.3, 60.5))
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

		if (LocalDate.now().getMonth() == Month.OCTOBER) {
			// HALLOWEEN
			SoundBuilder heartbeat = new SoundBuilder("custom.misc.heartbeat").location(heartLocation).volume(2).pitch(0.1);
			ParticleBuilder heartParticles = new ParticleBuilder(Particle.CRIMSON_SPORE).location(heartLocation).offset(3, 2, 3).count(50).extra(0);
			Tasks.repeat(0, TickTime.SECOND.x(2.8), () -> {
				heartbeat.play();

				if (hasPlayersNearby(heartParticles.location(), 25))
					heartParticles.spawn();

			});

			SoundBuilder bloodGush = new SoundBuilder("custom.misc.blood").location(bloodLocation).volume(0.8).pitch(0.1);
			Tasks.repeat(0, TickTime.TICK.x(16), bloodGush::play);

			ParticleBuilder bloodDrip = new ParticleBuilder(Particle.FALLING_LAVA).extra(0.1).count(1);
			Tasks.repeat(0, TickTime.SECOND, () -> {
				if (!hasPlayersNearby(heartLocation, 25))
					return;

				for (Location location : bloodDripLocations) {
					if (location == null || !location.isChunkLoaded())
						continue;

					if (RandomUtils.chanceOf(25)) {
						Tasks.wait(RandomUtils.randomInt(1, 20), () -> bloodDrip.location(location.toCenterLocation().add(0, 0.45, 0)).spawn());
					}
				}
			});
		}
	}

}
