package gg.projecteden.nexus.features.events.y2024.pugmas24;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.effects.Effects;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.Geyser;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import org.bukkit.World;

import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
public class Pugmas24Effects extends Effects {

	@Override
	public World getWorld() {
		return Pugmas24.get().getWorld();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void particles() {
		Tasks.repeat(0, 10, Geyser::animateSmoke);

	}

	@Override
	public void animations() {
		AtomicInteger tries = new AtomicInteger(0);
		Tasks.repeat(0, TickTime.MINUTE.x(1), () -> {
			if (!shouldAnimate(Geyser.geyserOrigin))
				return;

			if (RandomUtils.chanceOf(50) && tries.get() < 5) {
				tries.getAndIncrement();
				return;
			}

			tries.set(0);
			Geyser.animate();
		});
	}


}
