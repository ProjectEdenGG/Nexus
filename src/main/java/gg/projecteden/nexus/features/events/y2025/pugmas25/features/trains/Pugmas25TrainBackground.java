package gg.projecteden.nexus.features.events.y2025.pugmas25.features.trains;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.resourcepack.models.CustomSound;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldEditUtils;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Pugmas25TrainBackground {

	private static final Pugmas25 PUGMAS = Pugmas25.get();
	private static final WorldEditUtils worldedit = PUGMAS.worldedit();
	private static final String BASE_REGION = PUGMAS.getRegionName() + "_train_loop";
	private static final String SCHEM_PATH = "Animations/Train/Background/TerrainLoop_";
	public static final Location SCHEM_LOC = PUGMAS.location(-673, 92, -3233);
	private static final int MIN_TERRAIN_FRAME = 1;
	private static final int MAX_TERRAIN_FRAME = 74;

	@Getter
	private static boolean animating = false;
	private static int currentFrame = MIN_TERRAIN_FRAME;
	private static int taskId;

	public static void startup() {
		AtomicInteger ticks = new AtomicInteger();
		taskId = Tasks.repeat(0, TickTime.TICK.x(10), () -> {
			Set<Player> players = PUGMAS.getPlayersIn(BASE_REGION);
			if (players.isEmpty()) {
				ticks.set(0);
				animating = false;
				return;
			}

			animating = true;
			ticks.addAndGet(10);

			worldedit.paster().file(SCHEM_PATH + currentFrame).at(SCHEM_LOC).pasteAsync();

			if (ticks.get() % TickTime.SECOND.x(1) == 0) {
				for (Player player : players) {
					new SoundBuilder(CustomSound.TRAIN_CHUG)
						.receiver(player)
						.location(player)
						.category(SoundCategory.AMBIENT)
						.volume(0.8)
						.play();
				}
			}

			currentFrame++;
			if (currentFrame > MAX_TERRAIN_FRAME)
				currentFrame = MIN_TERRAIN_FRAME;
		});
	}

	public static void shutdown() {
		Tasks.cancel(taskId);
	}
}
