package gg.projecteden.nexus.features.events.y2024.pugmas24;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.effects.Effects;
import gg.projecteden.nexus.features.events.y2024.pugmas24.models.Geyser;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import org.bukkit.World;

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
	public void animations() {
		Tasks.repeat(0, TickTime.MINUTE.x(2), () -> {
			if (shouldAnimate(Geyser.geyserOrigin))
				return;

			if (RandomUtils.chanceOf(50))
				return;

			Geyser.animate();
		});
	}


}
