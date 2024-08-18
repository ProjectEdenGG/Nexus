package gg.projecteden.nexus.features.events.y2024.pugmas24.models;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import org.bukkit.block.BlockFace;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static gg.projecteden.nexus.utils.RandomUtils.randomInt;

@NoArgsConstructor
public class Pugmas24Train {

	private static final String BASE_REGION = Pugmas24.get().getRegionName() + "_train_";
	private static final String trainRevealRegion = BASE_REGION + "reveal";
	private static final String trainTrackRegion = BASE_REGION + "track";

	public static void startup() {
		Pugmas24.get().forceLoadRegions(trainTrackRegion);
		schedule();
	}

	public static void shutdown() {

	}

	public static void start() {
		getDefault().build().start();
	}

	public static void schedule() {
		final Supplier<Long> delay = () -> TickTime.MINUTE.x(randomInt(10, 20));

		Tasks.wait(delay.get(), new AtomicReference<Runnable>() {{
			set(() -> {
				if (!Pugmas24.get().anyActivePlayers())
					return;

				if (Nexus.isMaintenanceQueued())
					return;

				getDefault().build().start();
				Pugmas24.get().actionBarBroadcast("&c&lA train is passing by...", TickTime.SECOND.x(10));
				Tasks.wait(delay.get(), get());
			});
		}}.get());
	}

	public static gg.projecteden.nexus.features.events.models.Train.TrainBuilder getDefault() {
		return gg.projecteden.nexus.features.events.models.Train.builder()
				.location(Pugmas24.get().location(-503.5, 84, -2971.5, 90, 0))
				.direction(BlockFace.WEST)
				.seconds(80)
				.speed(.3)
				.test(false)
				.regionTrack(trainTrackRegion)
				.regionAnnounce(Pugmas24.get().getRegionName())
				.regionReveal(trainRevealRegion)
				.bonkPlayers(true);
	}
}
