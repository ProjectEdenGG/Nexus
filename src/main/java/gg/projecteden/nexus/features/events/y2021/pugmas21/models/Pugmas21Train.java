package gg.projecteden.nexus.features.events.y2021.pugmas21.models;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2021.pugmas21.Pugmas21;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.block.BlockFace;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static gg.projecteden.nexus.utils.RandomUtils.randomInt;

public class Pugmas21Train {

	public static void schedule() {
		final Supplier<Long> delay = () -> TickTime.MINUTE.x(randomInt(5, 10));

		Tasks.wait(delay.get(), new AtomicReference<Runnable>() {{
			set(() -> {
				if (!Pugmas21.anyActivePlayers())
					return;

				getDefault().build().start();
				Pugmas21.actionBar("&c&lA train is passing by...", TickTime.SECOND.x(10));
				Tasks.wait(delay.get(), get());
			});
		}}.get());
	}

	public static gg.projecteden.nexus.features.events.models.Train.TrainBuilder getDefault() {
		return gg.projecteden.nexus.features.events.models.Train.builder()
			.location(Pugmas21.location(112.5, 54, 7.5, 90, 0))
			.direction(BlockFace.WEST)
			.seconds(60)
			.speed(.3)
			.regionAnnounce(Pugmas21.REGION)
			.regionTrack(Pugmas21.REGION + "_track")
			.test(false);
	}

}
