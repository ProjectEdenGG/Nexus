package gg.projecteden.nexus.features.events.y2024.vulan24;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.nexus.features.effects.Effects;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class VuLan24Effects extends Effects {

	private List<Location> SMOKE_STACKS = new ArrayList<>();

	@Override
	public World getWorld() {
		return VuLan24.get().getWorld();
	}

	@Override
	public void onStart() {
		super.onStart();

		SMOKE_STACKS = List.of(
				location(28, 88, -6),
				location(32, 92, 6),
				location(40, 92, 29),
				location(23, 89, 29),
				location(131, 100, 104),
				location(171, 98, 94),
				location(165, 101, 90),
				location(180, 97, 69)
		);
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void sounds() {
		super.sounds();
	}

	@Override
	public void particles() {
		Tasks.repeat(0, 2, () -> {
			if (this.SMOKE_STACKS != null) {
				for (Location location : SMOKE_STACKS) {
					if (location == null || !location.isChunkLoaded())
						continue;

					if (RandomUtils.chanceOf(25))
						continue;

					if (hasPlayersNearby(location, 25))
						new ParticleBuilder(Particle.CAMPFIRE_COSY_SMOKE)
								.location(location.toCenterLocation())
								.offset(0, 4, 0)
								.extra(0.01)
								.count(0)
								.spawn();
				}
			}
		});

	}
}
