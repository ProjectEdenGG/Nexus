package gg.projecteden.nexus.features.events.y2025.pugmas25.features.trains;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.models.Train;
import gg.projecteden.nexus.features.events.models.Train.Crossing;
import gg.projecteden.nexus.features.events.models.Train.Crossing.TrackSide;
import gg.projecteden.nexus.features.events.models.Train.TrainCrossings;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25.Pugmas25DeathCause;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import lombok.NoArgsConstructor;
import org.bukkit.block.BlockFace;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@NoArgsConstructor
public class Pugmas25Train {

	private static final String BASE_REGION = Pugmas25.get().getRegionName() + "_train_";
	private static final String trainRevealRegion = BASE_REGION + "reveal";
	private static final String trainTrackRegion = BASE_REGION + "track";

	public static void startup() {
		schedule();
	}

	public static void shutdown() {

	}

	public static void start() {
		getDefault().build().start();
	}

	public static void schedule() {
		final Supplier<Long> delay = () -> TickTime.MINUTE.x(RandomUtils.randomInt(10, 20));

		Tasks.wait(delay.get(), new AtomicReference<Runnable>() {{
			set(() -> {
				if (!Nexus.isMaintenanceQueued() && Pugmas25.get().anyActivePlayers()) {
					getDefault().build().start();
					Pugmas25.get().actionBarBroadcast("&c&lA train is passing by...", TickTime.SECOND.x(10));
				}

				Tasks.wait(delay.get(), get());
			});
		}}.get());
	}

	public static TrainCrossings trainCrossings = new TrainCrossings(
		new Crossing(Pugmas25.get().location(-634, 82, -2978), "pugmas25_train_track_lights_1", TrackSide.NORTH_SIDE),
		new Crossing(Pugmas25.get().location(-643, 82, -2966), "pugmas25_train_track_lights_1", TrackSide.SOUTH_SIDE),
		new Crossing(Pugmas25.get().location(-707, 82, -2978), "pugmas25_train_track_lights_2", TrackSide.NORTH_SIDE),
		new Crossing(Pugmas25.get().location(-717, 82, -2966), "pugmas25_train_track_lights_2", TrackSide.SOUTH_SIDE)
	);

	public static Train.TrainBuilder getDefault() {
		return Train.builder()
			.location(Pugmas25.get().location(-410.5, 84, -2971.5, 90, 0))
			.direction(BlockFace.WEST)
			.seconds(95)
			.speed(.3)
			.test(false)
			.regionTrack(trainTrackRegion)
			.regionAnnounce(Pugmas25.get().getRegionName())
			.regionAnnounceMuteRegex(Pugmas25.get().getRegionName() + "_train_mute_[0-9]+")
			.whistleLocation(Pugmas25.get().location(-426, 82, -2972))
			.whistleRadius(300)
			.regionReveal(trainRevealRegion)
			.trainCrossings(trainCrossings)
			.bonkPlayers(true)
			.onBonk((player) -> {
				if (player.getHealth() <= 2)
					Pugmas25.get().onDeath(player, Pugmas25DeathCause.TRAIN);
				else
					NMSUtils.hurtEntity(player, NMSUtils.getDamageSources(player).fall(), 4f);
			})
			.modelOverrides(new HashMap<>() {{
				put(3, ItemModelType.PUGMAS25_TRAIN_3);
				put(18, ItemModelType.PUGMAS25_TRAIN_18);
			}})
			.forceLoadRegion(trainTrackRegion)
			;
	}
}
