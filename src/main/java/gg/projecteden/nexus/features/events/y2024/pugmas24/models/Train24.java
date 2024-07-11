package gg.projecteden.nexus.features.events.y2024.pugmas24.models;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static gg.projecteden.nexus.utils.RandomUtils.randomInt;

@NoArgsConstructor
public class Train24 {

	private static final Set<Chunk> trainChunks = new HashSet<>();
	private static final String trainRevealRegion = Pugmas24.get().getRegionName() + "_train_reveal";
	private static final String trainTrackRegion = Pugmas24.get().getRegionName() + "_train_track";

	public static void startup() {
		for (Block block : Pugmas24.get().worldedit().getBlocks(Pugmas24.get().worldguard().getProtectedRegion(trainTrackRegion))) {
			trainChunks.add(block.getChunk());
		}

		for (Chunk chunk : trainChunks) {
			chunk.setForceLoaded(true);
		}
	}

	public static void shutdown() {
		for (Chunk chunk : trainChunks) {
			chunk.setForceLoaded(false);
		}
	}

	public static void start() {
		getDefault().build().start();
	}

	public static void schedule() {
		final Supplier<Long> delay = () -> TickTime.MINUTE.x(randomInt(5, 10));

		Tasks.wait(delay.get(), new AtomicReference<Runnable>() {{
			set(() -> {
				if (!Pugmas24.get().anyActivePlayers())
					return;

				getDefault().build().start();
				Pugmas24.get().actionBar("&c&lA train is passing by...", TickTime.SECOND.x(10));
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
				.regionReveal(trainRevealRegion);
	}
}
